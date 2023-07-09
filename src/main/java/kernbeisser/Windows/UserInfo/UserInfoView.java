package kernbeisser.Windows.UserInfo;

import static java.lang.String.format;
import static kernbeisser.Useful.Tools.optional;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import javax.swing.*;
import jiconfont.icons.font_awesome.FontAwesome;
import jiconfont.swing.IconFontSwing;
import kernbeisser.CustomComponents.ComboBox.AdvancedComboBox;
import kernbeisser.CustomComponents.ObjectTable.Column;
import kernbeisser.CustomComponents.ObjectTable.Columns.Columns;
import kernbeisser.CustomComponents.ObjectTable.ObjectTable;
import kernbeisser.DBEntities.*;
import kernbeisser.Enums.Colors;
import kernbeisser.Enums.Mode;
import kernbeisser.Enums.StatementType;
import kernbeisser.Enums.TransactionType;
import kernbeisser.Forms.FormEditor.FormEditorController;
import kernbeisser.Forms.FormImplemetations.User.UserController;
import kernbeisser.Forms.ObjectForm.Components.AccessCheckingLabel;
import kernbeisser.Forms.ObjectForm.ObjectForm;
import kernbeisser.Security.Access.Access;
import kernbeisser.Security.Access.AccessManager;
import kernbeisser.Useful.Date;
import kernbeisser.Useful.Tools;
import kernbeisser.Useful.Users;
import kernbeisser.Windows.LogIn.LogInModel;
import kernbeisser.Windows.MVC.IView;
import kernbeisser.Windows.MVC.Linked;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

public class UserInfoView implements IView<UserInfoController> {

  public static final String ACCESS_DENIED = "[Keine Berechtigung]";

  private JPanel main;
  private JTabbedPane tabbedPane;
  private ObjectTable<Purchase> shoppingHistory;
  private ObjectTable<Transaction> valueHistory;
  private AccessCheckingLabel<User> phoneNumber1;
  private AccessCheckingLabel<User> username;
  private AccessCheckingLabel<User> firstName;
  private AccessCheckingLabel<User> surname;
  private AccessCheckingLabel<User> email;
  private AccessCheckingLabel<User> phoneNumber2;
  private AccessCheckingLabel<User> townCode;
  private AccessCheckingLabel<User> street;
  private AccessCheckingLabel<User> shares;
  private AccessCheckingLabel<User> solidarySurcharge;
  private AccessCheckingLabel<User> createDate;
  private AccessCheckingLabel<User> updateDate;
  private ObjectTable<Permission> permissions;
  private ObjectTable<Job> jobs;
  private ObjectTable<User> userGroup;
  private AccessCheckingLabel<User> key;
  private AccessCheckingLabel<User> city;
  private JButton printBon;
  private JRadioButton optCurrent;
  private JRadioButton optLast;
  private JButton printStatement;
  @Getter private JButton editUser;
  private JButton close;
  private AdvancedComboBox<StatementType> statementType;

  @Getter private ObjectForm<User> userObjectForm;

  @Linked private UserInfoController controller;

  void setUserGroupMembers(Collection<User> users) {
    userGroup.setObjects(users);
  }

  void setShoppingHistory(Collection<Purchase> purchases) {
    this.shoppingHistory.setObjects(purchases);
  }

