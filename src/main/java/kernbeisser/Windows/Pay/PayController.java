package kernbeisser.Windows.Pay;

import kernbeisser.DBEntities.SaleSession;
import kernbeisser.DBEntities.ShoppingItem;
import kernbeisser.Price.PriceCalculator;
import kernbeisser.Windows.Controller;
import kernbeisser.Windows.Model;
import kernbeisser.Windows.Window;

import java.util.Collection;

public class PayController implements Controller {

    private PayModel model;
    private PayView view;

    public PayController(Window current, SaleSession saleSession, Collection<ShoppingItem> shoppingCart,
                         Runnable transferCompleted) {
        model = new PayModel(saleSession, shoppingCart, transferCompleted);
        view = new PayView(current, this);
        view.fillShoppingCart(model.getShoppingCart());
        view.setPrintServices(model.getAllPrinters());
        view.setSelectedPrintService(model.getDefaultPrinter());
    }

    void commitPayment() {
        boolean paymentSuccessful = model.pay(model.getSaleSession(), model.getShoppingCart(), model.shoppingCartSum());
        if (paymentSuccessful) {
            model.print(view.getSelectedPrintService());
        }
    }

    private boolean checkBon() {
        model.print(model.getDefaultPrinter());
        return false;
    }

    void commit() {
        commitPayment();
        checkBon();
    }

    double getPrice(ShoppingItem item) {
        return PriceCalculator.getShoppingItemPrice(item,
                                                    model.getSaleSession().getCustomer().getSolidaritySurcharge());
    }

    @Override
    public PayView getView() {
        return view;
    }

    @Override
    public Model getModel() {
        return model;
    }
}
