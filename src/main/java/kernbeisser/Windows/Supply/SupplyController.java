package kernbeisser.Windows.Supply;

import javax.persistence.NoResultException;
import kernbeisser.DBEntities.ShoppingItem;
import kernbeisser.DBEntities.Supplier;
import kernbeisser.Windows.MVC.Controller;

public class SupplyController extends Controller<SupplyView, SupplyModel> {

  public SupplyController() {
    super(new SupplyModel());
  }

  @Override
  public void fillView(SupplyView supplyView) {
    getView().setSuppliers(model.getAllSuppliers());
  }

  void searchShoppingItem(Supplier supplier, int supNr) {
    try {
      getView().loadItem(model.getViaSuppliersItemNumber(supplier, supNr));
    } catch (NoResultException noResultException) {
      getView().loadItem(null);
    }
  }

  public ShoppingItem addItem(Supplier selected, int suppliersItemNumber, double amount) {
    ShoppingItem item = model.getViaSuppliersItemNumber(selected, suppliersItemNumber);
    item.setItemMultiplier((int) Math.round(amount * item.getContainerSize()));
    model.getShoppingItems().add(item);
    return item;
  }
}
