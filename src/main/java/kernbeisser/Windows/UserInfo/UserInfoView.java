package kernbeisser.Windows.UserInfo;

import static java.lang.String.format;
import static kernbeisser.Useful.Tools.optional;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.util.Collection;
import java.util.List;
import javax.swing.*;
import jiconfont.icons.font_awesome.FontAwesome;
import jiconfont.swing.IconFontSwing;
import kernbeisser.CustomComponents.ComboBox.AdvancedComboBox;
import kernbeisser.CustomComponents.ObjectTable.Column;
import kernbeisser.CustomComponents.ObjectTable.ObjectTable;
import kernbeisser.DBEntities.*;
import kernbeisser.Enums.Colors;
import kernbeisser.Enums.Mode;
import kernbeisser.Enums.StatementType;
import kernbeisser.Forms.FormEditor.FormEditorController;
import kernbeisser.Forms.FormImplemetations.User.UserController;
import kernbeisser.Forms.ObjectForm.Components.AccessCheckingLabel;
import kernbeisser.Forms.ObjectForm.ObjectForm;
import kernbeisser.Useful.Date;
import kernbeisser.Useful.Tools;
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
  private kernbeisser.Forms.ObjectForm.Components.AccessCheckingLabel<User> phoneNumber1;
  private kernbeisser.Forms.ObjectForm.Components.AccessCheckingLabel<User> username;
  private kernbeisser.Forms.ObjectForm.Components.AccessCheckingLabel<User> firstName;
  private kernbeisser.Forms.ObjectForm.Components.AccessCheckingLabel<User> surname;
  private kernbeisser.Forms.ObjectForm.Components.AccessCheckingLabel<User> email;
  private kernbeisser.Forms.ObjectForm.Components.AccessCheckingLabel<User> phoneNumber2;
  private kernbeisser.Forms.ObjectForm.Components.AccessCheckingLabel<User> townCode;
  private kernbeisser.Forms.ObjectForm.Components.AccessCheckingLabel<User> street;
  private kernbeisser.Forms.ObjectForm.Components.AccessCheckingLabel<User> shares;
  private kernbeisser.Forms.ObjectForm.Components.AccessCheckingLabel<User> solidarySurcharge;
  private kernbeisser.Forms.ObjectForm.Components.AccessCheckingLabel<User> createDate;
  private kernbeisser.Forms.ObjectForm.Components.AccessCheckingLabel<User> updateDate;
  private ObjectTable<Permission> permissions;
  private ObjectTable<Job> jobs;
  private ObjectTable<User> userGroup;
  private kernbeisser.Forms.ObjectForm.Components.AccessCheckingLabel<User> key;
  private kernbeisser.Forms.ObjectForm.Components.AccessCheckingLabel<User> city;
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
    permissions = new ObjectTable<>(Column.create("Name", Permission::getNeatName));
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
            Column.create("Summe", e -> format("%.2f€", e.getSum()), SwingConstants.RIGHT));
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
    return FormEditorController.open(LogInModel.getLoggedIn(), new UserController(), Mode.EDIT);
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
}
