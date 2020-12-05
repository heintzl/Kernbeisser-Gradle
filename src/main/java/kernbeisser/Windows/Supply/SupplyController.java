package kernbeisser.Windows.Supply;

import javax.persistence.NoResultException;
import kernbeisser.DBEntities.Article;
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
      getView().getObjectForm().setSource(model.findBySuppliersItemNumber(supplier, supNr));
      last = supNr;
    } catch (NoResultException noResultException) {
      getView().noArticleFound();
    }
  }

  private void checkInput() throws CannotParseException {
    if (!model.articleExists(getView().getSuppliersItemNumber())) {
      throw new CannotParseException();
    }
  }

  public ShoppingItem addItem(double amount) throws CannotParseException {
    checkInput();
    Article article = getView().getObjectForm().getData();
    ShoppingItem item = new ShoppingItem(article, 0, true);
    item.setItemMultiplier((int) Math.round(amount * item.getContainerSize()));
    model.getShoppingItems().add(item);
    model.togglePrint(article);
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
