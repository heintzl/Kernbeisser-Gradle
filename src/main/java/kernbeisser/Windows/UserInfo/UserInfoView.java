package kernbeisser.Windows.UserInfo;

import static java.lang.String.format;
import static kernbeisser.Useful.Tools.optional;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import javax.swing.*;
import javax.swing.plaf.FontUIResource;
import javax.swing.text.StyleContext;
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
    valueHistory =
        new ObjectTable<Transaction>(
            Columns.create("Art", Transaction::getTransactionType),
            Columns.create("Von", t -> t.getFromUser().getFullName()),
            Columns.create("An", t -> t.getToUser().getFullName()),
            Columns.<Transaction>create(
                    "Eingang",
                    e ->
                        controller.getModel().incoming(e)
                            ? String.format("%.2f€", e.getValue())
                            : "")
                .withSorter(Column.NUMBER_SORTER),
            Columns.<Transaction>create(
                    "Ausgang",
                    e ->
                        controller.getModel().incoming(e)
                            ? ""
                            : String.format("%.2f€", e.getValue()))
                .withSorter(Column.NUMBER_SORTER),
            Columns.<Transaction>create(
                    "Verbleibend", t -> String.format("%.2f€", controller.getTransactionSum(t)))
                .withSorter(Column.NUMBER_SORTER),
            Columns.create("Info", Transaction::getInfo),
            Columns.<Transaction>create("Datum", t -> Date.INSTANT_DATE_TIME.format(t.getDate()))
                .withSorter(Column.DATE_SORTER(Date.INSTANT_DATE_TIME)));

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
    main.setLayout(new GridLayoutManager(1, 5, new Insets(0, 0, 0, 0), -1, -1));
    tabbedPane = new JTabbedPane();
    main.add(
        tabbedPane,
        new GridConstraints(
            0,
            0,
            1,
            5,
            GridConstraints.ANCHOR_CENTER,
            GridConstraints.FILL_BOTH,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
            null,
            new Dimension(200, 200),
            null,
            0,
            false));
    final JPanel panel1 = new JPanel();
    panel1.setLayout(new GridLayoutManager(13, 2, new Insets(5, 5, 5, 5), -1, -1));
    tabbedPane.addTab("Mitgliedsdaten", panel1);
    final JScrollPane scrollPane1 = new JScrollPane();
    panel1.add(
        scrollPane1,
        new GridConstraints(
            6,
            1,
            3,
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
    scrollPane1.setViewportView(jobs);
    final JPanel panel2 = new JPanel();
    panel2.setLayout(new GridLayoutManager(17, 2, new Insets(0, 0, 0, 0), -1, -1));
    panel1.add(
        panel2,
        new GridConstraints(
            0,
            0,
            13,
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
    final JLabel label1 = new JLabel();
    Font label1Font = this.$$$getFont$$$(null, -1, -1, label1.getFont());
    if (label1Font != null) label1.setFont(label1Font);
    label1.setForeground(new Color(-4473925));
    label1.setText("Nutzername:");
    panel2.add(
        label1,
        new GridConstraints(
            0,
            0,
            1,
            1,
            GridConstraints.ANCHOR_WEST,
            GridConstraints.FILL_NONE,
            GridConstraints.SIZEPOLICY_FIXED,
            GridConstraints.SIZEPOLICY_FIXED,
            null,
            null,
            null,
            0,
            false));
    final JLabel label2 = new JLabel();
    Font label2Font = this.$$$getFont$$$(null, -1, -1, label2.getFont());
    if (label2Font != null) label2.setFont(label2Font);
    label2.setForeground(new Color(-4473925));
    label2.setText("Vorname:");
    panel2.add(
        label2,
        new GridConstraints(
            1,
            0,
            1,
            1,
            GridConstraints.ANCHOR_WEST,
            GridConstraints.FILL_NONE,
            GridConstraints.SIZEPOLICY_FIXED,
            GridConstraints.SIZEPOLICY_FIXED,
            null,
            null,
            null,
            0,
            false));
    final JLabel label3 = new JLabel();
    Font label3Font = this.$$$getFont$$$(null, -1, -1, label3.getFont());
    if (label3Font != null) label3.setFont(label3Font);
    label3.setForeground(new Color(-4473925));
    label3.setText("Nachname:");
    panel2.add(
        label3,
        new GridConstraints(
            2,
            0,
            1,
            1,
            GridConstraints.ANCHOR_WEST,
            GridConstraints.FILL_NONE,
            GridConstraints.SIZEPOLICY_FIXED,
            GridConstraints.SIZEPOLICY_FIXED,
            null,
            null,
            null,
            0,
            false));
    final JLabel label4 = new JLabel();
    Font label4Font = this.$$$getFont$$$(null, -1, -1, label4.getFont());
    if (label4Font != null) label4.setFont(label4Font);
    label4.setForeground(new Color(-4473925));
    label4.setText("E-Mail:");
    panel2.add(
        label4,
        new GridConstraints(
            3,
            0,
            1,
            1,
            GridConstraints.ANCHOR_WEST,
            GridConstraints.FILL_NONE,
            GridConstraints.SIZEPOLICY_FIXED,
            GridConstraints.SIZEPOLICY_FIXED,
            null,
            null,
            null,
            0,
            false));
    final JLabel label5 = new JLabel();
    Font label5Font = this.$$$getFont$$$(null, -1, -1, label5.getFont());
    if (label5Font != null) label5.setFont(label5Font);
    label5.setForeground(new Color(-4473925));
    label5.setText("Telefonnummer 1:");
    panel2.add(
        label5,
        new GridConstraints(
            4,
            0,
            1,
            1,
            GridConstraints.ANCHOR_WEST,
            GridConstraints.FILL_NONE,
            GridConstraints.SIZEPOLICY_FIXED,
            GridConstraints.SIZEPOLICY_FIXED,
            null,
            null,
            null,
            0,
            false));
    final JLabel label6 = new JLabel();
    Font label6Font = this.$$$getFont$$$(null, -1, -1, label6.getFont());
    if (label6Font != null) label6.setFont(label6Font);
    label6.setForeground(new Color(-4473925));
    label6.setText("Telefonnummer 2:");
    panel2.add(
        label6,
        new GridConstraints(
            5,
            0,
            1,
            1,
            GridConstraints.ANCHOR_WEST,
            GridConstraints.FILL_NONE,
            GridConstraints.SIZEPOLICY_FIXED,
            GridConstraints.SIZEPOLICY_FIXED,
            null,
            null,
            null,
            0,
            false));
    final JLabel label7 = new JLabel();
    Font label7Font = this.$$$getFont$$$(null, -1, -1, label7.getFont());
    if (label7Font != null) label7.setFont(label7Font);
    label7.setForeground(new Color(-4473925));
    label7.setText("Postleitzahl:");
    panel2.add(
        label7,
        new GridConstraints(
            6,
            0,
            1,
            1,
            GridConstraints.ANCHOR_WEST,
            GridConstraints.FILL_NONE,
            GridConstraints.SIZEPOLICY_FIXED,
            GridConstraints.SIZEPOLICY_FIXED,
            null,
            null,
            null,
            0,
            false));
    final JLabel label8 = new JLabel();
    Font label8Font = this.$$$getFont$$$(null, -1, -1, label8.getFont());
    if (label8Font != null) label8.setFont(label8Font);
    label8.setForeground(new Color(-4473925));
    label8.setText("Straße:");
    panel2.add(
        label8,
        new GridConstraints(
            8,
            0,
            1,
            1,
            GridConstraints.ANCHOR_WEST,
            GridConstraints.FILL_NONE,
            GridConstraints.SIZEPOLICY_FIXED,
            GridConstraints.SIZEPOLICY_FIXED,
            null,
            null,
            null,
            0,
            false));
    final JLabel label9 = new JLabel();
    Font label9Font = this.$$$getFont$$$(null, -1, -1, label9.getFont());
    if (label9Font != null) label9.setFont(label9Font);
    label9.setForeground(new Color(-4473925));
    label9.setText("Anteile:");
    panel2.add(
        label9,
        new GridConstraints(
            9,
            0,
            1,
            1,
            GridConstraints.ANCHOR_WEST,
            GridConstraints.FILL_NONE,
            GridConstraints.SIZEPOLICY_FIXED,
            GridConstraints.SIZEPOLICY_FIXED,
            null,
            null,
            null,
            0,
            false));
    final JLabel label10 = new JLabel();
    Font label10Font = this.$$$getFont$$$(null, -1, -1, label10.getFont());
    if (label10Font != null) label10.setFont(label10Font);
    label10.setForeground(new Color(-4473925));
    label10.setText("Solidarzuschlag:");
    panel2.add(
        label10,
        new GridConstraints(
            11,
            0,
            1,
            1,
            GridConstraints.ANCHOR_WEST,
            GridConstraints.FILL_NONE,
            GridConstraints.SIZEPOLICY_FIXED,
            GridConstraints.SIZEPOLICY_FIXED,
            null,
            null,
            null,
            0,
            false));
    final JLabel label11 = new JLabel();
    Font label11Font = this.$$$getFont$$$(null, -1, -1, label11.getFont());
    if (label11Font != null) label11.setFont(label11Font);
    label11.setForeground(new Color(-4473925));
    label11.setText("Erstellungsdatum:");
    panel2.add(
        label11,
        new GridConstraints(
            12,
            0,
            1,
            1,
            GridConstraints.ANCHOR_WEST,
            GridConstraints.FILL_NONE,
            GridConstraints.SIZEPOLICY_FIXED,
            GridConstraints.SIZEPOLICY_FIXED,
            null,
            null,
            null,
            0,
            false));
    final JLabel label12 = new JLabel();
    Font label12Font = this.$$$getFont$$$(null, -1, -1, label12.getFont());
    if (label12Font != null) label12.setFont(label12Font);
    label12.setForeground(new Color(-4473925));
    label12.setText("Bearbeitungsdatum:");
    panel2.add(
        label12,
        new GridConstraints(
            13,
            0,
            1,
            1,
            GridConstraints.ANCHOR_WEST,
            GridConstraints.FILL_NONE,
            GridConstraints.SIZEPOLICY_FIXED,
            GridConstraints.SIZEPOLICY_FIXED,
            null,
            null,
            null,
            0,
            false));
    Font surnameFont = this.$$$getFont$$$(null, -1, -1, surname.getFont());
    if (surnameFont != null) surname.setFont(surnameFont);
    surname.setForeground(new Color(-4473925));
    surname.setText("surname");
    panel2.add(
        surname,
        new GridConstraints(
            2,
            1,
            1,
            1,
            GridConstraints.ANCHOR_WEST,
            GridConstraints.FILL_NONE,
            GridConstraints.SIZEPOLICY_FIXED,
            GridConstraints.SIZEPOLICY_FIXED,
            null,
            null,
            null,
            0,
            false));
    Font firstNameFont = this.$$$getFont$$$(null, -1, -1, firstName.getFont());
    if (firstNameFont != null) firstName.setFont(firstNameFont);
    firstName.setForeground(new Color(-4473925));
    firstName.setText("firstName");
    panel2.add(
        firstName,
        new GridConstraints(
            1,
            1,
            1,
            1,
            GridConstraints.ANCHOR_WEST,
            GridConstraints.FILL_NONE,
            GridConstraints.SIZEPOLICY_FIXED,
            GridConstraints.SIZEPOLICY_FIXED,
            null,
            null,
            null,
            0,
            false));
    Font usernameFont = this.$$$getFont$$$(null, -1, -1, username.getFont());
    if (usernameFont != null) username.setFont(usernameFont);
    username.setForeground(new Color(-4473925));
    username.setText("username");
    panel2.add(
        username,
        new GridConstraints(
            0,
            1,
            1,
            1,
            GridConstraints.ANCHOR_WEST,
            GridConstraints.FILL_NONE,
            GridConstraints.SIZEPOLICY_FIXED,
            GridConstraints.SIZEPOLICY_FIXED,
            null,
            null,
            null,
            0,
            false));
    Font emailFont = this.$$$getFont$$$(null, -1, -1, email.getFont());
    if (emailFont != null) email.setFont(emailFont);
    email.setForeground(new Color(-4473925));
    email.setText("email");
    panel2.add(
        email,
        new GridConstraints(
            3,
            1,
            1,
            1,
            GridConstraints.ANCHOR_WEST,
            GridConstraints.FILL_NONE,
            GridConstraints.SIZEPOLICY_FIXED,
            GridConstraints.SIZEPOLICY_FIXED,
            null,
            null,
            null,
            0,
            false));
    Font phoneNumber1Font = this.$$$getFont$$$(null, -1, -1, phoneNumber1.getFont());
    if (phoneNumber1Font != null) phoneNumber1.setFont(phoneNumber1Font);
    phoneNumber1.setForeground(new Color(-4473925));
    phoneNumber1.setText("tele1");
    panel2.add(
        phoneNumber1,
        new GridConstraints(
            4,
            1,
            1,
            1,
            GridConstraints.ANCHOR_WEST,
            GridConstraints.FILL_NONE,
            GridConstraints.SIZEPOLICY_FIXED,
            GridConstraints.SIZEPOLICY_FIXED,
            null,
            null,
            null,
            0,
            false));
    Font phoneNumber2Font = this.$$$getFont$$$(null, -1, -1, phoneNumber2.getFont());
    if (phoneNumber2Font != null) phoneNumber2.setFont(phoneNumber2Font);
    phoneNumber2.setForeground(new Color(-4473925));
    phoneNumber2.setText("tele2");
    panel2.add(
        phoneNumber2,
        new GridConstraints(
            5,
            1,
            1,
            1,
            GridConstraints.ANCHOR_WEST,
            GridConstraints.FILL_NONE,
            GridConstraints.SIZEPOLICY_FIXED,
            GridConstraints.SIZEPOLICY_FIXED,
            null,
            null,
            null,
            0,
            false));
    Font townCodeFont = this.$$$getFont$$$(null, -1, -1, townCode.getFont());
    if (townCodeFont != null) townCode.setFont(townCodeFont);
    townCode.setForeground(new Color(-4473925));
    townCode.setText("postalcode");
    panel2.add(
        townCode,
        new GridConstraints(
            6,
            1,
            1,
            1,
            GridConstraints.ANCHOR_WEST,
            GridConstraints.FILL_NONE,
            GridConstraints.SIZEPOLICY_FIXED,
            GridConstraints.SIZEPOLICY_FIXED,
            null,
            null,
            null,
            0,
            false));
    Font streetFont = this.$$$getFont$$$(null, -1, -1, street.getFont());
    if (streetFont != null) street.setFont(streetFont);
    street.setForeground(new Color(-4473925));
    street.setText("street");
    panel2.add(
        street,
        new GridConstraints(
            8,
            1,
            1,
            1,
            GridConstraints.ANCHOR_WEST,
            GridConstraints.FILL_NONE,
            GridConstraints.SIZEPOLICY_FIXED,
            GridConstraints.SIZEPOLICY_FIXED,
            null,
            null,
            null,
            0,
            false));
    Font sharesFont = this.$$$getFont$$$(null, -1, -1, shares.getFont());
    if (sharesFont != null) shares.setFont(sharesFont);
    shares.setForeground(new Color(-4473925));
    shares.setText("shares");
    panel2.add(
        shares,
        new GridConstraints(
            9,
            1,
            1,
            1,
            GridConstraints.ANCHOR_WEST,
            GridConstraints.FILL_NONE,
            GridConstraints.SIZEPOLICY_FIXED,
            GridConstraints.SIZEPOLICY_FIXED,
            null,
            null,
            null,
            0,
            false));
    Font solidarySurchargeFont = this.$$$getFont$$$(null, -1, -1, solidarySurcharge.getFont());
    if (solidarySurchargeFont != null) solidarySurcharge.setFont(solidarySurchargeFont);
    solidarySurcharge.setForeground(new Color(-4473925));
    solidarySurcharge.setText("solidarySurcharge");
    panel2.add(
        solidarySurcharge,
        new GridConstraints(
            11,
            1,
            1,
            1,
            GridConstraints.ANCHOR_WEST,
            GridConstraints.FILL_NONE,
            GridConstraints.SIZEPOLICY_FIXED,
            GridConstraints.SIZEPOLICY_FIXED,
            null,
            null,
            null,
            0,
            false));
    Font createDateFont = this.$$$getFont$$$(null, -1, -1, createDate.getFont());
    if (createDateFont != null) createDate.setFont(createDateFont);
    createDate.setForeground(new Color(-4473925));
    createDate.setText("createDate");
    panel2.add(
        createDate,
        new GridConstraints(
            12,
            1,
            1,
            1,
            GridConstraints.ANCHOR_WEST,
            GridConstraints.FILL_NONE,
            GridConstraints.SIZEPOLICY_FIXED,
            GridConstraints.SIZEPOLICY_FIXED,
            null,
            null,
            null,
            0,
            false));
    Font updateDateFont = this.$$$getFont$$$(null, -1, -1, updateDate.getFont());
    if (updateDateFont != null) updateDate.setFont(updateDateFont);
    updateDate.setForeground(new Color(-4473925));
    updateDate.setText("updateDate");
    panel2.add(
        updateDate,
        new GridConstraints(
            13,
            1,
            1,
            1,
            GridConstraints.ANCHOR_WEST,
            GridConstraints.FILL_NONE,
            GridConstraints.SIZEPOLICY_FIXED,
            GridConstraints.SIZEPOLICY_FIXED,
            null,
            null,
            null,
            0,
            false));
    final Spacer spacer1 = new Spacer();
    panel2.add(
        spacer1,
        new GridConstraints(
            15,
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
    final JLabel label13 = new JLabel();
    label13.setText("Ort:");
    panel2.add(
        label13,
        new GridConstraints(
            7,
            0,
            1,
            1,
            GridConstraints.ANCHOR_WEST,
            GridConstraints.FILL_NONE,
            GridConstraints.SIZEPOLICY_FIXED,
            GridConstraints.SIZEPOLICY_FIXED,
            null,
            null,
            null,
            0,
            false));
    city.setText("city");
    panel2.add(
        city,
        new GridConstraints(
            7,
            1,
            1,
            1,
            GridConstraints.ANCHOR_WEST,
            GridConstraints.FILL_NONE,
            GridConstraints.SIZEPOLICY_FIXED,
            GridConstraints.SIZEPOLICY_FIXED,
            null,
            null,
            null,
            0,
            false));
    final JLabel label14 = new JLabel();
    label14.setText("Schlüsselbesitz:");
    panel2.add(
        label14,
        new GridConstraints(
            10,
            0,
            1,
            1,
            GridConstraints.ANCHOR_WEST,
            GridConstraints.FILL_NONE,
            GridConstraints.SIZEPOLICY_FIXED,
            GridConstraints.SIZEPOLICY_FIXED,
            null,
            null,
            null,
            0,
            false));
    key.setText("key");
    panel2.add(
        key,
        new GridConstraints(
            10,
            1,
            1,
            1,
            GridConstraints.ANCHOR_WEST,
            GridConstraints.FILL_NONE,
            GridConstraints.SIZEPOLICY_FIXED,
            GridConstraints.SIZEPOLICY_FIXED,
            null,
            null,
            null,
            0,
            false));
    close = new JButton();
    close.setText("Schließen");
    panel2.add(
        close,
        new GridConstraints(
            16,
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
    editUser = new JButton();
    editUser.setText("Bearbeiten...");
    panel2.add(
        editUser,
        new GridConstraints(
            14,
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
    final JLabel label15 = new JLabel();
    label15.setText("Jobs");
    panel1.add(
        label15,
        new GridConstraints(
            5,
            1,
            1,
            1,
            GridConstraints.ANCHOR_WEST,
            GridConstraints.FILL_NONE,
            GridConstraints.SIZEPOLICY_FIXED,
            GridConstraints.SIZEPOLICY_FIXED,
            null,
            null,
            null,
            0,
            false));
    final JLabel label16 = new JLabel();
    label16.setText("Berechtigungen");
    panel1.add(
        label16,
        new GridConstraints(
            9,
            1,
            1,
            1,
            GridConstraints.ANCHOR_WEST,
            GridConstraints.FILL_NONE,
            GridConstraints.SIZEPOLICY_FIXED,
            GridConstraints.SIZEPOLICY_FIXED,
            null,
            null,
            null,
            0,
            false));
    final JLabel label17 = new JLabel();
    label17.setText("Nutzergruppe");
    panel1.add(
        label17,
        new GridConstraints(
            0,
            1,
            1,
            1,
            GridConstraints.ANCHOR_WEST,
            GridConstraints.FILL_NONE,
            GridConstraints.SIZEPOLICY_FIXED,
            GridConstraints.SIZEPOLICY_FIXED,
            null,
            null,
            null,
            0,
            false));
    final JScrollPane scrollPane2 = new JScrollPane();
    panel1.add(
        scrollPane2,
        new GridConstraints(
            1,
            1,
            4,
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
    scrollPane2.setViewportView(userGroup);
    final JScrollPane scrollPane3 = new JScrollPane();
    panel1.add(
        scrollPane3,
        new GridConstraints(
            10,
            1,
            3,
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
    scrollPane3.setViewportView(permissions);
    final JPanel panel3 = new JPanel();
    panel3.setLayout(new GridLayoutManager(2, 2, new Insets(0, 0, 0, 0), -1, -1));
    tabbedPane.addTab("Einkaufsverlauf", panel3);
    final JScrollPane scrollPane4 = new JScrollPane();
    panel3.add(
        scrollPane4,
        new GridConstraints(
            0,
            0,
            1,
            2,
            GridConstraints.ANCHOR_CENTER,
            GridConstraints.FILL_BOTH,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW,
            null,
            null,
            null,
            0,
            false));
    scrollPane4.setViewportView(shoppingHistory);
    final JPanel panel4 = new JPanel();
    panel4.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
    panel3.add(
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
    printBon = new JButton();
    printBon.setText("Bon ausdrucken");
    panel4.add(
        printBon,
        new GridConstraints(
            0,
            1,
            1,
            1,
            GridConstraints.ANCHOR_EAST,
            GridConstraints.FILL_NONE,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
            GridConstraints.SIZEPOLICY_FIXED,
            null,
            null,
            null,
            0,
            false));
    final Spacer spacer2 = new Spacer();
    panel4.add(
        spacer2,
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
    final JPanel panel5 = new JPanel();
    panel5.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
    tabbedPane.addTab("Guthabenverlauf", panel5);
    final JScrollPane scrollPane5 = new JScrollPane();
    panel5.add(
        scrollPane5,
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
    scrollPane5.setViewportView(valueHistory);
    final JPanel panel6 = new JPanel();
    panel6.setLayout(new GridBagLayout());
    panel5.add(
        panel6,
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
    printStatement = new JButton();
    printStatement.setActionCommand("Kontoauszug drucken");
    printStatement.setText("Kontoauszug drucken");
    GridBagConstraints gbc;
    gbc = new GridBagConstraints();
    gbc.gridx = 3;
    gbc.gridy = 0;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.insets = new Insets(3, 0, 3, 0);
    panel6.add(printStatement, gbc);
    optCurrent = new JRadioButton();
    optCurrent.setText("aktueller");
    gbc = new GridBagConstraints();
    gbc.gridx = 1;
    gbc.gridy = 0;
    panel6.add(optCurrent, gbc);
    optLast = new JRadioButton();
    optLast.setText("letzer");
    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.anchor = GridBagConstraints.EAST;
    panel6.add(optLast, gbc);
    statementType = new AdvancedComboBox();
    gbc = new GridBagConstraints();
    gbc.gridx = 2;
    gbc.gridy = 0;
    gbc.weightx = 1.0;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    panel6.add(statementType, gbc);
    ButtonGroup buttonGroup;
    buttonGroup = new ButtonGroup();
    buttonGroup.add(optCurrent);
    buttonGroup.add(optLast);
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
