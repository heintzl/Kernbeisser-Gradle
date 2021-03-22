package kernbeisser.Windows.UserInfo;

import static java.lang.String.format;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.util.Collection;
import javax.swing.*;
import kernbeisser.CustomComponents.ObjectTable.Column;
import kernbeisser.CustomComponents.ObjectTable.ObjectTable;
import kernbeisser.DBEntities.*;
import kernbeisser.Enums.Colors;
import kernbeisser.Enums.PermissionKey;
import kernbeisser.Enums.StatementType;
import kernbeisser.Useful.Date;
import kernbeisser.Windows.LogIn.LogInModel;
import kernbeisser.Windows.MVC.IView;
import kernbeisser.Windows.MVC.Linked;
import org.jetbrains.annotations.NotNull;

public class UserInfoView implements IView<UserInfoController> {

  private JPanel main;
  private JTabbedPane tabbedPane;
  private ObjectTable<Purchase> shoppingHistory;
  private ObjectTable<Transaction> valueHistory;
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
  private JLabel key;
  private JLabel city;
  private JButton printBon;
  JComboBox transactionStatementType;
  JRadioButton optCurrent;
  private JRadioButton optLast;
  private JButton printStatement;
  private JTextPane infoText;

  @Linked private UserInfoController controller;

  void setUserGroupMembers(Collection<User> users) {
    userGroup.setObjects(users);
  }

  void setShoppingHistory(Collection<Purchase> purchases) {
    this.shoppingHistory.setObjects(purchases);
  }

  void setValueHistory(Collection<Transaction> valueChanges) {
    this.valueHistory.setObjects(valueChanges);
  }

  void setJobs(Collection<Job> jobs) {
    this.jobs.setObjects(jobs);
  }

  void setPermissions(Collection<Permission> permissions) {
    this.permissions.setObjects(permissions);
  }

  void setValueHistoryColumns(Collection<Column<Transaction>> columns) {
    valueHistory.setColumns(columns);
  }

  public void createUIComponents() {
    valueHistory = new ObjectTable<Transaction>();
    permissions = new ObjectTable<>(Column.create("Name", Permission::getName));
    userGroup =
        new ObjectTable<User>(
            Column.create("Benutzername", User::getUsername),
            Column.create("Vorname", User::getFirstName),
            Column.create("Nachname", User::getSurname));
    jobs =
        new ObjectTable<Job>(
            Column.create("Name", Job::getName),
            Column.create("Beschreibung", Job::getDescription));
    shoppingHistory =
        new ObjectTable<Purchase>(
            Column.create("Datum", e -> Date.INSTANT_DATE_TIME.format(e.getCreateDate())),
            Column.create("Verkäufer", e -> e.getSession().getSeller()),
            Column.create("Käufer", e -> e.getSession().getCustomer()),
            Column.create("Summe", e -> format("%.2f€", e.getSum())));
  }

  void pasteUser(User user) {
    phoneNumber1.setText(
        LogInModel.getLoggedIn().hasPermission(PermissionKey.USER_PHONE_NUMBER1_READ)
            ? user.getPhoneNumber1()
            : "Kein zugriff");
    username.setText(
        LogInModel.getLoggedIn().hasPermission(PermissionKey.USER_USERNAME_READ)
            ? user.getUsername()
            : "Kein zugriff");
    firstName.setText(
        LogInModel.getLoggedIn().hasPermission(PermissionKey.USER_FIRST_NAME_READ)
            ? user.getFirstName()
            : "Kein zugriff");
    surname.setText(
        LogInModel.getLoggedIn().hasPermission(PermissionKey.USER_SURNAME_READ)
            ? user.getSurname()
            : "Kein zugriff");
    email.setText(
        LogInModel.getLoggedIn().hasPermission(PermissionKey.USER_EMAIL_READ)
            ? user.getEmail()
            : "Kein zugriff");
    phoneNumber2.setText(
        LogInModel.getLoggedIn().hasPermission(PermissionKey.USER_PHONE_NUMBER2_READ)
            ? user.getPhoneNumber2()
            : "Kein zugriff");
    townCode.setText(
        LogInModel.getLoggedIn().hasPermission(PermissionKey.USER_TOWN_READ)
            ? String.valueOf(user.getTownCode())
            : "Kein zugriff");
    street.setText(
        LogInModel.getLoggedIn().hasPermission(PermissionKey.USER_STREET_READ)
            ? user.getStreet()
            : "Kein zugriff");
    shares.setText(
        LogInModel.getLoggedIn().hasPermission(PermissionKey.USER_SHARES_READ)
            ? String.valueOf(user.getShares())
            : "Kein zugriff");
    solidarySurcharge.setText(
        LogInModel.getLoggedIn().hasPermission(PermissionKey.USER_GROUP_SOLIDARITY_SURCHARGE_READ)
            ? format("%.1f%%", user.getUserGroup().getSolidaritySurcharge() * 100)
            : "Kein zugriff");
    createDate.setText(
        LogInModel.getLoggedIn().hasPermission(PermissionKey.USER_CREATE_DATE_READ)
            ? Date.INSTANT_DATE_TIME.format(user.getCreateDate())
            : "Kein zugriff");
    updateDate.setText(
        LogInModel.getLoggedIn().hasPermission(PermissionKey.USER_UPDATE_DATE_READ)
            ? Date.INSTANT_DATE_TIME.format(user.getUpdateDate())
            : "Kein zugriff");
    key.setText(
        LogInModel.getLoggedIn().hasPermission(PermissionKey.USER_KERNBEISSER_KEY_READ)
            ? user.getKernbeisserKey() == -1 ? "Kein Schlüssel" : user.getKernbeisserKey() + ""
            : "Kein zugriff");
    city.setText(
        LogInModel.getLoggedIn().hasPermission(PermissionKey.USER_TOWN_READ)
            ? user.getTown()
            : "Kein Zugriff");
  }

