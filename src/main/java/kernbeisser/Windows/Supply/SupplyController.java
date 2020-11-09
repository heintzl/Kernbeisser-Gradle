package kernbeisser.Windows.Supply;

import javax.persistence.NoResultException;
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
      ArticleBase ab = getView().select(model.getViaSuppliersItemNumber(supplier, supNr));
      if (ab != null) getView().getObjectForm().setSource(ab);
      else getView().noArticleFound();
      last = supNr;
    } catch (NoResultException noResultException) {
      getView().noArticleFound();
    }
  }

  public ShoppingItem addItem(double amount) throws CannotParseException {
    ArticleBase ab = getView().getObjectForm().getData();
    if (ab == null) throw new NoResultException();
    ShoppingItem item = new ShoppingItem(ab, 0, true);
    item.setItemMultiplier((int) Math.round(amount * item.getContainerSize()));
    model.getShoppingItems().add(item);
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
    return model.getShoppingItems().size() == 0 || getView().commitClose();
  }

  public void remove(ShoppingItem selectedObject) {
    model.getShoppingItems().remove(selectedObject);
  }
}
