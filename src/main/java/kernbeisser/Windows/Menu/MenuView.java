package kernbeisser.Windows.Menu;

import javax.swing.*;
import kernbeisser.CustomComponents.ControllerButton;
import kernbeisser.DBEntities.User;
import kernbeisser.Enums.Mode;
import kernbeisser.Exeptions.NotEnoughCreditException;
import kernbeisser.StartUp.LogIn.DBLogInController;
import kernbeisser.Windows.CashierShoppingMask.CashierShoppingMaskController;
import kernbeisser.Windows.ChangePassword.ChangePasswordController;
import kernbeisser.Windows.Container.ContainerController;
import kernbeisser.Windows.EditItems.EditItemsController;
import kernbeisser.Windows.EditJobs.EditJobs;
import kernbeisser.Windows.EditSuppliers.EditSuppliers;
import kernbeisser.Windows.EditSurchargeTables.EditSurchargeTables;
import kernbeisser.Windows.EditUser.EditUserController;
import kernbeisser.Windows.EditUserGroup.EditUserGroupController;
import kernbeisser.Windows.EditUserSetting.EditUserSettingController;
import kernbeisser.Windows.EditUsers.EditUsers;
import kernbeisser.Windows.LogIn.LogInModel;
import kernbeisser.Windows.MVC.IView;
import kernbeisser.Windows.ManagePriceLists.ManagePriceListsController;
import kernbeisser.Windows.PermissionManagement.PermissionController;
import kernbeisser.Windows.Setting.SettingController;
import kernbeisser.Windows.SoloShoppingMask.SoloShoppingMaskController;
import kernbeisser.Windows.SynchronizeArticles.SynchronizeArticleController;
import kernbeisser.Windows.Tillroll.TillrollController;
import kernbeisser.Windows.Trasaction.TransactionController;
import kernbeisser.Windows.UserInfo.UserInfoController;
import kernbeisser.Windows.UserInfo.UserInfoView;
import org.jetbrains.annotations.NotNull;

public class MenuView implements IView<MenuController> {

  private UserInfoView infoPanel;
  private JPanel main;
  private kernbeisser.CustomComponents.ControllerButton openCashierShoppingMask;
  private kernbeisser.CustomComponents.ControllerButton printBonFromPast;
  private ControllerButton editPriceList;
  private ControllerButton editArticles;
  private ControllerButton editSurchargeTables;
  private kernbeisser.CustomComponents.ControllerButton changePassword;
  private kernbeisser.CustomComponents.ControllerButton transactionHistory;
  private kernbeisser.CustomComponents.ControllerButton editOwnUser;
  private kernbeisser.CustomComponents.ControllerButton editUserSettings;
  private ControllerButton editUsers;
  private ControllerButton doTransaction;
  private ControllerButton changePermissions;
  private ControllerButton tillrollControllerButton;
  private ControllerButton changeDBConnection;
  private ControllerButton editApplicationSettings;
  private ControllerButton order;
  private ControllerButton placeHolderControllerButton1;
  private ControllerButton placeHolderControllerButton2;
  private ControllerButton openSelfShoppingMask;
  private ControllerButton addBeginner;
  private ControllerButton editJobs;
  private ControllerButton editSuppliers;
  private ControllerButton editUserGroup;
  private ControllerButton synchoniseCatalog;

  @Override
  public void initialize(MenuController controller) {}

  @Override
  public @NotNull JComponent getContent() {
    return main;
  }

