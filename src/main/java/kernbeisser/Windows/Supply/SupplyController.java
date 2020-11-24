package kernbeisser.Windows.Supply;

import java.util.Collection;
import javax.persistence.NoResultException;
import kernbeisser.DBEntities.Article;
import kernbeisser.DBEntities.ArticleBase;
import kernbeisser.DBEntities.ShoppingItem;
import kernbeisser.DBEntities.Supplier;
import kernbeisser.Enums.Mode;
import kernbeisser.Exeptions.CannotParseException;
import kernbeisser.Windows.MVC.Controller;

public class SupplyController extends Controller<SupplyView, SupplyModel> {

  public SupplyController() {
    super(new SupplyModel());
  }

  @Override
  public void fillView(SupplyView supplyView) {
    getView().setSuppliers(model.getAllSuppliers());
  }

  private int last;

  void searchShoppingItem(Supplier supplier, int supNr) {
    if (supNr == 0 || last == supNr) return;
    try {
      ArticleBase ab = select(model.findBySuppliersItemNumber(supplier, supNr));
      getView().getObjectForm().setSource(ab);
      last = supNr;
    } catch (NoResultException noResultException) {
      getView().noArticleFound();
    }
  }

  private ArticleBase select(Collection<ArticleBase> articleBases) {
    if (articleBases.size() == 0) throw new NoResultException();
    for (ArticleBase base : articleBases) {
      if (model.isArticle(base)) return base;
    }
    return articleBases.iterator().next();
  }

  private ArticleBase obtainInput() throws CannotParseException {
    if (!model.articleExists(getView().getSuppliersItemNumber())) throw new NoResultException();
    return getView().getObjectForm().getData();
  }

  public ShoppingItem addItem(double amount) throws CannotParseException {
    ArticleBase ab = obtainInput();
    if (!model.isArticle(ab)) {
      Article article = model.findNextTo(ab);
      article.setKbNumber(model.getNextUnusedKBNumber(article.getKbNumber()));
      getView().verifyArticleAutofill(article, model.getAllPriceLists());
      ab =
          model.persistArticleBase(
              ab, article.isWeighable(), article.getPriceList(), article.getKbNumber());
    }
    ShoppingItem item = new ShoppingItem(ab, 0, true);
    item.setItemMultiplier((int) Math.round(amount * item.getContainerSize()));
    model.getShoppingItems().add(item);
    model.togglePrint(ab);
    getView().getObjectForm().applyMode(Mode.EDIT);
    return item;
  }

  void commit() {
    model.commit();
    model.getShoppingItems().clear();
    getView().success();
    getView().back();
  }

  @Override
  protected boolean commitClose() {
    if (getView().shouldPrintLabels()) model.print();
    return model.getShoppingItems().size() == 0 || getView().commitClose();
  }

  public void remove(ShoppingItem selectedObject) {
    model.getShoppingItems().remove(selectedObject);
  }

  public void togglePrint(ShoppingItem t) {
    model.togglePrint(t.extractArticleBySupplierNumber());
    getView().repaintTable();
  }

  public boolean becomePrinted(ShoppingItem e) {
    return model.becomePrinted(e.extractArticleBySupplierNumber());
  }
}
