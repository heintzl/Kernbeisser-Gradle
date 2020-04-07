package kernbeisser.Windows.SoloShoppingMask;

import kernbeisser.DBEntities.SaleSession;
import kernbeisser.Windows.Controller;
import kernbeisser.Windows.LogIn.LogInModel;
import kernbeisser.Windows.Model;
import kernbeisser.Windows.ShoppingMask.ShoppingMaskUIController;
import kernbeisser.Windows.ShoppingMask.ShoppingMaskUIView;
import kernbeisser.Windows.View;
import kernbeisser.Windows.Window;

public class SoloShoppingMaskController implements Controller {

    private SoloShoppingMaskView view;

    private SoloShoppingMaskModel model;

    private ShoppingMaskUIController shoppingMaskUIController;

    public SoloShoppingMaskController(Window current){
        SaleSession saleSession = new SaleSession();
        saleSession.setCustomer(LogInModel.getLoggedIn());
        saleSession.setSeller(LogInModel.getLoggedIn());
        this.shoppingMaskUIController = new ShoppingMaskUIController(saleSession);
        this.model = new SoloShoppingMaskModel();
        this.view = new SoloShoppingMaskView(current,this);
    }

    @Override
    public View getView() {
        return view;
    }

    @Override
    public Model getModel() {
        return model;
    }

    public ShoppingMaskUIView getShoppingMaskView() {
        return shoppingMaskUIController.getView();
    }
}
