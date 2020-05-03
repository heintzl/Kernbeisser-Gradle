package kernbeisser.Windows.Pay;

import kernbeisser.DBEntities.Purchase;
import kernbeisser.DBEntities.Action;
import kernbeisser.DBEntities.SaleSession;
import kernbeisser.DBEntities.ShoppingItem;
import kernbeisser.Enums.Key;
import kernbeisser.Windows.Controller;
import kernbeisser.Windows.Window;
import org.jetbrains.annotations.NotNull;

import javax.persistence.PersistenceException;
import java.util.Collection;

public class PayController implements Controller<PayView,PayModel> {

    private PayModel model;
    private PayView view;

    public PayController(Window current, SaleSession saleSession, Collection<ShoppingItem> shoppingCart,
                         Runnable transferCompleted) {
        model = new PayModel(saleSession, shoppingCart, transferCompleted);
        view = new PayView(current, this);
    }

    void commitPayment() {
        Purchase purchase = null;
        try {
            purchase = model.pay(model.getSaleSession(), model.getShoppingCart(),
                                 model.shoppingCartSum());
            model.print(purchase, view.getSelectedPrintService());
            Action.logCurrentFunctionCall();
        } catch (PersistenceException e) {
            e.printStackTrace();
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
        view.setPrintServices(model.getAllPrinters());
        view.setSelectedPrintService(model.getDefaultPrinter());
    }

    @Override
    public Key[] getRequiredKeys() {
        return new Key[0];
    }
}
