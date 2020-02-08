package kernbeisser.Windows.Pay;

import kernbeisser.DBEntities.SaleSession;
import kernbeisser.DBEntities.ShoppingItem;
import kernbeisser.Windows.Controller;
import kernbeisser.Windows.Model;
import kernbeisser.Windows.View;
import kernbeisser.Windows.Window;

import java.util.Collection;

public class PayController implements Controller {

    private PayModel model;
    private PayView view;

    public PayController(Window current, SaleSession saleSession, Collection<ShoppingItem> shoppingCart, Runnable transferCompleted){
        model=new PayModel(saleSession,shoppingCart,transferCompleted);
        view = new PayView(current,this);
        view.fillShoppingCart(model.getShoppingCart());
        view.setPrintServices(model.getAllPrinters());
        view.setSelectedPrintService(model.getDefaultPrinter());
    }
    private boolean commitPay(){
        return model.pay(model.getSaleSession(),model.getShoppingCart(),model.shoppingCartSum());
    }
    private boolean checkBon(){
        return false;
    }
    void commit() {
        commitPay();
        checkBon();
    }

    @Override
    public View getView() {
        return view;
    }

    @Override
    public Model getModel() {
        return model;
    }
}
