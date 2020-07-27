package kernbeisser.Windows.Pay;

import java.awt.*;
import java.util.List;
import javax.persistence.PersistenceException;
import kernbeisser.CustomComponents.ShoppingTable.ShoppingCartController;
import kernbeisser.DBEntities.Purchase;
import kernbeisser.DBEntities.SaleSession;
import kernbeisser.DBEntities.ShoppingItem;
import kernbeisser.Enums.PermissionKey;
import kernbeisser.Exeptions.AccessDeniedException;
import kernbeisser.Useful.Tools;
import kernbeisser.Windows.Controller;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

public class PayController implements Controller<PayView, PayModel> {

  private final PayModel model;
  private final PayView view;
  private final Dimension viewSize;
  @Getter private final double userValue;

  public PayController(
      SaleSession saleSession,
      List<ShoppingItem> shoppingCart,
      Runnable transferCompleted,
      Dimension windowSize) {
    userValue = saleSession.getCustomer().getUserGroup().getValue();
    model = new PayModel(saleSession, shoppingCart, transferCompleted);
    view =
        new PayView(
            new ShoppingCartController(
                userValue, saleSession.getCustomer().getSolidaritySurcharge(), false));
    this.viewSize = windowSize;
  }

  void commitPayment(boolean printReceipt) {
    Purchase purchase;
    try {
      // FIXME why pass shoppingCart to model if it was initialized with it?

      try {
        purchase =
            model.pay(model.getSaleSession(), model.getShoppingCart(), model.shoppingCartSum());
        if (printReceipt) {
          model.print(purchase);
        }
      } catch (AccessDeniedException e) {
        view.notEnoughValue();
      }

    } catch (PersistenceException e) {
      Tools.showUnexpectedErrorWarning(e);
    }
  }

  double getPrice(ShoppingItem item) {
    return item.getItemRetailPrice();
  }

  @Override
  public @NotNull PayView getView() {
    return view;
  }

  @Override
  public @NotNull PayModel getModel() {
    return model;
  }

  @Override
  public void fillUI() {
    view.setViewSize(viewSize);
    view.fillShoppingCart(model.getShoppingCart());
  }

  @Override
  public PermissionKey[] getRequiredKeys() {
    return new PermissionKey[0];
  }
}
