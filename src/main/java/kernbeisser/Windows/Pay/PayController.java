package kernbeisser.Windows.Pay;

import kernbeisser.DBEntitys.SaleSession;
import kernbeisser.DBEntitys.ShoppingItem;
import kernbeisser.Windows.Controller;
import kernbeisser.Windows.Model;
import kernbeisser.Windows.View;

import java.util.Collection;

class PayController implements Controller {

    private PayModel model;
    private PayView view;

    PayController(PayView view, SaleSession saleSession, Collection<ShoppingItem> shoppingCart,Runnable transferCompleted){
        this.view=view;
        model=new PayModel(saleSession,shoppingCart,transferCompleted);
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