  private void createUIComponents() {
    infoPanel = new UserInfoController(LogInModel.getLoggedIn()).getView();
    openCashierShoppingMask =
        new ControllerButton(
            CashierShoppingMaskController::new, CashierShoppingMaskController.class);
    // NOT IMPLEMENTED
    printBonFromPast = ControllerButton.empty();
    editPriceList =
        new ControllerButton(
            ManagePriceListsController::new,
            ManagePriceListsController.class,
            controller -> controller.openTab("Preislisten bearbeiten"));
    editArticles =
        new ControllerButton(
            EditItemsController::new,
            EditItemsController.class,
            controller -> controller.openTab("Artikel bearbeiten"));
    editSurchargeTables =
        new ControllerButton(
            EditSurchargeTables::new,
            EditSurchargeTables.class,
            controller -> controller.openTab("Zuschlagstabellen bearbeiten"));
    changePassword =
        new ControllerButton(
            () -> new ChangePasswordController(LogInModel.getLoggedIn(), true),
            ChangePasswordController.class,
            controller -> controller.openTab("Passwort"));
    transactionHistory =
        new ControllerButton(
            () -> new UserInfoController(LogInModel.getLoggedIn()),
            UserInfoController.class,
            controller -> controller.openTab(""));
    editOwnUser =
        new ControllerButton(
            () -> new EditUserController(LogInModel.getLoggedIn(), Mode.EDIT),
            EditUserController.class,
            controller -> controller.openTab("Persönliche Information"));
    // NOT IMPLEMENTED
    editUserSettings =
        new ControllerButton(
            () -> new EditUserSettingController(LogInModel.getLoggedIn()),
            EditUserSettingController.class,
            controller -> controller.openTab("<PlaceHolder>"));
    editUsers =
        new ControllerButton(
            EditUsers::new,
            EditUsers.class,
            controller -> controller.openTab("Benutzer bearbeiten"));
    doTransaction =
        new ControllerButton(
            () -> new TransactionController(LogInModel.getLoggedIn()),
            TransactionController.class,
            controller -> controller.openTab("Überweisungen"));
    changePermissions =
        new ControllerButton(
            PermissionController::new,
            PermissionController.class,
            controller -> controller.openTab("Berechtigungen"));
    // NOT IMPLEMENTED
    tillrollControllerButton =
        new ControllerButton(
            TillrollController::new,
            TillrollController.class,
            controller -> controller.openTab("Bonrolle"));
    changeDBConnection =
        new ControllerButton(
            DBLogInController::new,
            DBLogInController.class,
            controller -> controller.openTab("Datenbankverbindung"));
    editJobs =
        new ControllerButton(
            EditJobs::new, EditJobs.class, controller -> controller.openTab("Jobs bearbeiten"));
    editApplicationSettings =
        new ControllerButton(
            SettingController::new, SettingController.class, e -> e.openTab("Einstellungen"));
    order =
        new ControllerButton(
            () -> new ContainerController(LogInModel.getLoggedIn()), ContainerController.class);
    // NOT IMPLEMENTED
    placeHolderControllerButton1 = ControllerButton.empty();
    // NOT IMPLEMENTED
    placeHolderControllerButton2 = ControllerButton.empty();
    openSelfShoppingMask =
        new ControllerButton(
            () -> {
              try {
                return new SoloShoppingMaskController();
              } catch (NotEnoughCreditException e) {
                JOptionPane.showMessageDialog(
                    getTopComponent(),
                    "sie können keinen Einkauf beginnen, da ihr Guthaben nicht ausreicht.\nFalls sie ihr Guthaben aufladen wollen, melden sie sich bitte bei dem Ladendienst,\ndieser wird sie dann an die / den Guthaben beauftragte/n verweissen.");
                return null;
              }
            },
            SoloShoppingMaskController.class,
            e -> {
              if (e != null) e.openTab("Selbsteinkauf");
            });
    addBeginner =
        new ControllerButton(
            () -> new EditUserController(User.generateBeginnerUser(), Mode.ADD),
            EditUserController.class,
            e -> e.openTab("Probemitglied aufnehmen"));
    editSuppliers =
        new ControllerButton(
            EditSuppliers::new,
            EditSuppliers.class,
            controller -> controller.openTab("Lieferanten bearbeiten"));

    editUserGroup =
        new ControllerButton(
            () -> new EditUserGroupController(LogInModel.getLoggedIn()),
            EditUserGroupController.class,
            controller -> controller.openTab("Nutzergruppe ändern"));
    synchoniseCatalog =
        new ControllerButton(
            SynchronizeArticleController::new,
            SynchronizeArticleController.class,
            controller -> controller.openTab("Katalog sychonisieren"));
    openCashierShoppingMask.requestFocusInWindow();
  }
}
