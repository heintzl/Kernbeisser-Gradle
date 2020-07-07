package kernbeisser.Windows.Pay;

import kernbeisser.CustomComponents.ShoppingTable.ShoppingCartController;
import kernbeisser.DBEntities.Purchase;
import kernbeisser.DBEntities.SaleSession;
import kernbeisser.DBEntities.ShoppingItem;
import kernbeisser.Enums.Key;
import kernbeisser.Exeptions.AccessDeniedException;
import kernbeisser.Useful.Tools;
import kernbeisser.Windows.Controller;
import kernbeisser.Windows.Window;
import org.jetbrains.annotations.NotNull;

import javax.persistence.PersistenceException;
import java.util.List;

public class PayController implements Controller<PayView,PayModel> {

    private final PayModel model;
    private final PayView view;

    public PayController(SaleSession saleSession, List<ShoppingItem> shoppingCart,
                         Runnable transferCompleted) {
        model = new PayModel(saleSession, shoppingCart, transferCompleted);
        view = new PayView(new ShoppingCartController(saleSession.getCustomer().getUserGroup().getValue(),saleSession.getCustomer().getSolidaritySurcharge()));
    }

    void commitPayment(boolean printReceipt) {
        Purchase purchase;
        try {
            // FIXME why pass shoppingCart to model if it was initialized with it?

            try {
                purchase = model.pay(model.getSaleSession(), model.getShoppingCart(),
                                     model.shoppingCartSum());
                if (printReceipt) {model.print(purchase);}
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
        view.fillShoppingCart(model.getShoppingCart());
    }

    @Override
    public Key[] getRequiredKeys() {
        return new Key[0];
    }
}
