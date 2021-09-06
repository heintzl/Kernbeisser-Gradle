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
import kernbeisser.Enums.Setting;
import kernbeisser.Enums.TransactionType;
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
import kernbeisser.Windows.Trasaction.TransactionController;
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

  @Override
  public void initialize(MenuController controller) {
    ((Frame) TabbedPaneModel.getMainPanel().getContainer())
        .setTitle("Kernbeißer (" + LogInModel.getLoggedIn().getFullName() + ")");
    logout.setIcon(
        IconFontSwing.buildIcon(
            FontAwesome.POWER_OFF,
            25 * Setting.LABEL_SCALE_FACTOR.getFloatValue(),
            new Color(182, 46, 4)));
    logout.addActionListener(e -> back());
    logout.setToolTipText(LogInModel.getLoggedIn().getFullName() + " vom Programm abmelden");
  }

  @Linked private MenuController controller;

  @Override
  public @NotNull JComponent getContent() {
    return main;
  }

  private void createUIComponents() {
    infoPanel = new InfoPanelController().getView();
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
    // NOT IMPLEMENTED
    placeHolderControllerButton2 = ControllerButton.empty();
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
              }
            },
            SoloShoppingMaskController.class,
            e -> {
              if (e != null) {
                e.openTab();
              }
            });
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
            () -> new EditUserGroupController(LogInModel.getLoggedInFromDB()),
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
                new TransactionController(
                    LogInModel.getLoggedInFromDB(), TransactionType.USER_GENERATED),
            TransactionController.class);
    permissionAssignment =
        new ControllerButton(
            PermissionAssignmentController::new, PermissionAssignmentController.class);
    grantCashierRole =
        new ControllerButton(
            PermissionAssignmentController::cashierPermissionController,
            PermissionAssignmentController.class);
  }

  @Override
  public Component getFocusOnInitialize() {
    return openSelfShoppingMask;
  }

  @Override
  public String getTitle() {
    return "Menü";
  }

  {
    // GUI initializer generated by IntelliJ IDEA GUI Designer
    // >>> IMPORTANT!! <<<
    // DO NOT EDIT OR ADD ANY CODE HERE!
    $$$setupUI$$$();
  }

  /**
   * Method generated by IntelliJ IDEA GUI Designer >>> IMPORTANT!! <<< DO NOT edit this method OR
   * call it in your code!
   *
   * @noinspection ALL
   */
  private void $$$setupUI$$$() {
    createUIComponents();
    main = new JPanel();
    main.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
    final JScrollPane scrollPane1 = new JScrollPane();
    main.add(
        scrollPane1,
        new GridConstraints(
            0,
            0,
            1,
            1,
            GridConstraints.ANCHOR_CENTER,
            GridConstraints.FILL_BOTH,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW,
            null,
            null,
            null,
            0,
            false));
    menuPanel = new JPanel();
    menuPanel.setLayout(new GridLayoutManager(4, 3, new Insets(5, 5, 5, 5), -1, -1));
    scrollPane1.setViewportView(menuPanel);
    final JPanel panel1 = new JPanel();
    panel1.setLayout(new GridLayoutManager(8, 1, new Insets(5, 5, 5, 5), -1, -1));
    menuPanel.add(
        panel1,
        new GridConstraints(
            0,
            1,
            1,
            1,
            GridConstraints.ANCHOR_CENTER,
            GridConstraints.FILL_BOTH,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
            null,
            null,
            null,
            0,
            false));
    panel1.setBorder(
        BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(),
            "Mein Kernbeißer-Konto",
            TitledBorder.DEFAULT_JUSTIFICATION,
            TitledBorder.DEFAULT_POSITION,
            this.$$$getFont$$$(null, -1, 18, panel1.getFont()),
            null));
    final Spacer spacer1 = new Spacer();
    panel1.add(
        spacer1,
        new GridConstraints(
            7,
            0,
            1,
            1,
            GridConstraints.ANCHOR_CENTER,
            GridConstraints.FILL_VERTICAL,
            1,
            GridConstraints.SIZEPOLICY_WANT_GROW,
            null,
            null,
            null,
            0,
            false));
    Font showUserInfoFont = this.$$$getFont$$$(null, -1, -1, showUserInfo.getFont());
    if (showUserInfoFont != null) {
      showUserInfo.setFont(showUserInfoFont);
    }
    showUserInfo.setLabel("Persönliche Informationen");
    showUserInfo.setRequestFocusEnabled(false);
    showUserInfo.setText("Persönliche Informationen");
    panel1.add(
        showUserInfo,
        new GridConstraints(
            4,
            0,
            1,
            1,
            GridConstraints.ANCHOR_CENTER,
            GridConstraints.FILL_HORIZONTAL,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
            GridConstraints.SIZEPOLICY_FIXED,
            null,
            null,
            null,
            0,
            false));
    Font editUserSettingsFont = this.$$$getFont$$$(null, -1, -1, editUserSettings.getFont());
    if (editUserSettingsFont != null) {
      editUserSettings.setFont(editUserSettingsFont);
    }
    editUserSettings.setRequestFocusEnabled(false);
    editUserSettings.setText("Persönliche Programmeinstellungen");
    panel1.add(
        editUserSettings,
        new GridConstraints(
            6,
            0,
            1,
            1,
            GridConstraints.ANCHOR_CENTER,
            GridConstraints.FILL_HORIZONTAL,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
            GridConstraints.SIZEPOLICY_FIXED,
            null,
            null,
            null,
            0,
            false));
    Font openSelfShoppingMaskFont =
        this.$$$getFont$$$(null, -1, -1, openSelfShoppingMask.getFont());
    if (openSelfShoppingMaskFont != null) {
      openSelfShoppingMask.setFont(openSelfShoppingMaskFont);
    }
    openSelfShoppingMask.setLabel("Selbsteinkauf");
    openSelfShoppingMask.setRequestFocusEnabled(false);
    openSelfShoppingMask.setText("Selbsteinkauf");
    panel1.add(
        openSelfShoppingMask,
        new GridConstraints(
            0,
            0,
            1,
            1,
            GridConstraints.ANCHOR_CENTER,
            GridConstraints.FILL_HORIZONTAL,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
            null,
            null,
            null,
            0,
            false));
    Font editUserGroupFont = this.$$$getFont$$$(null, -1, -1, editUserGroup.getFont());
    if (editUserGroupFont != null) {
      editUserGroup.setFont(editUserGroupFont);
    }
    editUserGroup.setRequestFocusEnabled(false);
    editUserGroup.setText("Nutzergruppe wechseln");
    panel1.add(
        editUserGroup,
        new GridConstraints(
            3,
            0,
            1,
            1,
            GridConstraints.ANCHOR_CENTER,
            GridConstraints.FILL_HORIZONTAL,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
            GridConstraints.SIZEPOLICY_FIXED,
            null,
            null,
            null,
            0,
            false));
    Font openSelfPreorderFont = this.$$$getFont$$$(null, -1, -1, openSelfPreorder.getFont());
    if (openSelfPreorderFont != null) {
      openSelfPreorder.setFont(openSelfPreorderFont);
    }
    openSelfPreorder.setLabel("Meine Vorbestellung");
    openSelfPreorder.setRequestFocusEnabled(false);
    openSelfPreorder.setText("Meine Vorbestellung");
    openSelfPreorder.setVisible(true);
    panel1.add(
        openSelfPreorder,
        new GridConstraints(
            1,
            0,
            1,
            1,
            GridConstraints.ANCHOR_CENTER,
            GridConstraints.FILL_HORIZONTAL,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
            GridConstraints.SIZEPOLICY_FIXED,
            null,
            null,
            null,
            0,
            false));
    Font doUserDefiniedTransactionFont =
        this.$$$getFont$$$(null, -1, -1, doUserDefiniedTransaction.getFont());
    if (doUserDefiniedTransactionFont != null) {
      doUserDefiniedTransaction.setFont(doUserDefiniedTransactionFont);
    }
    doUserDefiniedTransaction.setLabel("Guthaben überweisen");
    doUserDefiniedTransaction.setRequestFocusEnabled(false);
    doUserDefiniedTransaction.setText("Guthaben überweisen");
    panel1.add(
        doUserDefiniedTransaction,
        new GridConstraints(
            2,
            0,
            1,
            1,
            GridConstraints.ANCHOR_CENTER,
            GridConstraints.FILL_HORIZONTAL,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
            GridConstraints.SIZEPOLICY_FIXED,
            null,
            null,
            null,
            0,
            false));
    Font changePasswordFont = this.$$$getFont$$$(null, -1, -1, changePassword.getFont());
    if (changePasswordFont != null) {
      changePassword.setFont(changePasswordFont);
    }
    changePassword.setRequestFocusEnabled(false);
    changePassword.setText("Passwort ändern");
    panel1.add(
        changePassword,
        new GridConstraints(
            5,
            0,
            1,
            1,
            GridConstraints.ANCHOR_CENTER,
            GridConstraints.FILL_HORIZONTAL,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
            GridConstraints.SIZEPOLICY_FIXED,
            null,
            null,
            null,
            0,
            false));
    final JPanel panel2 = new JPanel();
    panel2.setLayout(new GridLayoutManager(11, 1, new Insets(10, 10, 10, 10), -1, -1));
    menuPanel.add(
        panel2,
        new GridConstraints(
            0,
            0,
            1,
            1,
            GridConstraints.ANCHOR_CENTER,
            GridConstraints.FILL_BOTH,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
            null,
            null,
            null,
            0,
            false));
    panel2.setBorder(
        BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(),
            "Ladendienst",
            TitledBorder.DEFAULT_JUSTIFICATION,
            TitledBorder.DEFAULT_POSITION,
            this.$$$getFont$$$(null, -1, 18, panel2.getFont()),
            null));
    Font openCashierShoppingMaskFont =
        this.$$$getFont$$$(null, -1, -1, openCashierShoppingMask.getFont());
    if (openCashierShoppingMaskFont != null) {
      openCashierShoppingMask.setFont(openCashierShoppingMaskFont);
    }
    openCashierShoppingMask.setHideActionText(false);
    openCashierShoppingMask.setRequestFocusEnabled(false);
    openCashierShoppingMask.setText("Ladendienst beginnen");
    panel2.add(
        openCashierShoppingMask,
        new GridConstraints(
            0,
            0,
            1,
            1,
            GridConstraints.ANCHOR_CENTER,
            GridConstraints.FILL_HORIZONTAL,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
            GridConstraints.SIZEPOLICY_FIXED,
            null,
            null,
            null,
            0,
            false));
    Font orderFont = this.$$$getFont$$$(null, -1, -1, order.getFont());
    if (orderFont != null) {
      order.setFont(orderFont);
    }
    order.setRequestFocusEnabled(false);
    order.setText("Vorbestellung");
    panel2.add(
        order,
        new GridConstraints(
            1,
            0,
            1,
            1,
            GridConstraints.ANCHOR_CENTER,
            GridConstraints.FILL_HORIZONTAL,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
            GridConstraints.SIZEPOLICY_FIXED,
            null,
            null,
            null,
            0,
            false));
    Font editPriceListFont = this.$$$getFont$$$(null, -1, -1, editPriceList.getFont());
    if (editPriceListFont != null) {
      editPriceList.setFont(editPriceListFont);
    }
    editPriceList.setRequestFocusEnabled(false);
    editPriceList.setText("Preislisten bearbeiten");
    panel2.add(
        editPriceList,
        new GridConstraints(
            2,
            0,
            1,
            1,
            GridConstraints.ANCHOR_CENTER,
            GridConstraints.FILL_HORIZONTAL,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
            GridConstraints.SIZEPOLICY_FIXED,
            null,
            null,
            null,
            0,
            false));
    editArticles.setActionCommand("Artikel bearbeiten");
    Font editArticlesFont = this.$$$getFont$$$(null, -1, -1, editArticles.getFont());
    if (editArticlesFont != null) {
      editArticles.setFont(editArticlesFont);
    }
    editArticles.setRequestFocusEnabled(false);
    editArticles.setText("Artikel bearbeiten");
    panel2.add(
        editArticles,
        new GridConstraints(
            3,
            0,
            1,
            1,
            GridConstraints.ANCHOR_CENTER,
            GridConstraints.FILL_HORIZONTAL,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
            GridConstraints.SIZEPOLICY_FIXED,
            null,
            null,
            null,
            0,
            false));
    Font editSurchargeTablesFont = this.$$$getFont$$$(null, -1, -1, editSurchargeTables.getFont());
    if (editSurchargeTablesFont != null) {
      editSurchargeTables.setFont(editSurchargeTablesFont);
    }
    editSurchargeTables.setRequestFocusEnabled(false);
    editSurchargeTables.setText("Zuschlagsgruppen bearbeiten");
    panel2.add(
        editSurchargeTables,
        new GridConstraints(
            4,
            0,
            1,
            1,
            GridConstraints.ANCHOR_CENTER,
            GridConstraints.FILL_HORIZONTAL,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
            GridConstraints.SIZEPOLICY_FIXED,
            null,
            null,
            null,
            0,
            false));
    Font editSuppliersFont = this.$$$getFont$$$(null, -1, -1, editSuppliers.getFont());
    if (editSuppliersFont != null) {
      editSuppliers.setFont(editSuppliersFont);
    }
    editSuppliers.setText("Lieferanten beabeiten");
    panel2.add(
        editSuppliers,
        new GridConstraints(
            5,
            0,
            1,
            1,
            GridConstraints.ANCHOR_CENTER,
            GridConstraints.FILL_HORIZONTAL,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
            null,
            null,
            null,
            0,
            false));
    Font synchoniseCatalogFont = this.$$$getFont$$$(null, -1, -1, synchoniseCatalog.getFont());
    if (synchoniseCatalogFont != null) {
      synchoniseCatalog.setFont(synchoniseCatalogFont);
    }
    synchoniseCatalog.setText("Kornkraft-Katalog synchronisieren");
    panel2.add(
        synchoniseCatalog,
        new GridConstraints(
            7,
            0,
            1,
            1,
            GridConstraints.ANCHOR_CENTER,
            GridConstraints.FILL_HORIZONTAL,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
            null,
            null,
            null,
            0,
            false));
    final Spacer spacer2 = new Spacer();
    panel2.add(
        spacer2,
        new GridConstraints(
            10,
            0,
            1,
            1,
            GridConstraints.ANCHOR_CENTER,
            GridConstraints.FILL_VERTICAL,
            1,
            GridConstraints.SIZEPOLICY_WANT_GROW,
            null,
            null,
            null,
            0,
            false));
    offerManagement.setActionCommand("Artikel bearbeiten");
    Font offerManagementFont = this.$$$getFont$$$(null, -1, -1, offerManagement.getFont());
    if (offerManagementFont != null) {
      offerManagement.setFont(offerManagementFont);
    }
    offerManagement.setRequestFocusEnabled(false);
    offerManagement.setText("Aktionsartikel bearbeiten");
    panel2.add(
        offerManagement,
        new GridConstraints(
            6,
            0,
            1,
            1,
            GridConstraints.ANCHOR_CENTER,
            GridConstraints.FILL_HORIZONTAL,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
            GridConstraints.SIZEPOLICY_FIXED,
            null,
            null,
            null,
            0,
            false));
    Font supplyFont = this.$$$getFont$$$(null, -1, -1, supply.getFont());
    if (supplyFont != null) {
      supply.setFont(supplyFont);
    }
    supply.setText("Lieferung eingeben");
    panel2.add(
        supply,
        new GridConstraints(
            8,
            0,
            1,
            1,
            GridConstraints.ANCHOR_CENTER,
            GridConstraints.FILL_HORIZONTAL,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
            null,
            null,
            null,
            0,
            false));
    grantCashierRole.setText("Ladendienst-Mitglieder verwalten");
    panel2.add(
        grantCashierRole,
        new GridConstraints(
            9,
            0,
            1,
            1,
            GridConstraints.ANCHOR_CENTER,
            GridConstraints.FILL_HORIZONTAL,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
            GridConstraints.SIZEPOLICY_FIXED,
            null,
            null,
            null,
            0,
            false));
    final JPanel panel3 = new JPanel();
    panel3.setLayout(new GridLayoutManager(5, 1, new Insets(10, 10, 10, 10), -1, -1));
    menuPanel.add(
        panel3,
        new GridConstraints(
            1,
            1,
            1,
            1,
            GridConstraints.ANCHOR_CENTER,
            GridConstraints.FILL_BOTH,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
            null,
            null,
            null,
            0,
            false));
    panel3.setBorder(
        BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(),
            "Vorstand",
            TitledBorder.DEFAULT_JUSTIFICATION,
            TitledBorder.DEFAULT_POSITION,
            this.$$$getFont$$$(null, -1, 18, panel3.getFont()),
            null));
    Font accountingReportsFont = this.$$$getFont$$$(null, -1, -1, accountingReports.getFont());
    if (accountingReportsFont != null) {
      accountingReports.setFont(accountingReportsFont);
    }
    accountingReports.setText("Buchhaltungsberichte");
    panel3.add(
        accountingReports,
        new GridConstraints(
            0,
            0,
            1,
            1,
            GridConstraints.ANCHOR_CENTER,
            GridConstraints.FILL_HORIZONTAL,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
            GridConstraints.SIZEPOLICY_FIXED,
            null,
            null,
            null,
            0,
            false));
    final Spacer spacer3 = new Spacer();
    panel3.add(
        spacer3,
        new GridConstraints(
            4,
            0,
            1,
            1,
            GridConstraints.ANCHOR_CENTER,
            GridConstraints.FILL_VERTICAL,
            1,
            GridConstraints.SIZEPOLICY_WANT_GROW,
            null,
            null,
            null,
            0,
            false));
    placeHolderControllerButton2.setEnabled(false);
    Font placeHolderControllerButton2Font =
        this.$$$getFont$$$(null, -1, -1, placeHolderControllerButton2.getFont());
    if (placeHolderControllerButton2Font != null) {
      placeHolderControllerButton2.setFont(placeHolderControllerButton2Font);
    }
    placeHolderControllerButton2.setText("<PlaceHolder>");
    panel3.add(
        placeHolderControllerButton2,
        new GridConstraints(
            3,
            0,
            1,
            1,
            GridConstraints.ANCHOR_CENTER,
            GridConstraints.FILL_HORIZONTAL,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
            GridConstraints.SIZEPOLICY_FIXED,
            null,
            null,
            null,
            0,
            false));
    Font changePermissionsFont = this.$$$getFont$$$(null, -1, -1, changePermissions.getFont());
    if (changePermissionsFont != null) {
      changePermissions.setFont(changePermissionsFont);
    }
    changePermissions.setRequestFocusEnabled(false);
    changePermissions.setText("Berechtigungen bearbeiten");
    panel3.add(
        changePermissions,
        new GridConstraints(
            1,
            0,
            1,
            1,
            GridConstraints.ANCHOR_CENTER,
            GridConstraints.FILL_HORIZONTAL,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
            GridConstraints.SIZEPOLICY_FIXED,
            null,
            null,
            null,
            0,
            false));
    permissionAssignment.setText("Berechtigungen erteilen");
    panel3.add(
        permissionAssignment,
        new GridConstraints(
            2,
            0,
            1,
            1,
            GridConstraints.ANCHOR_CENTER,
            GridConstraints.FILL_HORIZONTAL,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
            null,
            null,
            null,
            0,
            false));
    final JPanel panel4 = new JPanel();
    panel4.setLayout(new GridLayoutManager(5, 1, new Insets(10, 10, 10, 10), -1, -1));
    menuPanel.add(
        panel4,
        new GridConstraints(
            1,
            0,
            1,
            1,
            GridConstraints.ANCHOR_CENTER,
            GridConstraints.FILL_BOTH,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
            null,
            null,
            null,
            0,
            false));
    panel4.setBorder(
        BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(),
            "Benutzerverwaltung",
            TitledBorder.DEFAULT_JUSTIFICATION,
            TitledBorder.DEFAULT_POSITION,
            this.$$$getFont$$$(null, -1, 18, panel4.getFont()),
            null));
    Font editUsersFont = this.$$$getFont$$$(null, -1, -1, editUsers.getFont());
    if (editUsersFont != null) {
      editUsers.setFont(editUsersFont);
    }
    editUsers.setRequestFocusEnabled(false);
    editUsers.setText("Benutzer bearbeiten");
    panel4.add(
        editUsers,
        new GridConstraints(
            1,
            0,
            1,
            1,
            GridConstraints.ANCHOR_CENTER,
            GridConstraints.FILL_HORIZONTAL,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
            GridConstraints.SIZEPOLICY_FIXED,
            null,
            null,
            null,
            0,
            false));
    final Spacer spacer4 = new Spacer();
    panel4.add(
        spacer4,
        new GridConstraints(
            4,
            0,
            1,
            1,
            GridConstraints.ANCHOR_CENTER,
            GridConstraints.FILL_VERTICAL,
            1,
            GridConstraints.SIZEPOLICY_WANT_GROW,
            null,
            null,
            null,
            0,
            false));
    doTransactionPayIn.setActionCommand("Guthaben überweisen");
    Font doTransactionPayInFont = this.$$$getFont$$$(null, -1, -1, doTransactionPayIn.getFont());
    if (doTransactionPayInFont != null) {
      doTransactionPayIn.setFont(doTransactionPayInFont);
    }
    doTransactionPayIn.setLabel("Guthaben für Mitglieder buchen");
    doTransactionPayIn.setRequestFocusEnabled(false);
    doTransactionPayIn.setText("Guthaben für Mitglieder buchen");
    panel4.add(
        doTransactionPayIn,
        new GridConstraints(
            2,
            0,
            1,
            1,
            GridConstraints.ANCHOR_CENTER,
            GridConstraints.FILL_HORIZONTAL,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
            GridConstraints.SIZEPOLICY_FIXED,
            null,
            null,
            null,
            0,
            false));
    Font addBeginnerFont = this.$$$getFont$$$(null, -1, -1, addBeginner.getFont());
    if (addBeginnerFont != null) {
      addBeginner.setFont(addBeginnerFont);
    }
    addBeginner.setRequestFocusEnabled(false);
    addBeginner.setText("Probemitglied aufnehmen");
    panel4.add(
        addBeginner,
        new GridConstraints(
            0,
            0,
            1,
            1,
            GridConstraints.ANCHOR_CENTER,
            GridConstraints.FILL_HORIZONTAL,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
            null,
            null,
            null,
            0,
            false));
    Font editJobsFont = this.$$$getFont$$$(null, -1, -1, editJobs.getFont());
    if (editJobsFont != null) {
      editJobs.setFont(editJobsFont);
    }
    editJobs.setText("Jobs bearbeiten");
    panel4.add(
        editJobs,
        new GridConstraints(
            3,
            0,
            1,
            1,
            GridConstraints.ANCHOR_CENTER,
            GridConstraints.FILL_HORIZONTAL,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
            null,
            null,
            null,
            0,
            false));
    final JPanel panel5 = new JPanel();
    panel5.setLayout(new GridLayoutManager(1, 1, new Insets(10, 10, 10, 10), -1, -1));
    menuPanel.add(
        panel5,
        new GridConstraints(
            2,
            1,
            1,
            1,
            GridConstraints.ANCHOR_CENTER,
            GridConstraints.FILL_BOTH,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
            null,
            null,
            null,
            0,
            false));
    final JPanel panel6 = new JPanel();
    panel6.setLayout(new GridLayoutManager(3, 1, new Insets(10, 10, 10, 10), -1, -1));
    menuPanel.add(
        panel6,
        new GridConstraints(
            2,
            0,
            1,
            1,
            GridConstraints.ANCHOR_CENTER,
            GridConstraints.FILL_BOTH,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
            null,
            null,
            null,
            0,
            false));
    panel6.setBorder(
        BorderFactory.createTitledBorder(
            null,
            "Programmadministration",
            TitledBorder.DEFAULT_JUSTIFICATION,
            TitledBorder.DEFAULT_POSITION,
            this.$$$getFont$$$(null, -1, 18, panel6.getFont()),
            null));
    Font changeDBConnectionFont = this.$$$getFont$$$(null, -1, -1, changeDBConnection.getFont());
    if (changeDBConnectionFont != null) {
      changeDBConnection.setFont(changeDBConnectionFont);
    }
    changeDBConnection.setLabel("Datenbankverbindung bearbeiten");
    changeDBConnection.setRequestFocusEnabled(false);
    changeDBConnection.setText("Datenbankverbindung bearbeiten");
    panel6.add(
        changeDBConnection,
        new GridConstraints(
            0,
            0,
            1,
            1,
            GridConstraints.ANCHOR_CENTER,
            GridConstraints.FILL_HORIZONTAL,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
            GridConstraints.SIZEPOLICY_FIXED,
            null,
            null,
            null,
            0,
            false));
    final Spacer spacer5 = new Spacer();
    panel6.add(
        spacer5,
        new GridConstraints(
            2,
            0,
            1,
            1,
            GridConstraints.ANCHOR_CENTER,
            GridConstraints.FILL_VERTICAL,
            1,
            GridConstraints.SIZEPOLICY_WANT_GROW,
            null,
            null,
            null,
            0,
            false));
    Font editApplicationSettingsFont =
        this.$$$getFont$$$(null, -1, -1, editApplicationSettings.getFont());
    if (editApplicationSettingsFont != null) {
      editApplicationSettings.setFont(editApplicationSettingsFont);
    }
    editApplicationSettings.setLabel("Programmeinstellungen bearbeiten");
    editApplicationSettings.setRequestFocusEnabled(false);
    editApplicationSettings.setText("Programmeinstellungen bearbeiten");
    panel6.add(
        editApplicationSettings,
        new GridConstraints(
            1,
            0,
            1,
            1,
            GridConstraints.ANCHOR_CENTER,
            GridConstraints.FILL_HORIZONTAL,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
            GridConstraints.SIZEPOLICY_FIXED,
            null,
            null,
            null,
            0,
            false));
    final Spacer spacer6 = new Spacer();
    menuPanel.add(
        spacer6,
        new GridConstraints(
            3,
            0,
            1,
            1,
            GridConstraints.ANCHOR_CENTER,
            GridConstraints.FILL_VERTICAL,
            1,
            GridConstraints.SIZEPOLICY_WANT_GROW,
            null,
            null,
            null,
            0,
            false));
    menuPanel.add(
        infoPanel.$$$getRootComponent$$$(),
        new GridConstraints(
            0,
            2,
            3,
            1,
            GridConstraints.ANCHOR_CENTER,
            GridConstraints.FILL_BOTH,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
            new Dimension(500, 500),
            new Dimension(500, 500),
            null,
            0,
            false));
    final JPanel panel7 = new JPanel();
    panel7.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
    main.add(
        panel7,
        new GridConstraints(
            1,
            0,
            1,
            1,
            GridConstraints.ANCHOR_CENTER,
            GridConstraints.FILL_BOTH,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
            null,
            null,
            null,
            0,
            false));
    logout = new JButton();
    logout.setText("");
    panel7.add(
        logout,
        new GridConstraints(
            0,
            1,
            1,
            1,
            GridConstraints.ANCHOR_CENTER,
            GridConstraints.FILL_HORIZONTAL,
            GridConstraints.SIZEPOLICY_FIXED,
            GridConstraints.SIZEPOLICY_FIXED,
            null,
            null,
            null,
            0,
            false));
    final Spacer spacer7 = new Spacer();
    panel7.add(
        spacer7,
        new GridConstraints(
            0,
            0,
            1,
            1,
            GridConstraints.ANCHOR_CENTER,
            GridConstraints.FILL_HORIZONTAL,
            GridConstraints.SIZEPOLICY_WANT_GROW,
            1,
            null,
            null,
            null,
            0,
            false));
  }

  /** @noinspection ALL */
  private Font $$$getFont$$$(String fontName, int style, int size, Font currentFont) {
    if (currentFont == null) {
      return null;
    }
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
    Font font =
        new Font(
            resultName,
            style >= 0 ? style : currentFont.getStyle(),
            size >= 0 ? size : currentFont.getSize());
    boolean isMac = System.getProperty("os.name", "").toLowerCase(Locale.ENGLISH).startsWith("mac");
    Font fontWithFallback =
        isMac
            ? new Font(font.getFamily(), font.getStyle(), font.getSize())
            : new StyleContext().getFont(font.getFamily(), font.getStyle(), font.getSize());
    return fontWithFallback instanceof FontUIResource
        ? fontWithFallback
        : new FontUIResource(fontWithFallback);
  }

  /** @noinspection ALL */
  public JComponent $$$getRootComponent$$$() {
    return main;
  }
}
