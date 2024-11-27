package kernbeisser.Windows.Inventory.Counting;

import java.awt.event.KeyEvent;
import java.time.LocalDate;
import java.time.chrono.ChronoLocalDate;
import java.util.Collection;
import java.util.concurrent.ExecutionException;
import javax.swing.*;
import kernbeisser.CustomComponents.BarcodeCapture;
import kernbeisser.DBEntities.Article;
import kernbeisser.DBEntities.ArticleStock;
import kernbeisser.DBEntities.Repositories.ArticleRepository;
import kernbeisser.DBEntities.Shelf;
import kernbeisser.Enums.Setting;
import kernbeisser.Exeptions.handler.UnexpectedExceptionHandler;
import kernbeisser.Useful.Date;
import kernbeisser.Windows.MVC.Controller;
import kernbeisser.Windows.ShoppingMask.ArticleSelector.ArticleSelectorController;
import kernbeisser.Windows.ViewContainers.SubWindow;
import rs.groump.AccessDeniedException;

public class CountingController extends Controller<CountingView, CountingModel> {
  Setting inventoryScheduledDate = Setting.INVENTORY_SCHEDULED_DATE;
  private final Runnable runOnClose;

  public CountingController(Runnable runOnClose) throws AccessDeniedException {
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

  String getCountingUnit(ArticleStock stock) {
    Article article = stock.getArticle();
    return article.isWeighable() ? article.getMetricUnits().getShortName() : "Stk.";
  }

  public void loadShelf(Shelf shelf) {
    SwingWorker stockLoaderWorker =
        new SwingWorker<Collection<ArticleStock>, Void>() {
          @Override
          protected Collection<ArticleStock> doInBackground() throws Exception {
            return shelf.getArticleStocks();
          }

          @Override
          protected void done() {
            try {
              getView().setArticleStocks(get());
            } catch (InterruptedException | ExecutionException e) {
              UnexpectedExceptionHandler.showUnexpectedErrorWarning(e.getCause());
              ;
            }
          }
        };
    stockLoaderWorker.execute();
    getView().selectFirst();
  }

  public boolean setStock(ArticleStock articleStock, double safeValue) {
    CountingView view = getView();
    Article article = articleStock.getArticle();
    if (article.isWeighable()) {
      if (safeValue < Setting.INVENTORY_MIN_THRESHOLD_WEIGHABLE.getFloatValue()) {
        if (!view.confirmLowWeighableAmountWarning(safeValue)) {
          return false;
        }
      }
    } else if (safeValue > Setting.INVENTORY_MAX_THRESHOLD_PIECE.getFloatValue()) {
      if (!view.confirmHighPieceAmountWarning(safeValue)) {
        return false;
      }
    }
    model.setStock(articleStock, safeValue);
    articleStock.setCounted(safeValue);
    getView().refreshArticleStock(articleStock);
    return true;
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
              ArticleRepository.getByBarcode(Long.parseLong(b))
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
