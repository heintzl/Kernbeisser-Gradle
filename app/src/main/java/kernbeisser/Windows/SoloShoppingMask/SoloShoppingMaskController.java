package kernbeisser.Windows.SoloShoppingMask;

import kernbeisser.DBEntities.SaleSession;
import kernbeisser.DBEntities.User;
import kernbeisser.Enums.SaleSessionType;
import kernbeisser.Enums.Setting;
import kernbeisser.Exeptions.MissingFullMemberException;
import kernbeisser.Exeptions.NotEnoughCreditException;
import kernbeisser.Windows.LogIn.LogInModel;
import kernbeisser.Windows.MVC.Controller;
import kernbeisser.Windows.MVC.Linked;
import kernbeisser.Windows.ShoppingMask.ShoppingMaskController;
import kernbeisser.Windows.ShoppingMask.ShoppingMaskView;
import rs.groump.Key;
import rs.groump.PermissionKey;

public class SoloShoppingMaskController
    extends Controller<SoloShoppingMaskView, SoloShoppingMaskModel> {

  @Linked private final ShoppingMaskController shoppingMaskController;

  @Key(PermissionKey.ACTION_OPEN_SOLO_SHOPPING_MASK)
  public SoloShoppingMaskController() throws NotEnoughCreditException, MissingFullMemberException {
    super(new SoloShoppingMaskModel());
    SaleSession saleSession = new SaleSession(SaleSessionType.SOLO);
    User user = LogInModel.getLoggedIn();
    if (!user.isFullMember() && user.getUserGroup().getMembers().size() == 1) {
      throw new MissingFullMemberException(
          "Du darfst nicht selbst einkaufen, weil du kein "
              + Setting.STORE_NAME.getStringValue()
              + "-Mitglied bist.");
    }
    user.validateGroupMemberships(
        "Du darfst nicht selbst einkaufen, weil weder du, noch die Mitglieder deiner Benutzergruppe "
            + Setting.STORE_NAME.getStringValue()
            + "-Mitglied sind.");
    saleSession.setCustomer(user);
    saleSession.setSeller(user);
    this.shoppingMaskController = new ShoppingMaskController(saleSession);
  }

  public void processBarcode(String barcode) {
    shoppingMaskController.processBarcode(barcode);
  }

  @Override
  public void fillView(SoloShoppingMaskView soloShoppingMaskView) {}

  public ShoppingMaskView getShoppingMaskView() {
    return shoppingMaskController.getView();
  }
}
