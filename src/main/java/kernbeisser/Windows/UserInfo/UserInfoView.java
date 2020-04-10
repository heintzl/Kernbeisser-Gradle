package kernbeisser.Windows.UserInfo;

import kernbeisser.CustomComponents.Charts.BuyChart;
import kernbeisser.CustomComponents.ObjectTable.Column;
import kernbeisser.CustomComponents.ObjectTable.ObjectTable;
import kernbeisser.DBEntities.*;
import kernbeisser.Enums.Key;
import kernbeisser.Windows.LogIn.LogInModel;
import kernbeisser.Windows.View;
import kernbeisser.Windows.Window;

import javax.swing.*;
import java.util.Collection;

public class UserInfoView extends JPanel implements View {

    private JPanel main;
    private JTabbedPane tabbedPane;
    private ObjectTable<Purchase> shoppingHistory;
    private ObjectTable<ValueChange> valueHistory;
    private BuyChart buyChart;
    private JLabel phoneNumber1;
    private JLabel username;
    private JLabel firstName;
    private JLabel surname;
    private JLabel email;
    private JLabel phoneNumber2;
    private JLabel townCode;
    private JLabel street;
    private JLabel shares;
    private JLabel solidarySurcharge;
    private JLabel createDate;
    private JLabel updateDate;
    private ObjectTable<Permission> permissions;
    private ObjectTable<Job> jobs;
    private ObjectTable<User> userGroup;

    private UserInfoController controller;

    private Window parent;

    public UserInfoView(Window current, UserInfoController userInfoController) {
        this.controller = userInfoController;
        this.parent = current;
        add(main);
        tabbedPane.addChangeListener(e -> userInfoController.loadCurrentSite());
        shoppingHistory.addSelectionListener(e -> controller.openPurchase());
    }

    void setUserGroupMembers(Collection<User> users){
        userGroup.setObjects(users);
    }

    void setShoppingHistory(Collection<Purchase> purchases){
        this.shoppingHistory.setObjects(purchases);
    }

    void setValueHistory(Collection<ValueChange> valueChanges){
        this.valueHistory.setObjects(valueChanges);
    }

    void setJobs(Collection<Job> jobs){
        this.jobs.setObjects(jobs);
    }

    void setPermissions(Collection<Permission> permissions){
        this.permissions.setObjects(permissions);
    }

    void setValueHistoryColumns(Collection<Column<ValueChange>> columns){
        valueHistory.setColumns(columns);
    }

    public void createUIComponents(){
        valueHistory = new ObjectTable<ValueChange>();
        buyChart = controller.createBuyChart();
        permissions = new ObjectTable<>(Column.create("Name",Permission::getName,Key.PERMISSION_NAME_READ));
        userGroup = new ObjectTable<User>(Column.create("Benutzername",User::getUsername,Key.USER_USERNAME_READ),Column.create("Vorname",User::getFirstName,Key.USER_FIRST_NAME_READ),Column.create("Nachname",User::getSurname,Key.USER_SURNAME_READ));
        jobs = new ObjectTable<Job>(Column.create("Name",Job::getName,Key.JOB_NAME_READ),Column.create("Beschreibung",Job::getDescription,Key.JOB_DESCRIPTION_READ));
        shoppingHistory = new ObjectTable<Purchase>(Column.create("Datum",e -> e.getDate().toString()),Column.create("Verkäufer",e -> e.getSession().getSeller()),Column.create("Käufer",e -> e.getSession().getCustomer()),Column.create("Summe",e -> String.format("%.2f€",e.getSum())));
    }

    void pasteUser(User user){
        phoneNumber1.setText(LogInModel.getLoggedIn().hasPermission(Key.USER_PHONE_NUMBER1_READ) ? user.getPhoneNumber1() : "Kein zugriff");
        username.setText(LogInModel.getLoggedIn().hasPermission(Key.USER_USERNAME_READ) ? user.getUsername() : "Kein zugriff");
        firstName.setText(LogInModel.getLoggedIn().hasPermission(Key.USER_FIRST_NAME_READ) ? user.getFirstName() : "Kein zugriff");
        surname.setText(LogInModel.getLoggedIn().hasPermission(Key.USER_SURNAME_READ) ? user.getSurname() : "Kein zugriff");
        email.setText(LogInModel.getLoggedIn().hasPermission(Key.USER_EMAIL_READ) ? user.getEmail() : "Kein zugriff");
        phoneNumber2.setText(LogInModel.getLoggedIn().hasPermission(Key.USER_PHONE_NUMBER2_READ) ? user.getPhoneNumber2() : "Kein zugriff");
        townCode.setText(LogInModel.getLoggedIn().hasPermission(Key.USER_TOWN_READ) ? String.valueOf(user.getTownCode()) : "Kein zugriff");
        street.setText(LogInModel.getLoggedIn().hasPermission(Key.USER_STREET_READ) ? user.getStreet() : "Kein zugriff");
        shares.setText(LogInModel.getLoggedIn().hasPermission(Key.USER_SHARES_READ) ? String.valueOf(user.getShares()) : "Kein zugriff");
        solidarySurcharge.setText(LogInModel.getLoggedIn().hasPermission(Key.USER_SOLIDARITY_SURCHARGE_READ) ? user.getSolidaritySurcharge()+"" : "Kein zugriff");
        createDate.setText(LogInModel.getLoggedIn().hasPermission(Key.USER_CREATE_DATE_READ) ? user.getCreateDate().toString() : "Kein zugriff");
        updateDate.setText(LogInModel.getLoggedIn().hasPermission(Key.USER_UPDATE_DATE_READ) ? user.getUpdateDate().toString() : "Kein zugriff");
    }

    void pasteWithoutPermissionCheck(User user){
        phoneNumber1.setText(user.getPhoneNumber1());
        username.setText(user.getUsername());
        firstName.setText(user.getFirstName());
        surname.setText(user.getSurname());
        email.setText(user.getEmail());
        phoneNumber2.setText(user.getPhoneNumber2());
        townCode.setText(String.valueOf(user.getTownCode()));
        street.setText(user.getStreet());
        shares.setText(String.valueOf(user.getShares()));
        solidarySurcharge.setText(user.getSolidaritySurcharge()+"");
        createDate.setText(user.getCreateDate().toString());
        updateDate.setText(user.getUpdateDate().toString());
    }

    int getSelectedTabIndex(){
        return tabbedPane.getSelectedIndex();
    }


    public Purchase getSelectedPurchase() {
        return shoppingHistory.getSelectedObject();
    }

    public Window getParentWindow() {
        return parent;
    }
}
