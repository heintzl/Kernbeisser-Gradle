package kernbeisser.Windows.Pay;

import jakarta.persistence.PersistenceException;
import java.awt.*;
import java.util.List;
import kernbeisser.CustomComponents.ShoppingTable.ShoppingCartController;
import kernbeisser.DBEntities.SaleSession;
import kernbeisser.DBEntities.ShoppingItem;
import kernbeisser.DBEntities.User;
import kernbeisser.Enums.Setting;
import kernbeisser.Exeptions.InvalidTransactionException;
import kernbeisser.Exeptions.handler.UnexpectedExceptionHandler;
import kernbeisser.Windows.MVC.Controller;
import kernbeisser.Windows.MVC.Linked;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

public class PayController extends Controller<PayView, PayModel> {
  @Getter private final double userValue;

  @Linked private final ShoppingCartController cartController;

  public PayController(
      SaleSession saleSession, List<ShoppingItem> shoppingCart, Runnable transferCompleted) {
    super(new PayModel(saleSession, shoppingCart, transferCompleted));
    userValue = saleSession.getCustomer().getUserGroup().getValue();
    cartController =
        new ShoppingCartController(
            userValue, saleSession.getCustomer().getUserGroup().getSolidaritySurcharge(), false);
    if (userValue - shoppingCart.stream().mapToDouble(ShoppingItem::getRetailPrice).sum()
        < Setting.DEFAULT_MIN_VALUE.getDoubleValue()) {
      cartController.showUnderMinWarning();
      getView().setCommitPaymentWarning();
    }
  }

  void commitPayment(boolean printReceipt) {
    PayView view = getView();
    // FIXME why pass shoppingCart to model if it was initialized with it?
    try {
      long purchaseId = model.pay();
      if (printReceipt) {
        PayModel.print(purchaseId);
      }
      view.confirmLogging(
          model.getSaleSession().getCustomer().getFullName(), model.shoppingCartSum());
      view.back();

      model.runTransferCompleted();
    } catch (InvalidTransactionException e) {
      view.notEnoughValue();
    } catch (PersistenceException e) {
      UnexpectedExceptionHandler.showUnexpectedErrorWarning(e);
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
    PayView view = getView();
    view.fillShoppingCart(model.getShoppingCart());
    view.setCustomerStandard.setVisible(false);
    User customer = model.getSaleSession().getCustomer();
    String customerDisplayText = customer.getFirstName().charAt(0) + ". " + customer.getSurname();
    view.setCustomerStandard.setText("für " + customerDisplayText + " merken");
    view.setCustomerStandard.addActionListener(
        e -> {
          model.safeStandardPrint(view.printReceipt.isSelected());
          customerStandardVisibility(view);
        });
    view.printReceipt.setSelected(model.readStandardPrint());
    view.printReceipt.addActionListener(e -> customerStandardVisibility(view));
  }

  private void customerStandardVisibility(PayView view) {
    view.setCustomerStandard.setVisible(
        view.printReceipt.isSelected() != model.readStandardPrint());
  }
}
