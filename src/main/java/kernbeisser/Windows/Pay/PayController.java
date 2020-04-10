package kernbeisser.Windows.Pay;

import kernbeisser.DBEntities.SaleSession;
import kernbeisser.DBEntities.ShoppingItem;
import kernbeisser.Enums.Key;
import kernbeisser.Price.PriceCalculator;
import kernbeisser.Windows.Controller;
import kernbeisser.Windows.Window;
import org.jetbrains.annotations.NotNull;

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
        boolean paymentSuccessful = model.pay(model.getSaleSession(), model.getShoppingCart(), model.shoppingCartSum());
        if (paymentSuccessful) {
            model.print(view.getSelectedPrintService());
        }
    }

    double getPrice(ShoppingItem item) {
        return PriceCalculator.getShoppingItemPrice(item,
                                                    model.getSaleSession().getCustomer().getSolidaritySurcharge());
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