  void setValueHistory(Collection<Transaction> valueChanges) {
    if (valueChanges.size() == 0) {
      Transaction noHistory = new Transaction();
      Access.putException(noHistory, AccessManager.NO_ACCESS_CHECKING);
      noHistory.setInfo("keine Umsätze");
      noHistory.setValue(0.0);
      noHistory.setFromUser(controller.getModel().getUser());
      noHistory.setToUser(controller.getModel().getUser());
      noHistory.setTransactionType(TransactionType.INFO);
      noHistory.setDate(Instant.now());
      Access.removeException(noHistory);
      valueChanges.add(noHistory);
    }
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
    permissions = new ObjectTable<>(Columns.create("Name", Permission::getNeatName));
    userGroup =
        new ObjectTable<User>(
            Columns.create("Benutzername", User::getUsername),
            Columns.create("Vorname", User::getFirstName),
            Columns.create("Nachname", User::getSurname),
            Columns.create("Mitgliedschaft", Users::getMembership));
    jobs =
        new ObjectTable<Job>(
            Columns.create("Name", Job::getName),
            Columns.create("Beschreibung", Job::getDescription));
    shoppingHistory =
        new ObjectTable<Purchase>(
            Columns.<Purchase>create("Datum", e -> Date.INSTANT_DATE_TIME.format(e.getCreateDate()))
                .withSorter(Column.DATE_SORTER(Date.INSTANT_DATE_TIME)),
            Columns.create("Verkäufer", e -> e.getSession().getSeller()),
            Columns.create("Käufer", e -> e.getSession().getCustomer()),
            Columns.create("Summe", e -> format("%.2f€", e.getSum()), SwingConstants.RIGHT));
    phoneNumber1 = new AccessCheckingLabel<>(User::getPhoneNumber1);
    username = new AccessCheckingLabel<>(User::getUsername);
    firstName = new AccessCheckingLabel<>(User::getFirstName);
    surname = new AccessCheckingLabel<>(User::getSurname);
    email = new AccessCheckingLabel<>(User::getEmail);
    phoneNumber2 = new AccessCheckingLabel<>(User::getPhoneNumber2);
    townCode = new AccessCheckingLabel<>(User::getTownCode);
    street = new AccessCheckingLabel<>(User::getStreet);
    shares = new AccessCheckingLabel<>(e -> String.valueOf(e.getShares()));
    solidarySurcharge =
        new AccessCheckingLabel<>(
            e -> String.format("%.1f%%", e.getUserGroup().getSolidaritySurcharge() * 100));
    createDate = new AccessCheckingLabel<>(e -> Date.INSTANT_DATE_TIME.format(e.getCreateDate()));
    updateDate = new AccessCheckingLabel<>(e -> Date.INSTANT_DATE_TIME.format(e.getUpdateDate()));
    key =
        new AccessCheckingLabel<>(
            e -> String.valueOf(e.getKernbeisserKey()).replace("-1", "Kein Schlüssel"));
    city = new AccessCheckingLabel<>(User::getTown);
  }

  FormEditorController<User> generateUserController() {
    return FormEditorController.create(LogInModel.getLoggedIn(), new UserController(), Mode.EDIT);
  }

  int getSelectedTabIndex() {
    return tabbedPane.getSelectedIndex();
  }

  public Optional<Purchase> getSelectedPurchase() {
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
                (StatementType) statementType.getSelectedItem(), optCurrent.isSelected()));
    editUser.addActionListener(e -> controller.editUser());
    editUser.setIcon(
        IconFontSwing.buildIcon(
            FontAwesome.PENCIL, Tools.scaleWithLabelScalingFactor(16), new Color(0xFF00CCFF)));
    close.addActionListener(e -> back());
    userObjectForm =
        new ObjectForm<>(
            phoneNumber1,
            username,
            firstName,
            surname,
            email,
            phoneNumber2,
            townCode,
            street,
            shares,
            solidarySurcharge,
            createDate,
            updateDate,
            key,
            city);
  }

  @Override
  public @NotNull JComponent getContent() {
    return main;
  }

  @Override
  public String getTitle() {
    return "Benutzerinformationen von "
        + optional(controller.getModel().getUser()::getFirstName).orElse(ACCESS_DENIED)
        + ", "
        + optional(controller.getModel().getUser()::getSurname).orElse(ACCESS_DENIED);
  }

  public void setOptCurrentSelected(boolean b) {
    optCurrent.setSelected(b);
  }

  public void setTransactionStatementTypeItems(List<StatementType> asList) {
    statementType.setItems(asList);
  }

  public void messageSelectPurchaseFirst() {
    JOptionPane.showMessageDialog(getContent(), "Bitte wähle zunächst einen Einkauf aus.");
  }
}
