package kernbeisser.Windows.SoloShoppingMask;

import kernbeisser.DBEntities.SaleSession;
import kernbeisser.Enums.PermissionKey;
import kernbeisser.Windows.Controller;
import kernbeisser.Windows.LogIn.LogInModel;
import kernbeisser.Windows.ShoppingMask.ShoppingMaskUIController;
import kernbeisser.Windows.ShoppingMask.ShoppingMaskUIView;
import org.jetbrains.annotations.NotNull;

public class SoloShoppingMaskController implements Controller<SoloShoppingMaskView,SoloShoppingMaskModel> {

    private SoloShoppingMaskView view;

    private SoloShoppingMaskModel model;

    private ShoppingMaskUIController shoppingMaskUIController;

    public SoloShoppingMaskController(){
        SaleSession saleSession = new SaleSession();
        saleSession.setCustomer(LogInModel.getLoggedIn());
        saleSession.setSeller(LogInModel.getLoggedIn());
        this.shoppingMaskUIController = new ShoppingMaskUIController(saleSession);
        shoppingMaskUIController.initView();
        this.model = new SoloShoppingMaskModel();
        this.view = new SoloShoppingMaskView(this);
    }

    public void processBarcode(String barcode) {
        shoppingMaskUIController.processBarcode(barcode);
    }

    @Override
    public @NotNull SoloShoppingMaskView getView() {
        return view;
    }

    @Override
    public @NotNull SoloShoppingMaskModel getModel() {
        return model;
    }

    @Override
    public void fillUI() {

    }

    @Override
    public PermissionKey[] getRequiredKeys() {
        return new PermissionKey[0];
    }

    public ShoppingMaskUIView getShoppingMaskView() {
        return shoppingMaskUIController.getView();
    }
}
