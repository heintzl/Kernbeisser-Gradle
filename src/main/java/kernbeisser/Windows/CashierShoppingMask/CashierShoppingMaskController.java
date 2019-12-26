package kernbeisser.Windows.CashierShoppingMask;

import kernbeisser.DBEntitys.SaleSession;
import kernbeisser.DBEntitys.User;
import kernbeisser.Windows.Controller;
import kernbeisser.Windows.ShoppingMask.ShoppingMaskView;

class CashierShoppingMaskController implements Controller {
    private CashierShoppingMaskModel model;
    private CashierShoppingMaskView view;
    CashierShoppingMaskController(User seller,CashierShoppingMaskView view){
        this.view=view;
        this.model=new CashierShoppingMaskModel(seller);
        view.setUsers(model.getAllUser());
    }

    void startShoppingFor(User customer) throws NullPointerException{
        if(customer==null)throw new NullPointerException("No selected Object");
        SaleSession saleSession = new SaleSession();
        saleSession.setCustomer(customer);
        saleSession.setSeller(model.getSeller());
        ShoppingMaskView shoppingMask = new ShoppingMaskView(view,saleSession);
        view.openShoppingMask(shoppingMask);
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
