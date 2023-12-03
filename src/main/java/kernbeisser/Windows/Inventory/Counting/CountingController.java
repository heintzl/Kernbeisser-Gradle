package kernbeisser.Windows.Inventory.Counting;

import java.awt.event.KeyEvent;
import java.time.LocalDate;
import java.time.chrono.ChronoLocalDate;
import kernbeisser.CustomComponents.BarcodeCapture;
import kernbeisser.DBEntities.ArticleStock;
import kernbeisser.DBEntities.Articles;
import kernbeisser.DBEntities.Shelf;
import kernbeisser.Enums.Setting;
import kernbeisser.Exeptions.PermissionKeyRequiredException;
import kernbeisser.Useful.Date;
import kernbeisser.Windows.MVC.Controller;
import kernbeisser.Windows.ShoppingMask.ArticleSelector.ArticleSelectorController;
import kernbeisser.Windows.ViewContainers.SubWindow;

public class CountingController extends Controller<CountingView, CountingModel> {
  Setting inventoryScheduledDate = Setting.INVENTORY_SCHEDULED_DATE;
  private final Runnable runOnClose;

  public CountingController(Runnable runOnClose) throws PermissionKeyRequiredException {
    super(new CountingModel());
    this.runOnClose = runOnClose;
  }

  public CountingController withShelf(Shelf shelf) {
    if (shelf != null) {
      getView().setSelectedShelf(shelf);
    }
    return this;
  }

  public void runOnClose() {
    runOnClose.run();
  }

  @Override
  public void fillView(CountingView countingView) {
    countingView.setShelves(model.getAllShelves());
    countingView.getSelectedShelf().ifPresent(this::loadShelf);
    setInventoryDateMessage(countingView);
  }

  void setInventoryDateMessage(CountingView countingView) {
    LocalDate inventoryDate = inventoryScheduledDate.getDateValue();
    boolean asWarning =
        inventoryDate.isBefore(ChronoLocalDate.from(LocalDate.now().atStartOfDay()));
    String inventoryDateString = createInventoryDateString(asWarning);
    countingView.setInventoryDate(inventoryDateString);
    if (asWarning) {
      countingView.formatInventoryDateAsWarning();
    } else {
      countingView.formatInventoryDateAsMessage();
    }
  }

  String createInventoryDateString(boolean asWarning) {
    String dateString = Date.INSTANT_DATE.format(inventoryScheduledDate.getDateValue());
    if (asWarning) {
      return String.format(
          "Achtung: Das Inventur-Datum '%s' liegt in der Vergangenheit!", dateString);
    }
    return String.format("Inventur-Datum: %s", dateString);
  }

  public void loadShelf(Shelf shelf) {
    getView().setArticleStocks(shelf.getArticleStocks());
    getView().selectFirst();
  }

  public void setStock(ArticleStock articleStock, double safeValue) {
    model.setStock(articleStock, safeValue);
    articleStock.setCounted(safeValue);
    getView().refreshArticleStock(articleStock);
  }

  public void addArticleStock() {
    CountingView view = getView();
    new ArticleSelectorController(
            e ->
                view.getSelectedShelf()
                    .ifPresent(
                        shelf -> {
                          model.addArticleToShelf(shelf, e);
                          loadShelf(shelf);
                          view.selectArticle(e);
                        }))
        .openIn(new SubWindow(view.traceViewContainer()));
  }

  private final BarcodeCapture capture =
      new BarcodeCapture(
          b ->
              Articles.getByBarcode(Long.parseLong(b))
                  .ifPresent(
                      a ->
                          getView()
                              .getSelectedShelf()
                              .ifPresent(s -> model.addArticleToShelf(s, a))));

  @Override
  protected boolean processKeyboardInput(KeyEvent e) {
    return capture.processKeyEvent(e);
  }
}
