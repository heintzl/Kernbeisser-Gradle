package kernbeisser.Windows.Menu;

import java.awt.*;
import java.util.concurrent.CancellationException;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import jiconfont.icons.font_awesome.FontAwesome;
import jiconfont.swing.IconFontSwing;
import kernbeisser.CustomComponents.ControllerButton;
import kernbeisser.DBEntities.User;
import kernbeisser.Enums.Setting;
import kernbeisser.Enums.TransactionType;
import kernbeisser.Exeptions.MissingFullMemberException;
import kernbeisser.Exeptions.NotEnoughCreditException;
import kernbeisser.Forms.FormEditor.FormEditorController;
import kernbeisser.StartUp.LogIn.DBLogInController;
import kernbeisser.Useful.Tools;
import kernbeisser.Windows.AccountingReports.AccountingReportsController;
import kernbeisser.Windows.CashierShoppingMask.CashierShoppingMaskController;
import kernbeisser.Windows.ChangePassword.ChangePasswordController;
import kernbeisser.Windows.EditArticles.EditItemsController;
import kernbeisser.Windows.EditJobs.EditJobs;
import kernbeisser.Windows.EditSuppliers.EditSuppliers;
import kernbeisser.Windows.EditSurchargeGroups.EditSurchargeGroupController;
import kernbeisser.Windows.EditUserGroup.EditUserGroupController;
import kernbeisser.Windows.EditUserSetting.EditUserSettingController;
import kernbeisser.Windows.EditUsers.EditUsers;
import kernbeisser.Windows.InfoPanel.InfoPanelController;
import kernbeisser.Windows.InfoPanel.InfoPanelView;
import kernbeisser.Windows.Inventory.InventoryController;
import kernbeisser.Windows.LogIn.LogInModel;
import kernbeisser.Windows.MVC.Controller;
import kernbeisser.Windows.MVC.IView;
import kernbeisser.Windows.MVC.Linked;
import kernbeisser.Windows.ManagePriceLists.ManagePriceListsController;
import kernbeisser.Windows.PermissionAssignment.PermissionAssignmentController;
import kernbeisser.Windows.PermissionManagement.PermissionController;
import kernbeisser.Windows.PreOrder.PreOrderController;
import kernbeisser.Windows.Setting.SettingController;
import kernbeisser.Windows.SoloShoppingMask.SoloShoppingMaskController;
import kernbeisser.Windows.Supply.SupplyController;
import kernbeisser.Windows.SynchronizeArticles.SynchronizeArticleController;
import kernbeisser.Windows.TabbedPane.TabbedPaneModel;
import kernbeisser.Windows.Transaction.TransactionController;
import kernbeisser.Windows.UserInfo.UserInfoController;
import kernbeisser.Windows.ViewContainers.SubWindow;
import org.jetbrains.annotations.NotNull;

public class MenuView implements IView<MenuController> {

  private JPanel main;
  private InfoPanelView infoPanel;
  private JPanel menuPanel;
  private ControllerButton openCashierShoppingMask;
  private ControllerButton editPriceList;
  private ControllerButton editArticles;
  private ControllerButton editSurchargeTables;
  private ControllerButton changePassword;
  private ControllerButton openSelfPreorder;
  private ControllerButton showUserInfo;
  private ControllerButton editUserSettings;
  private ControllerButton editUsers;
  private ControllerButton doTransactionPayIn;
  private ControllerButton changePermissions;
  private ControllerButton accountingReports;
  private ControllerButton changeDBConnection;
  private ControllerButton editApplicationSettings;
  private ControllerButton order;
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
  private ControllerButton permissionAssignment;
  private JButton grantCashierRole;
  private JButton beginnInventory;
  private JPanel menugroupMyAccount;
  private ControllerButton internalTransaction;

  @Override
  public void initialize(MenuController controller) {
    ((Frame) TabbedPaneModel.getMainPanel().getContainer())
        .setTitle(
            Setting.STORE_NAME.getStringValue()
                + " ("
                + LogInModel.getLoggedIn().getFullName()
                + ")");
    logout.setIcon(
        IconFontSwing.buildIcon(
            FontAwesome.POWER_OFF,
            25 * Setting.LABEL_SCALE_FACTOR.getFloatValue(),
            new Color(182, 46, 4)));
    logout.addActionListener(e -> back());
    logout.setToolTipText(LogInModel.getLoggedIn().getFullName() + " vom Programm abmelden");
    TitledBorder myAccountBorder = (TitledBorder) menugroupMyAccount.getBorder();
    myAccountBorder.setTitle("Mein " + Setting.STORE_NAME.getStringValue() + "-Konto");
  }

