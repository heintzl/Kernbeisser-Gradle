package kernbeisser.Windows.Purchase;

import java.util.Collection;
import kernbeisser.CustomComponents.ShoppingTable.ShoppingCartController;
import kernbeisser.DBEntities.Purchase;
import kernbeisser.DBEntities.ShoppingItem;
import kernbeisser.Enums.PermissionKey;
import kernbeisser.Useful.Date;
import kernbeisser.Windows.MVC.IController;
import kernbeisser.Windows.MVC.Linked;
import kernbeisser.Windows.Pay.PayModel;
import org.jetbrains.annotations.NotNull;

public class PurchaseController implements IController<PurchaseView, PurchaseModel> {
  private final PurchaseModel model;
  private PurchaseView view;
  @Linked private final ShoppingCartController cartController;

  public PurchaseController(Purchase purchase) {
    model = new PurchaseModel(purchase);
    cartController = new ShoppingCartController(0, purchase.getUserSurcharge(), false);
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
    fillShoppingCart();
    cartController.setValueAfterLabel("Damaliges Guthaben nach dem Einkauf:");
  }

  @Override
  public PermissionKey[] getRequiredKeys() {
    return new PermissionKey[0];
  }

  public void printBon() {
    PayModel.print(model.getLoaded(), model.getLoaded().getAllItems());
  }

  public void fillShoppingCart() {
    double sum = 0;
    Collection<ShoppingItem> items = model.getAllItems();
    for (ShoppingItem item : items) {
      sum += item.getRetailPrice();
    }
    cartController.getView().setSum(sum);
    cartController
        .getView()
        .setValue(
            model.getLoaded().getSession().getCustomer().valueAt(model.getLoaded().getCreateDate())
                - sum);
    cartController.getView().setObjects(items);
  }
}
