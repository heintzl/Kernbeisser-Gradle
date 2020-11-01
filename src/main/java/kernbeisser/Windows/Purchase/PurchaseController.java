package kernbeisser.Windows.Purchase;

import java.util.Collection;
import kernbeisser.CustomComponents.ShoppingTable.ShoppingCartController;
import kernbeisser.DBEntities.Purchase;
import kernbeisser.DBEntities.ShoppingItem;
import kernbeisser.Enums.PermissionKey;
import kernbeisser.Useful.Date;
import kernbeisser.Windows.MVC.Controller;
import kernbeisser.Windows.MVC.Linked;
import kernbeisser.Windows.Pay.PayModel;
import lombok.var;
import org.jetbrains.annotations.NotNull;

public class PurchaseController extends Controller<PurchaseView, PurchaseModel> {
  @Linked private final ShoppingCartController cartController;

  public PurchaseController(Purchase purchase) {
    super(new PurchaseModel(purchase));
    cartController = new ShoppingCartController(0, purchase.getUserSurcharge(), false);
  }

  @Override
  public @NotNull PurchaseModel getModel() {
    return model;
  }

  @Override
  public void fillView(PurchaseView purchaseView) {
    var view = getView();
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
    var view = cartController.getView();
    double sum = 0;
    Collection<ShoppingItem> items = model.getAllItems();
    for (ShoppingItem item : items) {
      sum += item.getRetailPrice();
    }
    view.setSum(sum);
    view.setValue(
        model.getLoaded().getSession().getCustomer().valueAt(model.getLoaded().getCreateDate())
            - sum);
    view.setObjects(items);
  }
}
