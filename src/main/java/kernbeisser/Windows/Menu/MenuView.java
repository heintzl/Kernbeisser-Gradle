package kernbeisser.Windows.Menu;

import kernbeisser.CustomComponents.ControllerButton;
import kernbeisser.DBEntities.User;
import kernbeisser.Enums.Mode;
import kernbeisser.StartUp.LogIn.DBLogInController;
import kernbeisser.Windows.CashierShoppingMask.CashierShoppingMaskController;
import kernbeisser.Windows.ChangePassword.ChangePasswordController;
import kernbeisser.Windows.Container.ContainerController;
import kernbeisser.Windows.Controller;
import kernbeisser.Windows.EditItems.EditItemsController;
import kernbeisser.Windows.EditSurchargeTables.EditSurchargeTables;
import kernbeisser.Windows.EditUser.EditUserController;
import kernbeisser.Windows.EditUsers.EditUsers;
import kernbeisser.Windows.LogIn.LogInModel;
import kernbeisser.Windows.ManagePriceLists.ManagePriceListsController;
import kernbeisser.Windows.PermissionManagement.PermissionController;
import kernbeisser.Windows.Setting.SettingController;
import kernbeisser.Windows.SoloShoppingMask.SoloShoppingMaskController;
import kernbeisser.Windows.Trasaction.TransactionController;
import kernbeisser.Windows.UserInfo.UserInfoController;
import kernbeisser.Windows.UserInfo.UserInfoView;
import kernbeisser.Windows.View;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class MenuView implements View<MenuController> {


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
    private ControllerButton placeHolderControllerButton;
    private ControllerButton changeDBConnection;
    private ControllerButton editApplicationSettings;
    private ControllerButton order;
    private ControllerButton placeHolderControllerButton1;
    private ControllerButton placeHolderControllerButton2;
    private ControllerButton openSelfShoppingMask;
    private ControllerButton addBeginner;

    @Override
    public void initialize(MenuController controller) {

    }

    @Override
    public @NotNull JComponent getContent() {
        return main;
    }


    private void createUIComponents() {
        infoPanel = new UserInfoController(LogInModel.getLoggedIn()).getInitializedView();
        openCashierShoppingMask = new ControllerButton(new CashierShoppingMaskController(),
                                                       controller -> controller.openTab("Ladendienst"));
        //NOT IMPLEMENTED
        printBonFromPast = new ControllerButton(Controller.createFakeController(new JPanel()),
                                                controller -> controller.openTab("<PlaceHolder>"));
        editPriceList = new ControllerButton(new ManagePriceListsController(),
                                             controller -> controller.openTab("Preislisten bearbeiten"));
        editArticles = new ControllerButton(new EditItemsController(),
                                            controller -> controller.openTab("Artikel bearbeiten"));
        editSurchargeTables = new ControllerButton(new EditSurchargeTables(),
                                                   controller -> controller.openTab("Zuschlagstabellen bearbeiten"));
        changePassword = new ControllerButton(new ChangePasswordController(LogInModel.getLoggedIn(), true),
                                              controller -> controller.openTab("<PlaceHolder>"));
        transactionHistory = new ControllerButton(new UserInfoController(LogInModel.getLoggedIn()),
                                                  controller -> controller.openTab(""));
        editOwnUser = new ControllerButton(new EditUserController(LogInModel.getLoggedIn(), Mode.EDIT),
                                           controller -> controller.openTab("<PlaceHolder>"));
        //NOT IMPLEMENTED
        editUserSettings = new ControllerButton(Controller.createFakeController(new JPanel()),
                                                controller -> controller.openTab("<PlaceHolder>"));
        editUsers = new ControllerButton(new EditUsers(), controller -> controller.openTab("<PlaceHolder>"));
        doTransaction = new ControllerButton(new TransactionController(LogInModel.getLoggedIn()),
                                             controller -> controller.openTab("<PlaceHolder>"));
        changePermissions = new ControllerButton(new PermissionController(),
                                                 controller -> controller.openTab("<PlaceHolder>"));
        //NOT IMPLEMENTED
        placeHolderControllerButton = new ControllerButton(Controller.createFakeController(new JPanel()),
                                                           controller -> controller.openTab("<PlaceHolder>"));
        changeDBConnection = new ControllerButton(new DBLogInController(),
                                                  controller -> controller.openTab("<PlaceHolder>"));
        //NOT IMPLEMENTED
        editApplicationSettings = new ControllerButton(new SettingController(), e -> e.openTab("Einstellungen"));
        order = new ControllerButton(new ContainerController(LogInModel.getLoggedIn()),
                                     controller -> controller.openTab("<PlaceHolder>"));
        //NOT IMPLEMENTED
        placeHolderControllerButton1 = new ControllerButton(Controller.createFakeController(new JPanel()),
                                                            controller -> controller.openTab("<PlaceHolder>"));
        //NOT IMPLEMENTED
        placeHolderControllerButton2 = new ControllerButton(Controller.createFakeController(new JPanel()),
                                                            controller -> controller.openTab("<PlaceHolder>"));
        openSelfShoppingMask = new ControllerButton(new SoloShoppingMaskController(),
                                                    controller -> controller.openTab("<PlaceHolder>"));
        addBeginner = new ControllerButton(new EditUserController(User.generateBeginnerUser(), Mode.ADD),
                                           e -> e.openTab("Probemitglied aufnehmen"));
        //TODO make focus on button work
        openCashierShoppingMask.requestFocusInWindow();
        //Releasesettings
        printBonFromPast.setEnabled(false);
        order.setEnabled(false);
        editPriceList.setEnabled(false);
        editSurchargeTables.setEnabled(false);
        editUserSettings.setEnabled(false);
        placeHolderControllerButton.setEnabled(false);
        placeHolderControllerButton1.setEnabled(false);
        placeHolderControllerButton2.setEnabled(false);
        //Releasesettings
    }
}
