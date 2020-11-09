package kernbeisser.Windows.Menu;

import javax.swing.*;
import kernbeisser.CustomComponents.ControllerButton;
import kernbeisser.DBEntities.User;
import kernbeisser.Enums.Mode;
import kernbeisser.Enums.TransactionType;
import kernbeisser.Exeptions.NotEnoughCreditException;
import kernbeisser.StartUp.LogIn.DBLogInController;
import kernbeisser.Windows.AccountingReports.AccountingReportsController;
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
import kernbeisser.Windows.MVC.Controller;
import kernbeisser.Windows.MVC.IView;
import kernbeisser.Windows.MVC.Linked;
import kernbeisser.Windows.ManagePriceLists.ManagePriceListsController;
import kernbeisser.Windows.PermissionManagement.PermissionController;
import kernbeisser.Windows.Setting.SettingController;
import kernbeisser.Windows.SoloShoppingMask.SoloShoppingMaskController;
import kernbeisser.Windows.SpecialPriceEditor.SpecialPriceEditorController;
import kernbeisser.Windows.SynchronizeArticles.SynchronizeArticleController;
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
  private ControllerButton accountingReports;
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
  private ControllerButton offerManagement;

  @Override
  public void initialize(MenuController controller) {}

  @Linked private MenuController controller;

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
            ManagePriceListsController::new, ManagePriceListsController.class, Controller::openTab);
    editArticles =
        new ControllerButton(
            EditItemsController::new, EditItemsController.class, Controller::openTab);
    editSurchargeTables =
        new ControllerButton(
            EditSurchargeTables::new, EditSurchargeTables.class, Controller::openTab);
    changePassword =
        new ControllerButton(
            () -> new ChangePasswordController(LogInModel.getLoggedIn(), true),
            ChangePasswordController.class,
            Controller::openTab);
    transactionHistory =
        new ControllerButton(
            () -> new UserInfoController(LogInModel.getLoggedIn()),
            UserInfoController.class,
            Controller::openTab);
    editOwnUser =
        new ControllerButton(
            () -> new EditUserController(LogInModel.getLoggedIn(), Mode.EDIT),
            EditUserController.class,
            Controller::openTab);
    // NOT IMPLEMENTED
    editUserSettings =
        new ControllerButton(
            () -> new EditUserSettingController(LogInModel.getLoggedIn()),
            EditUserSettingController.class,
            Controller::openTab);
    editUsers = new ControllerButton(EditUsers::new, EditUsers.class, Controller::openTab);
    doTransaction =
        new ControllerButton(
            () ->
                new TransactionController(LogInModel.getLoggedIn(), TransactionType.USER_GENERATED),
            TransactionController.class,
            Controller::openTab);
    changePermissions =
        new ControllerButton(
            PermissionController::new, PermissionController.class, Controller::openTab);
    // NOT IMPLEMENTED
    accountingReports =
        new ControllerButton(
            AccountingReportsController::new,
            AccountingReportsController.class,
            Controller::openTab);
    changeDBConnection =
        new ControllerButton(DBLogInController::new, DBLogInController.class, Controller::openTab);
    editJobs = new ControllerButton(EditJobs::new, EditJobs.class, Controller::openTab);
    editApplicationSettings =
        new ControllerButton(SettingController::new, SettingController.class, Controller::openTab);
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
              if (e != null) e.openTab();
            });
    addBeginner =
        new ControllerButton(
            () -> new EditUserController(User.generateBeginnerUser(), Mode.ADD),
            EditUserController.class,
            Controller::openTab);
    editSuppliers =
        new ControllerButton(EditSuppliers::new, EditSuppliers.class, Controller::openTab);

    editUserGroup =
        new ControllerButton(
            () -> new EditUserGroupController(LogInModel.getLoggedIn()),
            EditUserGroupController.class,
            Controller::openTab);
    synchoniseCatalog =
        new ControllerButton(
            SynchronizeArticleController::new,
            SynchronizeArticleController.class,
            Controller::openTab);
    offerManagement =
        new ControllerButton(SpecialPriceEditorController::new, SpecialPriceEditorController.class);

    openCashierShoppingMask.requestFocusInWindow();
  }

  @Override
  public String getTitle() {
    return "Menu";
  }
}
