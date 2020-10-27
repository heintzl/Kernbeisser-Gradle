package kernbeisser.Windows.SoloShoppingMask;

import kernbeisser.DBEntities.SaleSession;
import kernbeisser.Enums.PermissionKey;
import kernbeisser.Enums.SaleSessionType;
import kernbeisser.Exeptions.NotEnoughCreditException;
import kernbeisser.Windows.LogIn.LogInModel;
import kernbeisser.Windows.MVC.IController;
import kernbeisser.Windows.MVC.Linked;
import kernbeisser.Windows.ShoppingMask.ShoppingMaskUIController;
import kernbeisser.Windows.ShoppingMask.ShoppingMaskUIView;
import org.jetbrains.annotations.NotNull;

public class SoloShoppingMaskController
    implements IController<SoloShoppingMaskView, SoloShoppingMaskModel> {

  private SoloShoppingMaskView view;

  private final SoloShoppingMaskModel model;

  @Linked private final ShoppingMaskUIController shoppingMaskUIController;

  public SoloShoppingMaskController() throws NotEnoughCreditException {
    SaleSession saleSession = new SaleSession(SaleSessionType.SOLO);
    saleSession.setCustomer(LogInModel.getLoggedIn());
    saleSession.setSeller(LogInModel.getLoggedIn());
    this.shoppingMaskUIController = new ShoppingMaskUIController(saleSession);
    this.model = new SoloShoppingMaskModel();
  }

  public void processBarcode(String barcode) {
    shoppingMaskUIController.processBarcode(barcode);
  }

  @Override
  public @NotNull SoloShoppingMaskModel getModel() {
    return model;
  }

  @Override
  public void fillUI() {}

  @Override
  public PermissionKey[] getRequiredKeys() {
    return new PermissionKey[0];
  }

  public ShoppingMaskUIView getShoppingMaskView() {
    return shoppingMaskUIController.getView();
  }
}
