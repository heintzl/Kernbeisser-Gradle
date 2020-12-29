package kernbeisser.Windows.Pay;

import java.awt.*;
import java.util.List;
import javax.persistence.PersistenceException;
import kernbeisser.CustomComponents.ShoppingTable.ShoppingCartController;
import kernbeisser.DBEntities.Purchase;
import kernbeisser.DBEntities.SaleSession;
import kernbeisser.DBEntities.ShoppingItem;
import kernbeisser.DBEntities.User;
import kernbeisser.Enums.PermissionKey;
import kernbeisser.Exeptions.InvalidTransactionException;
import kernbeisser.Useful.Tools;
import kernbeisser.Windows.MVC.Controller;
import kernbeisser.Windows.MVC.Linked;
import lombok.Getter;
import lombok.var;
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
    var view = getView();
    try {
      // FIXME why pass shoppingCart to model if it was initialized with it?
      try {
        purchase = model.pay();
        if (printReceipt) {
          PayModel.print(purchase);
        }
        view.confirmLogging(
            model.getSaleSession().getCustomer().getFullName(), model.shoppingCartSum());
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
  public void fillView(PayView payView) {
    var view = getView();
    view.setViewSize(viewSize);
    view.fillShoppingCart(model.getShoppingCart());
    view.setCustomerStandard.setVisible(false);
    User customer = model.getSaleSession().getCustomer();
    String customerDisplayText = customer.getFirstName().substring(0,1) + ". " + customer.getSurname();
    view.setCustomerStandard.setText("für " + customerDisplayText + " merken");
    view.setCustomerStandard.addActionListener(e -> {
      model.safeStandardPrint(view.printReceipt.isSelected());
      customerStandardVisibility(view);
    });
    view.printReceipt.setSelected(model.readStandardPrint());
    view.printReceipt.addActionListener(e -> customerStandardVisibility(view));

  }

  private void customerStandardVisibility(PayView view) {
    view.setCustomerStandard.setVisible(view.printReceipt.isSelected() != model.readStandardPrint());
  }


}
