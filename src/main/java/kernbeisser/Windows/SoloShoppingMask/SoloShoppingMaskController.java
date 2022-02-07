package kernbeisser.Windows.SoloShoppingMask;

import kernbeisser.DBEntities.SaleSession;
import kernbeisser.DBEntities.User;
import kernbeisser.Enums.PermissionKey;
import kernbeisser.Enums.SaleSessionType;
import kernbeisser.Enums.Setting;
import kernbeisser.Exeptions.MissingFullMemberException;
import kernbeisser.Exeptions.NotEnoughCreditException;
import kernbeisser.Security.Key;
import kernbeisser.Windows.LogIn.LogInModel;
import kernbeisser.Windows.MVC.Controller;
import kernbeisser.Windows.MVC.Linked;
import kernbeisser.Windows.ShoppingMask.ShoppingMaskUIController;
import kernbeisser.Windows.ShoppingMask.ShoppingMaskUIView;

public class SoloShoppingMaskController
    extends Controller<SoloShoppingMaskView, SoloShoppingMaskModel> {

  @Linked private final ShoppingMaskUIController shoppingMaskUIController;

  @Key(PermissionKey.ACTION_OPEN_SOLO_SHOPPING_MASK)
  public SoloShoppingMaskController() throws NotEnoughCreditException, MissingFullMemberException {
    super(new SoloShoppingMaskModel());
    SaleSession saleSession = new SaleSession(SaleSessionType.SOLO);
    User user = LogInModel.getLoggedInFromDB();
    user.validateGroupMemberships(
        "Du darfst nicht selbst Einkaufen, weil weder du, noch  die Mitglieder deiner Benutzergruppe "
            + Setting.STORE_NAME.getStringValue()
            + "-Mitglied sind.");
    saleSession.setCustomer(user);
    saleSession.setSeller(user);
    this.shoppingMaskUIController = new ShoppingMaskUIController(saleSession);
  }

  public void processBarcode(String barcode) {
    shoppingMaskUIController.processBarcode(barcode);
  }

  @Override
  public void fillView(SoloShoppingMaskView soloShoppingMaskView) {}

  public ShoppingMaskUIView getShoppingMaskView() {
    return shoppingMaskUIController.getView();
  }
}
