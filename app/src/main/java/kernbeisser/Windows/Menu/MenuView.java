package kernbeisser.Windows.Menu;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import java.awt.*;
import java.util.Locale;
import java.util.concurrent.CancellationException;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.plaf.FontUIResource;
import javax.swing.text.StyleContext;
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
import kernbeisser.Windows.CatalogImport.CatalogImportController;
import kernbeisser.Windows.ChangePassword.ChangePasswordController;
import kernbeisser.Windows.DatabaseView.DatabaseViewController;
import kernbeisser.Windows.EditArticles.EditArticlesController;
import kernbeisser.Windows.EditCatalog.EditCatalogController;
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
import kernbeisser.Windows.PermissionAssignment.PermissionAssignmentModel;
import kernbeisser.Windows.PermissionGranterAssignment.PermissionGranterAssignmentController;
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
  private ControllerButton permissionGranterAssignment;
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
  private JButton beginnInventory;
  private JPanel menugroupMyAccount;
  private ControllerButton internalTransaction;
  private ControllerButton importCatalog;
  private ControllerButton editCatalog;
  private ControllerButton databaseView;

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
      User.validateGroupMemberships(user, "");
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
                CashierShoppingMaskController::new, CashierShoppingMaskController.class)
            .withConfirmMessage("Willst Du mit dem Ladendienst beginnen?");
    editPriceList =
        new ControllerButton(
            ManagePriceListsController::new, ManagePriceListsController.class, Controller::openTab);
    editArticles =
        new ControllerButton(
            EditArticlesController::new, EditArticlesController.class, Controller::openTab);
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
            c -> c.openIn(new SubWindow(traceViewContainer())).getLoaded());
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
        new ControllerButton(
            controller::getDBLoginController, DBLogInController.class, Controller::openTab);
    editJobs = new ControllerButton(EditJobs::new, EditJobs.class, Controller::openTab);
    editApplicationSettings =
        new ControllerButton(SettingController::new, SettingController.class, Controller::openTab);
    order =
        new ControllerButton(
            controller::getPreorderController, PreOrderController.class, Controller::openTab);
    openSelfPreorder =
        new ControllerButton(
            controller::getOwnPreorderController, PreOrderController.class, Controller::openTab);
    if (openSelfPreorder.isEnabled()) openSelfPreorder.setEnabled(inheritsFullMembership);

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
            controller::generateAddBeginnerForm, FormEditorController.class, Controller::openTab);
    editSuppliers =
        new ControllerButton(EditSuppliers::new, EditSuppliers.class, Controller::openTab);

    editUserGroup =
        new ControllerButton(
            () -> new EditUserGroupController(LogInModel.getLoggedIn()),
            EditUserGroupController.class,
            Controller::openTab);
    importCatalog =
        new ControllerButton(
            CatalogImportController::new, CatalogImportController.class, Controller::openTab);
    editCatalog =
        new ControllerButton(
            EditCatalogController::new, EditCatalogController.class, Controller::openTab);
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
            Controller::openTab);
    permissionAssignment =
        new ControllerButton(
            PermissionAssignmentController::new, PermissionAssignmentController.class);
    //permissionAssignment.setEnabled(PermissionAssignmentModel.isAccessible());

    permissionGranterAssignment =
        new ControllerButton(
            PermissionGranterAssignmentController::new,
            PermissionGranterAssignmentController.class);
    beginnInventory = new ControllerButton(InventoryController::new, InventoryController.class);
    databaseView =
        new ControllerButton(
            controller::createDatabaseViewController, DatabaseViewController.class);
  }

  @Override
  public Component getFocusOnInitialize() {
    return openSelfShoppingMask;
  }

  @Override
  public String getTitle() {
    return "Menü";
  }

  // @spotless:off

  {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
    $$$setupUI$$$();
  }

  /** Method generated by IntelliJ IDEA GUI Designer
   * >>> IMPORTANT!! <<<
   * DO NOT edit this method OR call it in your code!
   * @noinspection ALL
   */
  private void $$$setupUI$$$() {
    createUIComponents();
    main = new JPanel();
    main.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
    final JScrollPane scrollPane1 = new JScrollPane();
    main.add(scrollPane1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
    menuPanel = new JPanel();
    menuPanel.setLayout(new GridLayoutManager(3, 3, new Insets(5, 5, 5, 5), -1, -1));
    scrollPane1.setViewportView(menuPanel);
    menugroupMyAccount = new JPanel();
    menugroupMyAccount.setLayout(new GridLayoutManager(8, 1, new Insets(5, 5, 5, 5), -1, -1));
    menuPanel.add(menugroupMyAccount, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
    menugroupMyAccount.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Mein {Storename}-Konto", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, this.$$$getFont$$$(null, -1, 18, menugroupMyAccount.getFont()), null));
    final Spacer spacer1 = new Spacer();
    menugroupMyAccount.add(spacer1, new GridConstraints(7, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
    Font showUserInfoFont = this.$$$getFont$$$(null, -1, -1, showUserInfo.getFont());
    if (showUserInfoFont != null) showUserInfo.setFont(showUserInfoFont);
    showUserInfo.setLabel("Persönliche Informationen");
    showUserInfo.setRequestFocusEnabled(false);
    showUserInfo.setText("Persönliche Informationen");
    menugroupMyAccount.add(showUserInfo, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    Font editUserSettingsFont = this.$$$getFont$$$(null, -1, -1, editUserSettings.getFont());
    if (editUserSettingsFont != null) editUserSettings.setFont(editUserSettingsFont);
    editUserSettings.setRequestFocusEnabled(false);
    editUserSettings.setText("Persönliche Programmeinstellungen");
    menugroupMyAccount.add(editUserSettings, new GridConstraints(6, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    Font openSelfShoppingMaskFont = this.$$$getFont$$$(null, -1, -1, openSelfShoppingMask.getFont());
    if (openSelfShoppingMaskFont != null) openSelfShoppingMask.setFont(openSelfShoppingMaskFont);
    openSelfShoppingMask.setLabel("Selbsteinkauf");
    openSelfShoppingMask.setRequestFocusEnabled(false);
    openSelfShoppingMask.setText("Selbsteinkauf");
    menugroupMyAccount.add(openSelfShoppingMask, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
    Font editUserGroupFont = this.$$$getFont$$$(null, -1, -1, editUserGroup.getFont());
    if (editUserGroupFont != null) editUserGroup.setFont(editUserGroupFont);
    editUserGroup.setRequestFocusEnabled(false);
    editUserGroup.setText("Nutzergruppe wechseln");
    menugroupMyAccount.add(editUserGroup, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    Font openSelfPreorderFont = this.$$$getFont$$$(null, -1, -1, openSelfPreorder.getFont());
    if (openSelfPreorderFont != null) openSelfPreorder.setFont(openSelfPreorderFont);
    openSelfPreorder.setLabel("Meine Vorbestellung");
    openSelfPreorder.setRequestFocusEnabled(false);
    openSelfPreorder.setText("Meine Vorbestellung");
    openSelfPreorder.setVisible(true);
    menugroupMyAccount.add(openSelfPreorder, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    Font doUserDefiniedTransactionFont = this.$$$getFont$$$(null, -1, -1, doUserDefiniedTransaction.getFont());
    if (doUserDefiniedTransactionFont != null) doUserDefiniedTransaction.setFont(doUserDefiniedTransactionFont);
    doUserDefiniedTransaction.setLabel("Guthaben an Andere übertragen");
    doUserDefiniedTransaction.setRequestFocusEnabled(false);
    doUserDefiniedTransaction.setText("Guthaben an Andere übertragen");
    menugroupMyAccount.add(doUserDefiniedTransaction, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    Font changePasswordFont = this.$$$getFont$$$(null, -1, -1, changePassword.getFont());
    if (changePasswordFont != null) changePassword.setFont(changePasswordFont);
    changePassword.setRequestFocusEnabled(false);
    changePassword.setText("Passwort ändern");
    menugroupMyAccount.add(changePassword, new GridConstraints(5, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    final JPanel panel1 = new JPanel();
    panel1.setLayout(new GridLayoutManager(8, 1, new Insets(10, 10, 10, 10), -1, -1));
    menuPanel.add(panel1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
    panel1.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Warenumsatz", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, this.$$$getFont$$$(null, -1, 18, panel1.getFont()), null));
    Font openCashierShoppingMaskFont = this.$$$getFont$$$(null, -1, -1, openCashierShoppingMask.getFont());
    if (openCashierShoppingMaskFont != null) openCashierShoppingMask.setFont(openCashierShoppingMaskFont);
    openCashierShoppingMask.setHideActionText(false);
    openCashierShoppingMask.setRequestFocusEnabled(false);
    openCashierShoppingMask.setText("Ladendienst beginnen");
    panel1.add(openCashierShoppingMask, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    Font orderFont = this.$$$getFont$$$(null, -1, -1, order.getFont());
    if (orderFont != null) order.setFont(orderFont);
    order.setRequestFocusEnabled(false);
    order.setText("Vorbestellung");
    panel1.add(order, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    Font editPriceListFont = this.$$$getFont$$$(null, -1, -1, editPriceList.getFont());
    if (editPriceListFont != null) editPriceList.setFont(editPriceListFont);
    editPriceList.setRequestFocusEnabled(false);
    editPriceList.setText("Preislisten bearbeiten");
    panel1.add(editPriceList, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    Font editSurchargeTablesFont = this.$$$getFont$$$(null, -1, -1, editSurchargeTables.getFont());
    if (editSurchargeTablesFont != null) editSurchargeTables.setFont(editSurchargeTablesFont);
    editSurchargeTables.setRequestFocusEnabled(false);
    editSurchargeTables.setText("Zuschlagsgruppen bearbeiten");
    panel1.add(editSurchargeTables, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    final Spacer spacer2 = new Spacer();
    panel1.add(spacer2, new GridConstraints(7, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
    Font supplyFont = this.$$$getFont$$$(null, -1, -1, supply.getFont());
    if (supplyFont != null) supply.setFont(supplyFont);
    supply.setText("Lieferung eingeben");
    panel1.add(supply, new GridConstraints(5, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
    beginnInventory.setText("Inventur beginnen");
    panel1.add(beginnInventory, new GridConstraints(6, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    final JPanel panel2 = new JPanel();
    panel2.setLayout(new GridLayoutManager(4, 1, new Insets(10, 10, 10, 10), -1, -1));
    menuPanel.add(panel2, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
    panel2.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Vorstand", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, this.$$$getFont$$$(null, -1, 18, panel2.getFont()), null));
    Font accountingReportsFont = this.$$$getFont$$$(null, -1, -1, accountingReports.getFont());
    if (accountingReportsFont != null) accountingReports.setFont(accountingReportsFont);
    accountingReports.setText("Buchhaltungsberichte");
    panel2.add(accountingReports, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    final Spacer spacer3 = new Spacer();
    panel2.add(spacer3, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
    Font changePermissionsFont = this.$$$getFont$$$(null, -1, -1, changePermissions.getFont());
    if (changePermissionsFont != null) changePermissions.setFont(changePermissionsFont);
    changePermissions.setRequestFocusEnabled(false);
    changePermissions.setText("Berechtigungen bearbeiten");
    panel2.add(changePermissions, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    permissionGranterAssignment.setText("Berechtigungsweitergabe");
    panel2.add(permissionGranterAssignment, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
    menuPanel.add(infoPanel.$$$getRootComponent$$$(), new GridConstraints(0, 2, 3, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, new Dimension(500, 500), new Dimension(500, 500), null, 0, false));
    final JPanel panel3 = new JPanel();
    panel3.setLayout(new GridLayoutManager(4, 1, new Insets(10, 10, 10, 10), -1, -1));
    menuPanel.add(panel3, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
    panel3.setBorder(BorderFactory.createTitledBorder(null, "Programmadministration", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, this.$$$getFont$$$(null, -1, 18, panel3.getFont()), null));
    Font changeDBConnectionFont = this.$$$getFont$$$(null, -1, -1, changeDBConnection.getFont());
    if (changeDBConnectionFont != null) changeDBConnection.setFont(changeDBConnectionFont);
    changeDBConnection.setLabel("Datenbankverbindung bearbeiten");
    changeDBConnection.setRequestFocusEnabled(false);
    changeDBConnection.setText("Datenbankverbindung bearbeiten");
    panel3.add(changeDBConnection, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    final Spacer spacer4 = new Spacer();
    panel3.add(spacer4, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
    Font editApplicationSettingsFont = this.$$$getFont$$$(null, -1, -1, editApplicationSettings.getFont());
    if (editApplicationSettingsFont != null) editApplicationSettings.setFont(editApplicationSettingsFont);
    editApplicationSettings.setLabel("Programmeinstellungen bearbeiten");
    editApplicationSettings.setRequestFocusEnabled(false);
    editApplicationSettings.setText("Programmeinstellungen bearbeiten");
    panel3.add(editApplicationSettings, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    databaseView.setText("Datenbank anzeigen");
    panel3.add(databaseView, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    final JPanel panel4 = new JPanel();
    panel4.setLayout(new GridLayoutManager(7, 1, new Insets(10, 10, 10, 10), -1, -1));
    menuPanel.add(panel4, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
    panel4.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Benutzerverwaltung", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, this.$$$getFont$$$(null, -1, 18, panel4.getFont()), null));
    Font editUsersFont = this.$$$getFont$$$(null, -1, -1, editUsers.getFont());
    if (editUsersFont != null) editUsers.setFont(editUsersFont);
    editUsers.setRequestFocusEnabled(false);
    editUsers.setText("Benutzer bearbeiten");
    panel4.add(editUsers, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    final Spacer spacer5 = new Spacer();
    panel4.add(spacer5, new GridConstraints(6, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
    doTransactionPayIn.setActionCommand("Guthaben überweisen");
    Font doTransactionPayInFont = this.$$$getFont$$$(null, -1, -1, doTransactionPayIn.getFont());
    if (doTransactionPayInFont != null) doTransactionPayIn.setFont(doTransactionPayInFont);
    doTransactionPayIn.setLabel("Guthaben für Mitglieder buchen");
    doTransactionPayIn.setRequestFocusEnabled(false);
    doTransactionPayIn.setText("Guthaben für Mitglieder buchen");
    panel4.add(doTransactionPayIn, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    Font addBeginnerFont = this.$$$getFont$$$(null, -1, -1, addBeginner.getFont());
    if (addBeginnerFont != null) addBeginner.setFont(addBeginnerFont);
    addBeginner.setRequestFocusEnabled(false);
    addBeginner.setText("Probemitglied aufnehmen");
    panel4.add(addBeginner, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
    Font editJobsFont = this.$$$getFont$$$(null, -1, -1, editJobs.getFont());
    if (editJobsFont != null) editJobs.setFont(editJobsFont);
    editJobs.setText("Jobs bearbeiten");
    panel4.add(editJobs, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
    Font internalTransactionFont = this.$$$getFont$$$(null, -1, -1, internalTransaction.getFont());
    if (internalTransactionFont != null) internalTransaction.setFont(internalTransactionFont);
    internalTransaction.setLabel("Sonderzahlungen und interne Überweisungen");
    internalTransaction.setRequestFocusEnabled(false);
    internalTransaction.setText("Sonderzahlungen und interne Überweisungen");
    panel4.add(internalTransaction, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    permissionAssignment.setText("Berechtigungen erteilen");
    panel4.add(permissionAssignment, new GridConstraints(5, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
    final JPanel panel5 = new JPanel();
    panel5.setLayout(new GridLayoutManager(7, 1, new Insets(10, 10, 10, 10), -1, -1));
    panel5.setEnabled(true);
    menuPanel.add(panel5, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
    panel5.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Artikelstamm", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, this.$$$getFont$$$(null, -1, 18, panel5.getFont()), null));
    final Spacer spacer6 = new Spacer();
    panel5.add(spacer6, new GridConstraints(6, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
    offerManagement.setActionCommand("Artikel bearbeiten");
    offerManagement.setEnabled(false);
    Font offerManagementFont = this.$$$getFont$$$(null, -1, -1, offerManagement.getFont());
    if (offerManagementFont != null) offerManagement.setFont(offerManagementFont);
    offerManagement.setRequestFocusEnabled(false);
    offerManagement.setText("Aktionsartikel bearbeiten");
    panel5.add(offerManagement, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    Font importCatalogFont = this.$$$getFont$$$(null, -1, -1, importCatalog.getFont());
    if (importCatalogFont != null) importCatalog.setFont(importCatalogFont);
    importCatalog.setText("Kornkraft-Katalog einlesen");
    panel5.add(importCatalog, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
    Font editSuppliersFont = this.$$$getFont$$$(null, -1, -1, editSuppliers.getFont());
    if (editSuppliersFont != null) editSuppliers.setFont(editSuppliersFont);
    editSuppliers.setText("Lieferanten beabeiten");
    panel5.add(editSuppliers, new GridConstraints(5, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
    Font synchoniseCatalogFont = this.$$$getFont$$$(null, -1, -1, synchoniseCatalog.getFont());
    if (synchoniseCatalogFont != null) synchoniseCatalog.setFont(synchoniseCatalogFont);
    synchoniseCatalog.setText("Kornkraft-Katalog synchronisieren");
    panel5.add(synchoniseCatalog, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
    Font editCatalogFont = this.$$$getFont$$$(null, -1, -1, editCatalog.getFont());
    if (editCatalogFont != null) editCatalog.setFont(editCatalogFont);
    editCatalog.setText("Kornkraft-Katalog ansehen");
    panel5.add(editCatalog, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
    editArticles.setActionCommand("Artikel bearbeiten");
    Font editArticlesFont = this.$$$getFont$$$(null, -1, -1, editArticles.getFont());
    if (editArticlesFont != null) editArticles.setFont(editArticlesFont);
    editArticles.setRequestFocusEnabled(false);
    editArticles.setText("Artikel bearbeiten");
    panel5.add(editArticles, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    final JPanel panel6 = new JPanel();
    panel6.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
    main.add(panel6, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
    logout = new JButton();
    logout.setText("");
    panel6.add(logout, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    final Spacer spacer7 = new Spacer();
    panel6.add(spacer7, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
  }

  /** @noinspection ALL */
  private Font $$$getFont$$$(String fontName, int style, int size, Font currentFont) {
    if (currentFont == null) return null;
    String resultName;
    if (fontName == null) {
      resultName = currentFont.getName();
    } else {
      Font testFont = new Font(fontName, Font.PLAIN, 10);
      if (testFont.canDisplay('a') && testFont.canDisplay('1')) {
        resultName = fontName;
      } else {
        resultName = currentFont.getName();
      }
    }
    Font font = new Font(resultName, style >= 0 ? style : currentFont.getStyle(), size >= 0 ? size : currentFont.getSize());
    boolean isMac = System.getProperty("os.name", "").toLowerCase(Locale.ENGLISH).startsWith("mac");
    Font fontWithFallback = isMac ? new Font(font.getFamily(), font.getStyle(), font.getSize()) : new StyleContext().getFont(font.getFamily(), font.getStyle(), font.getSize());
    return fontWithFallback instanceof FontUIResource ? fontWithFallback : new FontUIResource(fontWithFallback);
  }

  /** @noinspection ALL */
  public JComponent $$$getRootComponent$$$() {
    return main;
  }

  // @spotless:on
}
