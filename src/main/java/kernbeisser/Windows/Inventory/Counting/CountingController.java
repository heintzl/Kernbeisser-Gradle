package kernbeisser.Windows.Inventory.Counting;

import java.awt.event.KeyEvent;
import kernbeisser.CustomComponents.BarcodeCapture;
import kernbeisser.DBEntities.ArticleStock;
import kernbeisser.DBEntities.Articles;
import kernbeisser.DBEntities.Shelf;
import kernbeisser.Exeptions.PermissionKeyRequiredException;
import kernbeisser.Windows.MVC.Controller;
import kernbeisser.Windows.ShoppingMask.ArticleSelector.ArticleSelectorController;
import kernbeisser.Windows.ViewContainers.SubWindow;

public class CountingController extends Controller<CountingView, CountingModel> {
  public CountingController() throws PermissionKeyRequiredException {
    super(new CountingModel());
  }

  @Override
  public void fillView(CountingView countingView) {
    countingView.setShelves(model.getAllShelves());
    countingView.getSelectedShelf().ifPresent(this::loadShelf);
  }

  public void loadShelf(Shelf shelf) {
    getView().setArticleStocks(shelf.getArticleStocks());
    getView().selectFirst();
  }

  public void setStock(ArticleStock articleStock, double safeValue) {
    model.setStock(articleStock, safeValue);
    getView().getSelectedShelf().ifPresent(this::loadShelf);
  }

  public void addArticleStock() {
    new ArticleSelectorController(
            e ->
                getView()
                    .getSelectedShelf()
                    .ifPresent(
                        shelf -> {
                          model.addArticleToShelf(shelf, e);
                          loadShelf(shelf);
                        }))
        .openIn(new SubWindow(getView().traceViewContainer()));
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
