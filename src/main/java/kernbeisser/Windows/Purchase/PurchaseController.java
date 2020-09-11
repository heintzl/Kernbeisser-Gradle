package kernbeisser.Windows.Purchase;

import java.util.Collection;
import kernbeisser.DBEntities.Purchase;
import kernbeisser.DBEntities.ShoppingItem;
import kernbeisser.Enums.PermissionKey;
import kernbeisser.Useful.Date;
import kernbeisser.Windows.MVC.IController;
import org.jetbrains.annotations.NotNull;

public class PurchaseController implements IController<PurchaseView, PurchaseModel> {
  private final PurchaseModel model;
  private PurchaseView view;

  public PurchaseController(Purchase purchase) {
    model = new PurchaseModel(purchase);
  }

  double getPrice(ShoppingItem item) {
    return item.getRetailPrice();
  }

  @Override
  public @NotNull PurchaseModel getModel() {
    return model;
  }

  @Override
  public void fillUI() {
    view.setCustomer(model.getLoaded().getSession().getCustomer().getUsername());
    view.setSeller(model.getLoaded().getSession().getSeller().getUsername());
    view.setDate(Date.INSTANT_FORMAT.format(model.getLoaded().getCreateDate()));
    Collection<ShoppingItem> items = model.getAllItems();
    view.setItemCount(items.size());
    view.setSum(model.getSum());
    view.setItems(items);
  }

  @Override
  public PermissionKey[] getRequiredKeys() {
    return new PermissionKey[0];
  }
}