  void pasteWithoutPermissionCheck(User user) {
    phoneNumber1.setText(user.getPhoneNumber1());
    username.setText(user.getUsername());
    firstName.setText(user.getFirstName());
    surname.setText(user.getSurname());
    email.setText(user.getEmail());
    phoneNumber2.setText(user.getPhoneNumber2());
    townCode.setText(String.valueOf(user.getTownCode()));
    street.setText(user.getStreet());
    shares.setText(String.valueOf(user.getShares()));
    solidarySurcharge.setText(format("%.1f%%", user.getUserGroup().getSolidaritySurcharge()));
    createDate.setText(Date.INSTANT_DATE_TIME.format(user.getCreateDate()));
    updateDate.setText(Date.INSTANT_DATE_TIME.format(user.getUpdateDate()));
    key.setText(user.getKernbeisserKey() == -1 ? "Kein Schlüssel" : user.getKernbeisserKey() + "");
    city.setText(user.getTown());
  }

  int getSelectedTabIndex() {
    return tabbedPane.getSelectedIndex();
  }

  public Purchase getSelectedPurchase() {
    return shoppingHistory.getSelectedObject();
  }

  private String getBuildDate() {
    try {
      File jarFile =
          new File(this.getClass().getProtectionDomain().getCodeSource().getLocation().toURI());
      return Date.INSTANT_DATE.format(Files.getLastModifiedTime(jarFile.toPath()).toInstant());
    } catch (IOException | URISyntaxException e) {
      return "(nicht gefunden)";
    }
  }

  @Override
  public void initialize(UserInfoController controller) {
    for (Component component : firstName.getParent().getComponents()) {
      if (component instanceof JLabel) {
        component.setForeground(Colors.LABEL_FOREGROUND.getColor());
      }
    }
    tabbedPane.addChangeListener(e -> controller.loadCurrentSite());
    shoppingHistory.addDoubleClickListener(e -> controller.openPurchase());
    shoppingHistory.addSelectionListener(
        e -> {
          printBon.setEnabled(true);
        });
    printBon.setEnabled(false);
    printBon.addActionListener(e -> controller.openPurchase());
    printStatement.addActionListener(
        e ->
            controller.printStatement(
                (StatementType) transactionStatementType.getSelectedItem(),
                optCurrent.isSelected()));
    infoText.setEditorKit(new javax.swing.text.html.HTMLEditorKit());
    infoText.setEditable(false);
    infoText.setText(
        "<HTML><BODY>"
            + "<table border=\"0\">"
            + "<tr><td colspan=\"2\"><h1>Kernbeißer Ladenprogramm</h1></td></tr>"
            + "<tr><td valign=\"top\"><i>Beschreibung:</i></td>"
            + "<td>Dieses Programm wurde für den Ladenbetrieb der Kernbeißer Verbraucher-Erzeuger-Genossenschaft"
            + " in Braunschweig (https://www.kernbeisser-bs.de) entwickelt. "
            + "Es wurde in Java als quelloffene Software implementiert.</td></tr>"
            + "<tr><td><i>Sourcecode:</i></td><td><a href=\"https://github.com/julikiller98/Kernbeisser-Gradle\">"
            + "https://github.com/julikiller98/Kernbeisser-Gradle</a></td></tr>"
            + "<tr><td><div><i>Erstellt am:</i></td><td>"
            + getBuildDate()
            + "</td></tr>"
            + "</table>"
            + "<BODY></HTML>");
  }

  @Override
  public @NotNull JComponent getContent() {
    return main;
  }

  @Override
  public @NotNull Dimension getSize() {
    return new Dimension(500, 500);
  }

  @Override
  public String getTitle() {
    return "Benutzerinformationen von "
        + controller.getModel().getUser().getFirstName()
        + ", "
        + controller.getModel().getUser().getSurname();
  }
}
