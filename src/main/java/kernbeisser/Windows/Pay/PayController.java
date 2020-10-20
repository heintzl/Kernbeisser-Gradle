package kernbeisser.Windows.Pay;

import java.awt.*;
import java.util.List;
import javax.persistence.PersistenceException;
import kernbeisser.CustomComponents.ShoppingTable.ShoppingCartController;
import kernbeisser.DBEntities.Purchase;
import kernbeisser.DBEntities.SaleSession;
import kernbeisser.DBEntities.ShoppingItem;
import kernbeisser.Enums.PermissionKey;
import kernbeisser.Exeptions.InvalidTransactionException;
import kernbeisser.Useful.Tools;
import kernbeisser.Windows.MVC.IController;
import kernbeisser.Windows.MVC.Linked;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

public class PayController implements IController<PayView, PayModel> {

  private final PayModel model;
  private PayView view;
  private final Dimension viewSize;
  @Getter private final double userValue;

  @Linked private final ShoppingCartController cartController;

  public PayController(
      SaleSession saleSession,
      List<ShoppingItem> shoppingCart,
      Runnable transferCompleted,
      Dimension windowSize) {
    userValue = saleSession.getCustomer().getUserGroup().getValue();
    cartController =
        new ShoppingCartController(
            userValue, saleSession.getCustomer().getUserGroup().getSolidaritySurcharge(), false);
    model = new PayModel(saleSession, shoppingCart, transferCompleted);
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
          model.print(purchase, model.getShoppingCart());
        }
        view.back();
        model.runTransferCompleted();
      } catch (InvalidTransactionException e) {
        view.notEnoughValue();
      }

    } catch (PersistenceException e) {
      Tools.showUnexpectedErrorWarning(e);
    }
  }

  @Override
  public boolean commitClose() {
    model.removeSolidarityItems();
    return true;
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
