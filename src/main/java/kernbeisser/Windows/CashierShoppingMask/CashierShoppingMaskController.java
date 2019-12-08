package kernbeisser.Windows.CashierShoppingMask;

import kernbeisser.DBEntitys.SaleSession;
import kernbeisser.DBEntitys.User;
import kernbeisser.Windows.Controller;
import kernbeisser.Windows.Model;
import kernbeisser.Windows.ShoppingMask.ShoppingMask;
import kernbeisser.Windows.View;

import java.util.List;

class CashierShoppingMaskController implements Controller {
    private CashierShoppingMaskModel model;
    private CashierShoppingMaskView view;
    CashierShoppingMaskController(User seller,CashierShoppingMaskView view){
        this.view=view;
        this.model=new CashierShoppingMaskModel(seller);
    }

    ShoppingMask startShoppingFor(User customer) throws NullPointerException{
        if(customer==null)throw new NullPointerException("No selected Object");
        SaleSession saleSession = new SaleSession();
        saleSession.setCustomer(customer);
        saleSession.setSeller(model.getSeller());
        ShoppingMask shoppingMask = new ShoppingMask(saleSession);
        model.getOpenSessions().add(shoppingMask);
        return shoppingMask;
    }

    List<User> getAllUser(){
        return User.getAll(null);
    }

    @Override
    public void refresh() {
    }

    @Override
    public CashierShoppingMaskView getView() {
        return view;
    }

    @Override
    public CashierShoppingMaskModel getModel() {
        return model;
    }
}
