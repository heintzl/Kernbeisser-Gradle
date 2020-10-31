package kernbeisser.Windows.SoloShoppingMask;

import kernbeisser.DBEntities.SaleSession;
import kernbeisser.Enums.PermissionKey;
import kernbeisser.Enums.SaleSessionType;
import kernbeisser.Exeptions.NotEnoughCreditException;
import kernbeisser.Windows.LogIn.LogInModel;
import kernbeisser.Windows.MVC.Controller;
import kernbeisser.Windows.MVC.Linked;
import kernbeisser.Windows.ShoppingMask.ShoppingMaskUIController;
import kernbeisser.Windows.ShoppingMask.ShoppingMaskUIView;
import org.jetbrains.annotations.NotNull;

public class SoloShoppingMaskController
    extends Controller<SoloShoppingMaskView, SoloShoppingMaskModel> {

  @Linked private final ShoppingMaskUIController shoppingMaskUIController;

  public SoloShoppingMaskController() throws NotEnoughCreditException {
    super(new SoloShoppingMaskModel());
    SaleSession saleSession = new SaleSession(SaleSessionType.SOLO);
    saleSession.setCustomer(LogInModel.getLoggedIn());
    saleSession.setSeller(LogInModel.getLoggedIn());
    this.shoppingMaskUIController = new ShoppingMaskUIController(saleSession);
  }

  public void processBarcode(String barcode) {
    shoppingMaskUIController.processBarcode(barcode);
  }

  @Override
  public void fillView(SoloShoppingMaskView soloShoppingMaskView) {}

  @Override
  public PermissionKey[] getRequiredKeys() {
    return new PermissionKey[0];
  }

  public ShoppingMaskUIView getShoppingMaskView() {
    return shoppingMaskUIController.getView();
  }
}
