package kernbeisser.Windows.CashierShoppingMask;

import kernbeisser.SaleSession;
import kernbeisser.User;
import kernbeisser.Windows.ShoppingMask.ShoppingMask;

class CashierShoppingMaskController {
    private User seller;
    CashierShoppingMaskController(User seller){
        this.seller=seller;
    }
    ShoppingMask startShoppingFor(User customer) throws NullPointerException{
        if(customer==null)throw new NullPointerException("No selected Object");
        SaleSession saleSession = new SaleSession();
        saleSession.setCustomer(customer);
        saleSession.setSeller(seller);
        return new ShoppingMask(saleSession);
    }
}
