package kernbeisser.Windows.EditPriceList;

import java.awt.*;
import javax.swing.*;
import kernbeisser.DBEntities.Article;
import kernbeisser.DBEntities.PriceList;
import kernbeisser.Exeptions.PermissionKeyRequiredException;
import kernbeisser.Security.Key;
import kernbeisser.Windows.CollectionView.CollectionController;
import kernbeisser.Windows.CollectionView.CollectionView;
import kernbeisser.Windows.MVC.Controller;
import kernbeisser.Windows.MVC.Linked;
import rs.groump.PermissionKey;

public class EditPriceListController extends Controller<EditPriceListView, EditPriceListModel> {

  @Linked private final CollectionController<Article> articles;

  @Key(PermissionKey.ACTION_OPEN_MANAGE_PRICE_LISTS)
  public EditPriceListController(PriceList priceList) throws PermissionKeyRequiredException {
    super(new EditPriceListModel(priceList));
    articles = EditPriceListModel.getArticleSource();
  }

  public PriceList getPricelist() {
    return model.getPriceList();
  }

  @Override
  public void fillView(EditPriceListView editPriceListView) {
    articles.setLoadedAndSource(model.getPriceList().getAllArticles(), model::getAllArticles);
    articles.getView().addSearchbox(CollectionView.BOTH);
  }

  @Override
  protected boolean commitClose() {
    return model.persistChanges(
        articles.getModel().getLoaded(), (i, j) -> getView().confirmChanges(i, j));
  }
}
