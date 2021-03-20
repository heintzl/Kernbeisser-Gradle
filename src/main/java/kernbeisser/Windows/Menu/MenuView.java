package kernbeisser.Windows.Menu;

import jiconfont.icons.font_awesome.FontAwesome;
import jiconfont.swing.IconFontSwing;
import kernbeisser.CustomComponents.ControllerButton;
import kernbeisser.Enums.PermissionKey;
import kernbeisser.Enums.Setting;
import kernbeisser.Enums.TransactionType;
import kernbeisser.Exeptions.NotEnoughCreditException;
import kernbeisser.Exeptions.PermissionKeyRequiredException;
import kernbeisser.Forms.FormEditor.FormEditorController;
import kernbeisser.StartUp.LogIn.DBLogInController;
import kernbeisser.Windows.AccountingReports.AccountingReportsController;
import kernbeisser.Windows.AdminTools.AdminToolController;
import kernbeisser.Windows.CashierShoppingMask.CashierShoppingMaskController;
import kernbeisser.Windows.ChangePassword.ChangePasswordController;
import kernbeisser.Windows.EditArticles.EditItemsController;
import kernbeisser.Windows.EditJobs.EditJobs;
import kernbeisser.Windows.EditSuppliers.EditSuppliers;
import kernbeisser.Windows.EditSurchargeGroups.EditSurchargeGroupController;
import kernbeisser.Windows.EditUserGroup.EditUserGroupController;
import kernbeisser.Windows.EditUserSetting.EditUserSettingController;
import kernbeisser.Windows.EditUsers.EditUsers;
import kernbeisser.Windows.LogIn.LogInModel;
import kernbeisser.Windows.MVC.Controller;
import kernbeisser.Windows.MVC.IView;
import kernbeisser.Windows.MVC.Linked;
import kernbeisser.Windows.ManagePriceLists.ManagePriceListsController;
import kernbeisser.Windows.PermissionManagement.PermissionController;
import kernbeisser.Windows.PreOrder.PreOrderController;
import kernbeisser.Windows.Setting.SettingController;
import kernbeisser.Windows.SoloShoppingMask.SoloShoppingMaskController;
import kernbeisser.Windows.SpecialPriceEditor.SpecialPriceEditorController;
import kernbeisser.Windows.Supply.SupplyController;
import kernbeisser.Windows.SynchronizeArticles.SynchronizeArticleController;
import kernbeisser.Windows.Trasaction.TransactionController;
import kernbeisser.Windows.UserInfo.UserInfoController;
import kernbeisser.Windows.UserInfo.UserInfoView;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;

public class MenuView implements IView<MenuController> {

  private JPanel main;
  private UserInfoView infoPanel;
  private JPanel menuPanel;
  private kernbeisser.CustomComponents.ControllerButton openCashierShoppingMask;
  private ControllerButton editPriceList;
  private ControllerButton editArticles;
  private ControllerButton editSurchargeTables;
  private kernbeisser.CustomComponents.ControllerButton changePassword;
  private kernbeisser.CustomComponents.ControllerButton transactionHistory;
  private kernbeisser.CustomComponents.ControllerButton editOwnUser;
  private kernbeisser.CustomComponents.ControllerButton editUserSettings;
  private ControllerButton editUsers;
  private ControllerButton doTransactionPayIn;
  private ControllerButton changePermissions;
  private ControllerButton accountingReports;
  private ControllerButton changeDBConnection;
  private ControllerButton editApplicationSettings;
  private ControllerButton order;
  private ControllerButton adminTools;
  private ControllerButton placeHolderControllerButton2;
  private ControllerButton openSelfShoppingMask;
  private ControllerButton addBeginner;
  private ControllerButton editJobs;
  private ControllerButton editSuppliers;
  private ControllerButton editUserGroup;
  private ControllerButton synchoniseCatalog;
  private ControllerButton offerManagement;
  private ControllerButton supply;
  private ControllerButton doUserDefiniedTransaction;
  private JButton logout;

  @Override
  public void initialize(MenuController controller) {
    logout.setIcon(
        IconFontSwing.buildIcon(
            FontAwesome.POWER_OFF,
            25 * Setting.LABEL_SCALE_FACTOR.getFloatValue(),
            new Color(182, 46, 4)));
    logout.addActionListener(e -> back());
  }

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
    editPriceList =
        new ControllerButton(
            ManagePriceListsController::new, ManagePriceListsController.class, Controller::openTab);
    editArticles =
        new ControllerButton(
            EditItemsController::new, EditItemsController.class, Controller::openTab);
    editSurchargeTables =
        new ControllerButton(EditSurchargeGroupController::new, EditSurchargeGroupController.class);
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
            controller::generateEditOwnUserController,
            FormEditorController.class,
            Controller::openTab,
            false,
            new PermissionKey[] {PermissionKey.ACTION_EDIT_OWN_DATA});
    editUserSettings =
        new ControllerButton(
            () -> new EditUserSettingController(LogInModel.getLoggedIn()),
            EditUserSettingController.class,
            Controller::openTab);
    editUsers = new ControllerButton(EditUsers::new, EditUsers.class, Controller::openTab);
    doTransactionPayIn =
        new ControllerButton(
            () -> new TransactionController(LogInModel.getLoggedIn(), TransactionType.PAYIN),
            TransactionController.class,
            Controller::openTab,
            false,
            new PermissionKey[] {PermissionKey.ACTION_TRANSACTION_FROM_KB});
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
        new ControllerButton(
            DBLogInController::new,
            DBLogInController.class,
            Controller::openTab,
            false,
            new PermissionKey[] {PermissionKey.ACTION_OPEN_DB_LOG_IN});
    editJobs = new ControllerButton(EditJobs::new, EditJobs.class, Controller::openTab);
    editApplicationSettings =
        new ControllerButton(SettingController::new, SettingController.class, Controller::openTab);
    order = new ControllerButton(PreOrderController::new, PreOrderController.class);
    // NOT IMPLEMENTED
    adminTools = new ControllerButton(AdminToolController::new, AdminToolController.class);
    // NOT IMPLEMENTED
    placeHolderControllerButton2 = ControllerButton.empty();
    openSelfShoppingMask =
        new ControllerButton(
            () -> {
              try {
                return new SoloShoppingMaskController();
              } catch (NotEnoughCreditException e) {
                openSelfShoppingMask.setToolTipText(
                    "Du kannst keinen Einkauf beginnen, da dein Guthaben nicht ausreicht.\n"
                        + "Falls du dein Guthaben aufladen möchtest, melde dich bitte beim Ladendienst,\n"
                        + "dieser wird dich dann an die/den Guthabenbeauftragte/n verweisen.");
                throw new PermissionKeyRequiredException();
              }
            },
            SoloShoppingMaskController.class,
            e -> {
              if (e != null) e.openTab();
            });
    addBeginner =
        new ControllerButton(
            controller::generateAddBeginnerForm,
            FormEditorController.class,
            Controller::openTab,
            false,
            new PermissionKey[] {PermissionKey.ACTION_ADD_BEGINNER});
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
    supply = new ControllerButton(SupplyController::new, SupplyController.class);
    doUserDefiniedTransaction =
        new ControllerButton(
            () ->
                new TransactionController(LogInModel.getLoggedIn(), TransactionType.USER_GENERATED),
            TransactionController.class);
  }

  @Override
  public Component getFocusOnInitialize() {
    return openSelfShoppingMask;
  }

  @Override
  public String getTitle() {
    return "Menü";
  }
}