  @Linked private MenuController controller;

  @Override
  public @NotNull JComponent getContent() {
    return main;
  }

  private boolean inheritsFullMembership() {
    User user = LogInModel.getLoggedIn();
    if (LogInModel.getLoggedIn().isFullMember()) return true;
    if (user.getUserGroup().getMembers().size() == 1) return false;
    try {
      user.validateGroupMemberships("");
      return true;
    } catch (MissingFullMemberException e) {
      return false;
    }
  }

  private void createUIComponents() {
    boolean inheritsFullMembership = inheritsFullMembership();
    infoPanel = new InfoPanelController().getView();
    openCashierShoppingMask =
        new ControllerButton(
            CashierShoppingMaskController::new,
            CashierShoppingMaskController.class,
            "Willst Du mit dem Ladendienst beginnen?");
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
            c -> c.openIn(new SubWindow(traceViewContainer())).getLoaded());
    showUserInfo =
        new ControllerButton(
            () -> new UserInfoController(LogInModel.getLoggedIn()),
            UserInfoController.class,
            c -> c.openIn(new SubWindow(traceViewContainer())).getLoaded(),
            false);
    editUserSettings =
        new ControllerButton(
            () -> new EditUserSettingController(LogInModel.getLoggedIn()),
            EditUserSettingController.class,
            Controller::openTab);
    editUsers = new ControllerButton(EditUsers::new, EditUsers.class, Controller::openTab);
    doTransactionPayIn =
        new ControllerButton(
            controller::getPayInTransactionController,
            TransactionController.class,
            Controller::openTab,
            false);
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
            controller::getDBLoginController, DBLogInController.class, Controller::openTab, false);
    editJobs = new ControllerButton(EditJobs::new, EditJobs.class, Controller::openTab);
    editApplicationSettings =
        new ControllerButton(SettingController::new, SettingController.class, Controller::openTab);
    order =
        new ControllerButton(
            controller::getPreorderController,
            PreOrderController.class,
            Controller::openTab,
            false);
    openSelfPreorder =
        new ControllerButton(
            controller::getOwnPreorderController,
            PreOrderController.class,
            Controller::openTab,
            false);
    if (openSelfPreorder.isEnabled()) openSelfPreorder.setEnabled(inheritsFullMembership);
    // NOT IMPLEMENTED
    placeHolderControllerButton2 = ControllerButton.empty();
    placeHolderControllerButton2.setVisible(false);
    openSelfShoppingMask =
        new ControllerButton(
            () -> {
              try {
                return new SoloShoppingMaskController();
              } catch (NotEnoughCreditException e) {
                Tools.beep();
                JOptionPane.showMessageDialog(
                    getContent(),
                    "Du kannst keinen Einkauf beginnen, da dein Guthaben nicht ausreicht.\n"
                        + "Falls du dein Guthaben aufladen möchtest, melde dich bitte beim Ladendienst,\n"
                        + "dieser wird dich dann an die/den Guthabenbeauftragte/n verweisen.",
                    "Nicht genug Guthaben",
                    JOptionPane.WARNING_MESSAGE);
                throw new CancellationException();
              } catch (MissingFullMemberException f) {
                Tools.beep();
                JOptionPane.showMessageDialog(
                    getContent(),
                    f.getMessage(),
                    "keine Berechtigung",
                    JOptionPane.WARNING_MESSAGE);
                throw new CancellationException();
              }
            },
            SoloShoppingMaskController.class,
            e -> {
              if (e != null) {
                e.openTab();
              }
            });
    if (openSelfShoppingMask.isEnabled()) {
      openSelfShoppingMask.setEnabled(inheritsFullMembership);
    }
    addBeginner =
        new ControllerButton(
            controller::generateAddBeginnerForm,
            FormEditorController.class,
            Controller::openTab,
            false);
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
    offerManagement = ControllerButton.empty();
    supply = new ControllerButton(SupplyController::new, SupplyController.class);
    doUserDefiniedTransaction =
        new ControllerButton(
            () ->
                new TransactionController(LogInModel.getLoggedIn(), TransactionType.USER_GENERATED),
            TransactionController.class);
    internalTransaction =
        new ControllerButton(
            controller::getInternalTransactionController,
            TransactionController.class,
            Controller::openTab,
            false);
    permissionAssignment =
        new ControllerButton(
            PermissionAssignmentController::new, PermissionAssignmentController.class);
    grantCashierRole =
        new ControllerButton(
            PermissionAssignmentController::cashierPermissionController,
            PermissionAssignmentController.class);
    beginnInventory = new ControllerButton(InventoryController::new, InventoryController.class);
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
