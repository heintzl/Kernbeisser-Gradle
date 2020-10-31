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
import kernbeisser.Windows.MVC.Controller;
import kernbeisser.Windows.MVC.Linked;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

public class PayController extends Controller<PayView, PayModel> {
  private final Dimension viewSize;
  @Getter private final double userValue;

  @Linked private final ShoppingCartController cartController;

  public PayController(
      SaleSession saleSession,
      List<ShoppingItem> shoppingCart,
      Runnable transferCompleted,
      Dimension windowSize) {
    super(new PayModel(saleSession, shoppingCart, transferCompleted));
    userValue = saleSession.getCustomer().getUserGroup().getValue();
    cartController =
        new ShoppingCartController(
            userValue, saleSession.getCustomer().getUserGroup().getSolidaritySurcharge(), false);
    this.viewSize = windowSize;
  }

  void commitPayment(boolean printReceipt) {
    Purchase purchase;
    try {
      // FIXME why pass shoppingCart to model if it was initialized with it?

      try {
        purchase = model.pay();
        if (printReceipt) {
          PayModel.print(purchase, model.getShoppingCart());
        }
        getView()
            .confirmLogging(
                model.getSaleSession().getCustomer().getFullName(), model.shoppingCartSum());
        getView().back();

        model.runTransferCompleted();
      } catch (InvalidTransactionException e) {
        getView().notEnoughValue();
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
  public void fillView(PayView payView) {
    getView().setViewSize(viewSize);
    getView().fillShoppingCart(model.getShoppingCart());
  }

  @Override
  public PermissionKey[] getRequiredKeys() {
    return new PermissionKey[0];
  }
}
