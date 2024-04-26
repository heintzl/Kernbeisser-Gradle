package kernbeisser.Forms.FormImplemetations.User;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.Set;
import javax.swing.*;
import javax.swing.plaf.FontUIResource;
import javax.swing.text.StyleContext;
import kernbeisser.CustomComponents.ObjectTable.Columns.Columns;
import kernbeisser.CustomComponents.PermissionCheckBox;
import kernbeisser.CustomComponents.Verifier.IntegerVerifier;
import kernbeisser.CustomComponents.Verifier.NotNullVerifier;
import kernbeisser.DBEntities.Job;
import kernbeisser.DBEntities.Permission;
import kernbeisser.DBEntities.User;
import kernbeisser.Enums.PermissionKey;
import kernbeisser.Forms.ObjectForm.Components.*;
import kernbeisser.Forms.ObjectForm.ObjectForm;
import kernbeisser.Reports.LoginInfo;
import kernbeisser.Security.Key;
import kernbeisser.Useful.Date;
import kernbeisser.Useful.Tools;
import kernbeisser.Windows.MVC.IView;
import kernbeisser.Windows.MVC.Linked;
import lombok.var;
import org.jetbrains.annotations.NotNull;

public class UserView implements IView<UserController> {

  private JLabel lblVorname;
  private JLabel lblNachname;
  private AccessCheckingField<User, String> firstName;
  private AccessCheckingField<User, String> lastName;
  private JLabel lblStrasse;
  private AccessCheckingField<User, String> street;
  private JLabel lblPlz;
  private JLabel lblOrt;
  private JLabel grpUser;
  private JLabel grpAddress;
  private JLabel lblTelefon1;
  private JLabel lblTelefon2;
  private JLabel grpLogin;
  private JLabel lblUsername;
  private AccessCheckingField<User, String> postalCode;
  private AccessCheckingField<User, String> town;
  private AccessCheckingField<User, String> phone1;
  private AccessCheckingField<User, String> phone2;
  private AccessCheckingField<User, String> username;
  private JLabel lblRolle;
  private JLabel lblHasKey;
  private JLabel lblIsEmployee;
  private PermissionCheckBox hasKey;
  private AccessCheckBox<User> isEmployee;
  private JLabel lblAnteile;
  private JLabel grpGenossenschaft;
  private AccessCheckingField<User, Integer> shares;
  private AccessCheckingCollectionEditor<User, Set<Job>, Job> chgJobs;
  private JLabel lblDienste;
  private JPanel userDataPanel;
  private AccessCheckingCollectionEditor<User, Set<Permission>, Permission> editPermission;
  private AccessCheckingField<User, Integer> keyNumber;
  private AccessCheckingField<User, String> email;
  private AccessCheckingLabel<User> userGroup;
  private JPanel main;
  private AccessCheckingLabel<User> jobs;
  private AccessCheckingLabel<User> updateInfo;
  private AccessCheckingLabel<User> creationInfo;

  @Linked private UserController controller;

  private ObjectForm<User> objectForm;

  ObjectForm<User> getObjectForm() {
    return objectForm;
  }

  void passwordToShort() {
    JOptionPane.showMessageDialog(
        null, "Das Passwort ist leider zu kurz. Es muss mindestens 4 Zeichen lang sein.");
  }

  String requestPassword() {
    return JOptionPane.showInputDialog("Bitte gib das neue Passwort ein:");
  }

  void passwordChanged() {
    JOptionPane.showMessageDialog(null, "Password ge\u00e4ndert!");
  }

  void setEditPermissionEnabled(boolean enabled) {
    editPermission.setEnabled(enabled);
  }

  void missingContact() {
    Tools.beep();
    JOptionPane.showMessageDialog(
        getTopComponent(),
        "Mindestens eine Kontaktmöglichkeit (Email oder Telefon) muss angegeben sein!",
        "Angaben unvollständig",
        JOptionPane.WARNING_MESSAGE);
  }

  void usernameAlreadyExists() {
    Tools.beep();
    JOptionPane.showMessageDialog(getTopComponent(), "Der Benutzername ist bereits vergeben");
  }

  @Override
  public void initialize(UserController controller) {
    objectForm =
        new ObjectForm<>(
            editPermission,
            firstName,
            lastName,
            street,
            postalCode,
            town,
            phone1,
            phone2,
            username,
            userGroup,
            isEmployee,
            shares,
            keyNumber,
            email,
            chgJobs,
            jobs,
            creationInfo,
            updateInfo);
    objectForm.setObjectDistinction("Der Benutzer");
    KeyAdapter refreshUsername =
        new KeyAdapter() {
          @Override
          public void keyReleased(KeyEvent e) {
            controller.refreshUsername();
          }
        };
    firstName.addKeyListener(refreshUsername);
    lastName.addKeyListener(refreshUsername);
    hasKey.addActionListener(e -> keyNumber.setEnabled(keyNumber.isEnabled()));
    firstName.setInputVerifier(new NotNullVerifier());
    lastName.setInputVerifier(new NotNullVerifier());
    hasKey.setReadable(Tools.canInvoke(this::checkUserKernbeisserKeyReadPermission));
    hasKey.setWriteable(Tools.canInvoke(this::checkUserKernbeisserKeyWritePermission));
    shares.setInputVerifier(IntegerVerifier.from(0, 1, 3, 10));
    shares.setEnabled(!controller.isTrialMemberMode());
    objectForm.registerUniqueCheck(username, controller::isUsernameUnique);
    objectForm.registerObjectValidators(
        controller::validateFullname, controller::validatePermissions, controller::validateUser);
  }

  @Key(PermissionKey.USER_KERNBEISSER_KEY_READ)
  private void checkUserKernbeisserKeyReadPermission() {}

  @Key(PermissionKey.USER_KERNBEISSER_KEY_WRITE)
  private void checkUserKernbeisserKeyWritePermission() {}

  @Override
  public @NotNull JComponent getContent() {
    return userDataPanel;
  }

  @Override
  public @NotNull Dimension getSize() {
    return new Dimension(500, 830);
  }

  private void createUIComponents() {
    // remove background
    userDataPanel =
        new JPanel() {
          @Override
          protected void paintComponent(Graphics g) {}
        };
    firstName =
        new AccessCheckingField<>(
            User::getFirstName, User::setFirstName, AccessCheckingField.NOT_NULL);
    lastName =
        new AccessCheckingField<>(User::getSurname, User::setSurname, AccessCheckingField.NOT_NULL);
    street = new AccessCheckingField<>(User::getStreet, User::setStreet, AccessCheckingField.NONE);
    postalCode =
        new AccessCheckingField<>(User::getTownCode, User::setTownCode, AccessCheckingField.NONE);
    town = new AccessCheckingField<>(User::getTown, User::setTown, AccessCheckingField.NONE);
    phone1 =
        new AccessCheckingField<>(
            User::getPhoneNumber1, User::setPhoneNumber1, AccessCheckingField.NOT_NULL);
    phone2 =
        new AccessCheckingField<>(
            User::getPhoneNumber2, User::setPhoneNumber2, AccessCheckingField.NONE);
    username =
        new AccessCheckingField<>(User::getUsername, User::setUsername, AccessCheckingField.NONE);
    userGroup =
        new AccessCheckingLabel<>(
            u -> u.getUserGroup() == null ? "" : u.getUserGroup().getMemberString());
    isEmployee = new AccessCheckBox<>(User::isEmployee, User::setEmployee);
    shares =
        new AccessCheckingField<>(User::getShares, User::setShares, AccessCheckingField.INT_FORMER);
    keyNumber =
        new AccessCheckingField<>(
            User::getKernbeisserKey, User::setKernbeisserKey, AccessCheckingField.INT_FORMER);
    email =
        new AccessCheckingField<>(User::getEmail, User::setEmail, AccessCheckingField.EMAIL_FORMER);
    Source<Permission> permissionSource =
        () ->
            Permission.getAll(
                "where not name in ('@APPLICATION', '@IMPORT', '@IN_RELATION_TO_OWN_USER')");
    editPermission =
        new AccessCheckingCollectionEditor<>(
            User::getPermissionsAsAvailable,
            permissionSource,
            Columns.create("Name", Permission::getNeatName));
    chgJobs =
        new AccessCheckingCollectionEditor<>(
                User::getJobsAsAvailable,
                Source.of(Job.class),
                Columns.create("Name", Job::getName),
                Columns.create("Beschreibung", Job::getDescription))
            .withCloseEvent(() -> jobs.setText(Job.concatenateJobs(chgJobs.getData())));
    jobs = new AccessCheckingLabel<>(User::getJobsAsString);
    updateInfo = new AccessCheckingLabel<>(this::getUpdateInfo);
    creationInfo = new AccessCheckingLabel<>(this::getCreationInfo);
  }

  private String getUpdateInfo(User u) {
    try {
      return Date.INSTANT_DATE.format(u.getUpdateDate())
          + " durch "
          + u.getUpdateBy().getFullName();
    } catch (NullPointerException e) {
      return "(nicht gespeichert)";
    }
  }

  private String getCreationInfo(User u) {
    try {
      return Date.INSTANT_DATE.format(u.getCreateDate());
    } catch (NullPointerException e) {
      return "(nicht gespeichert)";
    }
  }

  public void invalidInput() {
    JOptionPane.showMessageDialog(getTopComponent(), "Die eingegeben Werte sind nicht korrekt!");
  }

  public void invalidPermissions() {
    Color savedForeground = editPermission.getForeground();
    editPermission.setForeground(Color.RED);
    editPermission.addActionListener(e -> editPermission.setForeground(savedForeground));
  }

  public void setUsername(String username) {
    this.username.setText(username);
  }

  public String getFirstName() {
    return firstName.getText();
  }

  public String getSurname() {
    return lastName.getText();
  }

  public void showPasswordToken(String resetPassword, User user) {
    showPasswordToken(resetPassword, user, getTopComponent());
  }

  public static void showPasswordToken(String resetPassword, User user, Component parentComponent) {
    var printButton = new JButton("Benutzerinfo drucken");
    printButton.addActionListener(
        e ->
            new LoginInfo(user, resetPassword)
                .sendToPrinter("Benutzerinfo wird erstellt", Tools::showUnexpectedErrorWarning));
    Object message =
        new Object[] {
          "Der Anmeldename ist:\n",
          new JTextField(user.getUsername()) {
            {
              setEditable(false);
            }
          },
          "Das generierte Passwort ist Folgendes:\n",
          new JTextField(resetPassword) {
            {
              setEditable(false);
            }
          },
          "Bitte logge dich möglichst zeitnah ein,\num das Passwort zu ändern.\n",
          printButton
        };
    JOptionPane.showMessageDialog(
        parentComponent,
        message,
        "Generiertes Password für " + user.getFullName(),
        JOptionPane.INFORMATION_MESSAGE);
  }

  public boolean askForAddPermissionFullMember(int no, boolean trialMember) {
    Tools.beep();
    return JOptionPane.showConfirmDialog(
            getTopComponent(),
            no
                + " Anteile sind eingetragen - \n"
                + "Soll der Mitglied-Status zu \"Voll-Mitglied\" geändert"
                + (trialMember ? " und die Probemitgliedschaft aufgehoben " : "")
                + "werden?")
        == 0;
  }

  public boolean askForRemovePermissionFullMember() {
    Tools.beep();
    return JOptionPane.showConfirmDialog(
            getTopComponent(),
            "0 Anteile sind eingetragen - \n"
                + "Soll der Mitglied-Status zu \"kein Mitglied\" geändert werden?")
        == 0;
  }

  @Override
  public String getTitle() {
    return "Benutzerdaten bearbeiten";
  }

  public void wrongFullname(String fullName) {
    firstName.setInvalidInput();
    lastName.setInvalidInput();
    JOptionPane.showMessageDialog(
        getTopComponent(),
        "Der Name \""
            + fullName
            + "\" ist bereits belegt. Bitte den Benutzer so benennen, "
            + "dass Vor- und Nachname eindeutig sind!",
        "Name nicht eindeutig",
        JOptionPane.WARNING_MESSAGE);
  }

  public void invalidMembership() {
    invalidPermissions();
    JOptionPane.showMessageDialog(
        getTopComponent(),
        "Niemand kann gleichzeitig Probemitglied und Vollmitglied sein.\n"
            + "Bitte eine der Mitglied-Rollen entfernen!",
        "Fehlerhafte Rollenzuweisung",
        JOptionPane.WARNING_MESSAGE);
  }

  public void messageUserBalanceExists(double userValue) {
    JOptionPane.showMessageDialog(
        getTopComponent(),
        "Das Benutzer-Konto ist nicht ausgeglichen. Der "
            + (userValue < 0 ? "Fehlbetrag" : "Restbetrag")
            + " von "
            + MessageFormat.format("{0,number,0.00}€", userValue)
            + " muss "
            + (userValue < 0 ? "ein" : "aus")
            + "gezahlt werden, bevor das Konto gelöscht werden kann!",
        "Löschen fehlgeschlagen",
        JOptionPane.ERROR_MESSAGE);
  }

  public void messageUserIsInGroup() {
    JOptionPane.showMessageDialog(
        getTopComponent(),
        "Der Benutzer befindet sich in einer Gruppe mit anderen Benutzern. Er kann erst gelöscht werden, wenn er die Gruppe verlassen hat!",
        "Löschen fehlgeschlagen",
        JOptionPane.ERROR_MESSAGE);
  }

  public void messageDeleteSuccess(boolean success) {
    String message =
        success
            ? "Das Benutzerkonto wurde erfolgreich entfernt!"
            : "Das Benutzerkonto kann nicht gelöscht werden, weil dir die Berechtigung dafür fehlt!";
    JOptionPane.showMessageDialog(
        getTopComponent(), message, "Konto Löschen", JOptionPane.INFORMATION_MESSAGE);
  }

  public boolean confirmDelete() {
    return JOptionPane.showConfirmDialog(
            null,
            "Soll das Benutzerkonto wirklich gelöscht werden?",
            "Löschbestätigung",
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.QUESTION_MESSAGE)
        == JOptionPane.OK_OPTION;
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
    main.setMinimumSize(new Dimension(254, 1200));
    main.setPreferredSize(new Dimension(404, 1200));
    final JLabel label1 = new JLabel();
    label1.setText("Benutzerdaten");
    main.add(
        label1,
        new GridConstraints(
            0,
            0,
            1,
            1,
            GridConstraints.ANCHOR_CENTER,
            GridConstraints.FILL_NONE,
            GridConstraints.SIZEPOLICY_FIXED,
            GridConstraints.SIZEPOLICY_FIXED,
            null,
            null,
            null,
            0,
            false));
    userDataPanel.setLayout(new GridBagLayout());
    userDataPanel.setAutoscrolls(false);
    userDataPanel.setBackground(new Color(-1));
    userDataPanel.setDoubleBuffered(true);
    userDataPanel.setMinimumSize(new Dimension(250, 250));
    userDataPanel.setOpaque(false);
    userDataPanel.setPreferredSize(new Dimension(400, 565));
    main.add(
        userDataPanel,
        new GridConstraints(
            1,
            0,
            1,
            1,
            GridConstraints.ANCHOR_CENTER,
            GridConstraints.FILL_NONE,
            GridConstraints.SIZEPOLICY_WANT_GROW,
            GridConstraints.SIZEPOLICY_WANT_GROW,
            null,
            null,
            null,
            1,
            false));
    lblVorname = new JLabel();
    Font lblVornameFont = this.$$$getFont$$$(null, Font.PLAIN, -1, lblVorname.getFont());
    if (lblVornameFont != null) lblVorname.setFont(lblVornameFont);
    lblVorname.setText("Vorname:*");
    GridBagConstraints gbc;
    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 1;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.insets = new Insets(0, 25, 0, 10);
    userDataPanel.add(lblVorname, gbc);
    lblNachname = new JLabel();
    Font lblNachnameFont = this.$$$getFont$$$(null, Font.PLAIN, -1, lblNachname.getFont());
    if (lblNachnameFont != null) lblNachname.setFont(lblNachnameFont);
    lblNachname.setText("Nachname:*");
    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 2;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.insets = new Insets(0, 25, 0, 10);
    userDataPanel.add(lblNachname, gbc);
    lblStrasse = new JLabel();
    Font lblStrasseFont = this.$$$getFont$$$(null, Font.PLAIN, -1, lblStrasse.getFont());
    if (lblStrasseFont != null) lblStrasse.setFont(lblStrasseFont);
    lblStrasse.setText("Strasse:");
    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 4;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.insets = new Insets(0, 25, 0, 10);
    userDataPanel.add(lblStrasse, gbc);
    lblPlz = new JLabel();
    Font lblPlzFont = this.$$$getFont$$$(null, Font.PLAIN, -1, lblPlz.getFont());
    if (lblPlzFont != null) lblPlz.setFont(lblPlzFont);
    lblPlz.setText("PLZ:");
    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 5;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.insets = new Insets(0, 25, 0, 10);
    userDataPanel.add(lblPlz, gbc);
    lblOrt = new JLabel();
    Font lblOrtFont = this.$$$getFont$$$(null, Font.PLAIN, -1, lblOrt.getFont());
    if (lblOrtFont != null) lblOrt.setFont(lblOrtFont);
    lblOrt.setText("Ort:");
    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 6;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.insets = new Insets(0, 25, 0, 10);
    userDataPanel.add(lblOrt, gbc);
    firstName.setOpaque(false);
    gbc = new GridBagConstraints();
    gbc.gridx = 1;
    gbc.gridy = 1;
    gbc.gridwidth = 4;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.insets = new Insets(2, 0, 2, 15);
    userDataPanel.add(firstName, gbc);
    gbc = new GridBagConstraints();
    gbc.gridx = 1;
    gbc.gridy = 2;
    gbc.gridwidth = 4;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.insets = new Insets(2, 0, 2, 15);
    userDataPanel.add(lastName, gbc);
    gbc = new GridBagConstraints();
    gbc.gridx = 1;
    gbc.gridy = 4;
    gbc.gridwidth = 4;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.insets = new Insets(2, 0, 2, 15);
    userDataPanel.add(street, gbc);
    final JLabel label2 = new JLabel();
    Font label2Font = this.$$$getFont$$$(null, -1, -1, label2.getFont());
    if (label2Font != null) label2.setFont(label2Font);
    label2.setForeground(new Color(-16752083));
    label2.setText("Kontakt");
    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 7;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.insets = new Insets(3, 15, 0, 10);
    userDataPanel.add(label2, gbc);
    grpUser = new JLabel();
    Font grpUserFont = this.$$$getFont$$$(null, -1, -1, grpUser.getFont());
    if (grpUserFont != null) grpUser.setFont(grpUserFont);
    grpUser.setForeground(new Color(-16752083));
    grpUser.setText("Name");
    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.insets = new Insets(3, 15, 0, 0);
    userDataPanel.add(grpUser, gbc);
    grpAddress = new JLabel();
    Font grpAddressFont = this.$$$getFont$$$(null, -1, -1, grpAddress.getFont());
    if (grpAddressFont != null) grpAddress.setFont(grpAddressFont);
    grpAddress.setForeground(new Color(-16752083));
    grpAddress.setText("Adresse");
    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 3;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.insets = new Insets(3, 15, 0, 10);
    userDataPanel.add(grpAddress, gbc);
    lblTelefon1 = new JLabel();
    Font lblTelefon1Font = this.$$$getFont$$$(null, Font.PLAIN, -1, lblTelefon1.getFont());
    if (lblTelefon1Font != null) lblTelefon1.setFont(lblTelefon1Font);
    lblTelefon1.setText("Telefonnummer 1:*");
    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 9;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.insets = new Insets(0, 25, 0, 10);
    userDataPanel.add(lblTelefon1, gbc);
    lblTelefon2 = new JLabel();
    Font lblTelefon2Font = this.$$$getFont$$$(null, Font.PLAIN, -1, lblTelefon2.getFont());
    if (lblTelefon2Font != null) lblTelefon2.setFont(lblTelefon2Font);
    lblTelefon2.setText("Telefonnummer 2:");
    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 10;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.insets = new Insets(0, 25, 0, 10);
    userDataPanel.add(lblTelefon2, gbc);
    grpLogin = new JLabel();
    Font grpLoginFont = this.$$$getFont$$$(null, -1, -1, grpLogin.getFont());
    if (grpLoginFont != null) grpLogin.setFont(grpLoginFont);
    grpLogin.setForeground(new Color(-16752083));
    grpLogin.setText("Anmeldung");
    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 11;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.insets = new Insets(3, 15, 0, 10);
    userDataPanel.add(grpLogin, gbc);
    lblUsername = new JLabel();
    Font lblUsernameFont = this.$$$getFont$$$(null, Font.PLAIN, -1, lblUsername.getFont());
    if (lblUsernameFont != null) lblUsername.setFont(lblUsernameFont);
    lblUsername.setText("Nutzername:*");
    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 12;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.insets = new Insets(0, 25, 0, 10);
    userDataPanel.add(lblUsername, gbc);
    gbc = new GridBagConstraints();
    gbc.gridx = 1;
    gbc.gridy = 5;
    gbc.gridwidth = 4;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.insets = new Insets(2, 0, 2, 15);
    userDataPanel.add(postalCode, gbc);
    gbc = new GridBagConstraints();
    gbc.gridx = 1;
    gbc.gridy = 6;
    gbc.gridwidth = 4;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.insets = new Insets(2, 0, 2, 15);
    userDataPanel.add(town, gbc);
    gbc = new GridBagConstraints();
    gbc.gridx = 1;
    gbc.gridy = 9;
    gbc.gridwidth = 4;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.insets = new Insets(2, 0, 2, 15);
    userDataPanel.add(phone1, gbc);
    gbc = new GridBagConstraints();
    gbc.gridx = 1;
    gbc.gridy = 10;
    gbc.gridwidth = 4;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.insets = new Insets(2, 0, 2, 15);
    userDataPanel.add(phone2, gbc);
    gbc = new GridBagConstraints();
    gbc.gridx = 1;
    gbc.gridy = 12;
    gbc.gridwidth = 4;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.insets = new Insets(2, 0, 2, 15);
    userDataPanel.add(username, gbc);
    lblRolle = new JLabel();
    Font lblRolleFont = this.$$$getFont$$$(null, Font.PLAIN, -1, lblRolle.getFont());
    if (lblRolleFont != null) lblRolle.setFont(lblRolleFont);
    lblRolle.setText("Rolle:");
    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 13;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.insets = new Insets(0, 25, 0, 10);
    userDataPanel.add(lblRolle, gbc);
    lblHasKey = new JLabel();
    Font lblHasKeyFont = this.$$$getFont$$$(null, Font.PLAIN, -1, lblHasKey.getFont());
    if (lblHasKeyFont != null) lblHasKey.setFont(lblHasKeyFont);
    lblHasKey.setText("Schlüssel:");
    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 16;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.insets = new Insets(0, 25, 0, 10);
    userDataPanel.add(lblHasKey, gbc);
    hasKey = new PermissionCheckBox();
    hasKey.setOpaque(false);
    hasKey.setText("");
    gbc = new GridBagConstraints();
    gbc.gridx = 1;
    gbc.gridy = 16;
    gbc.gridwidth = 4;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.insets = new Insets(2, 0, 2, 15);
    userDataPanel.add(hasKey, gbc);
    isEmployee.setOpaque(false);
    gbc = new GridBagConstraints();
    gbc.gridx = 1;
    gbc.gridy = 18;
    gbc.gridwidth = 4;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.insets = new Insets(2, 0, 2, 15);
    userDataPanel.add(isEmployee, gbc);
    lblIsEmployee = new JLabel();
    Font lblIsEmployeeFont = this.$$$getFont$$$(null, Font.PLAIN, -1, lblIsEmployee.getFont());
    if (lblIsEmployeeFont != null) lblIsEmployee.setFont(lblIsEmployeeFont);
    lblIsEmployee.setText("Mitarbeiter:");
    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 18;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.insets = new Insets(0, 25, 0, 10);
    userDataPanel.add(lblIsEmployee, gbc);
    grpGenossenschaft = new JLabel();
    Font grpGenossenschaftFont = this.$$$getFont$$$(null, -1, -1, grpGenossenschaft.getFont());
    if (grpGenossenschaftFont != null) grpGenossenschaft.setFont(grpGenossenschaftFont);
    grpGenossenschaft.setForeground(new Color(-16752083));
    grpGenossenschaft.setText("Genossenschaft");
    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 14;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.insets = new Insets(3, 15, 0, 10);
    userDataPanel.add(grpGenossenschaft, gbc);
    chgJobs.setMinimumSize(new Dimension(126, 30));
    chgJobs.setOpaque(false);
    chgJobs.setPreferredSize(new Dimension(126, 30));
    chgJobs.setText("Dienste...");
    gbc = new GridBagConstraints();
    gbc.gridx = 1;
    gbc.gridy = 19;
    gbc.weightx = 0.2;
    gbc.weighty = 1.0;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.insets = new Insets(2, 0, 2, 15);
    userDataPanel.add(chgJobs, gbc);
    lblDienste = new JLabel();
    Font lblDiensteFont = this.$$$getFont$$$(null, Font.PLAIN, -1, lblDienste.getFont());
    if (lblDiensteFont != null) lblDienste.setFont(lblDiensteFont);
    lblDienste.setText("Dienste:");
    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 19;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.insets = new Insets(0, 25, 0, 10);
    userDataPanel.add(lblDienste, gbc);
    final JSeparator separator1 = new JSeparator();
    Font separator1Font = this.$$$getFont$$$(null, -1, -1, separator1.getFont());
    if (separator1Font != null) separator1.setFont(separator1Font);
    separator1.setForeground(new Color(-16752083));
    gbc = new GridBagConstraints();
    gbc.gridx = 1;
    gbc.gridy = 7;
    gbc.gridwidth = 4;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.insets = new Insets(3, 0, 0, 15);
    userDataPanel.add(separator1, gbc);
    final JSeparator separator2 = new JSeparator();
    Font separator2Font = this.$$$getFont$$$(null, -1, -1, separator2.getFont());
    if (separator2Font != null) separator2.setFont(separator2Font);
    separator2.setForeground(new Color(-16752083));
    gbc = new GridBagConstraints();
    gbc.gridx = 1;
    gbc.gridy = 11;
    gbc.gridwidth = 4;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.insets = new Insets(3, 0, 0, 15);
    userDataPanel.add(separator2, gbc);
    final JSeparator separator3 = new JSeparator();
    Font separator3Font = this.$$$getFont$$$(null, -1, -1, separator3.getFont());
    if (separator3Font != null) separator3.setFont(separator3Font);
    separator3.setForeground(new Color(-16752083));
    gbc = new GridBagConstraints();
    gbc.gridx = 1;
    gbc.gridy = 14;
    gbc.gridwidth = 4;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.insets = new Insets(3, 0, 0, 15);
    userDataPanel.add(separator3, gbc);
    final JSeparator separator4 = new JSeparator();
    Font separator4Font = this.$$$getFont$$$(null, -1, -1, separator4.getFont());
    if (separator4Font != null) separator4.setFont(separator4Font);
    separator4.setForeground(new Color(-16752083));
    gbc = new GridBagConstraints();
    gbc.gridx = 1;
    gbc.gridy = 0;
    gbc.gridwidth = 4;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.insets = new Insets(3, 0, 0, 15);
    userDataPanel.add(separator4, gbc);
    final JSeparator separator5 = new JSeparator();
    Font separator5Font = this.$$$getFont$$$(null, -1, -1, separator5.getFont());
    if (separator5Font != null) separator5.setFont(separator5Font);
    separator5.setForeground(new Color(-16752083));
    gbc = new GridBagConstraints();
    gbc.gridx = 1;
    gbc.gridy = 3;
    gbc.gridwidth = 4;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.insets = new Insets(3, 0, 0, 15);
    userDataPanel.add(separator5, gbc);
    editPermission.setText("Rollen bearbeiten");
    gbc = new GridBagConstraints();
    gbc.gridx = 1;
    gbc.gridy = 13;
    gbc.gridwidth = 4;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    gbc.anchor = GridBagConstraints.WEST;
    userDataPanel.add(editPermission, gbc);
    final JLabel label3 = new JLabel();
    label3.setText("Schlüsselnummer:");
    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 17;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.insets = new Insets(0, 25, 0, 0);
    userDataPanel.add(label3, gbc);
    gbc = new GridBagConstraints();
    gbc.gridx = 1;
    gbc.gridy = 17;
    gbc.gridwidth = 4;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.insets = new Insets(2, 0, 2, 15);
    userDataPanel.add(keyNumber, gbc);
    gbc = new GridBagConstraints();
    gbc.gridx = 1;
    gbc.gridy = 8;
    gbc.gridwidth = 4;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    gbc.anchor = GridBagConstraints.NORTH;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.insets = new Insets(2, 0, 2, 15);
    userDataPanel.add(email, gbc);
    final JLabel label4 = new JLabel();
    label4.setText("E-Mail");
    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 8;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.insets = new Insets(0, 25, 0, 0);
    userDataPanel.add(label4, gbc);
    lblAnteile = new JLabel();
    Font lblAnteileFont = this.$$$getFont$$$(null, Font.PLAIN, -1, lblAnteile.getFont());
    if (lblAnteileFont != null) lblAnteile.setFont(lblAnteileFont);
    lblAnteile.setText("Anteile:");
    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 20;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.insets = new Insets(0, 25, 0, 10);
    userDataPanel.add(lblAnteile, gbc);
    gbc = new GridBagConstraints();
    gbc.gridx = 1;
    gbc.gridy = 20;
    gbc.gridwidth = 4;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.insets = new Insets(2, 0, 2, 15);
    userDataPanel.add(shares, gbc);
    final JLabel label5 = new JLabel();
    Font label5Font = this.$$$getFont$$$(null, Font.PLAIN, -1, label5.getFont());
    if (label5Font != null) label5.setFont(label5Font);
    label5.setText("erstellt am:");
    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 21;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.insets = new Insets(0, 25, 0, 10);
    userDataPanel.add(label5, gbc);
    Font creationInfoFont = this.$$$getFont$$$(null, Font.ITALIC, -1, creationInfo.getFont());
    if (creationInfoFont != null) creationInfo.setFont(creationInfoFont);
    creationInfo.setForeground(new Color(-12828863));
    gbc = new GridBagConstraints();
    gbc.gridx = 1;
    gbc.gridy = 21;
    gbc.gridwidth = 4;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.insets = new Insets(2, 0, 5, 15);
    userDataPanel.add(creationInfo, gbc);
    final JLabel label6 = new JLabel();
    Font label6Font = this.$$$getFont$$$(null, Font.PLAIN, -1, label6.getFont());
    if (label6Font != null) label6.setFont(label6Font);
    label6.setText("Benutzergruppe:");
    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 15;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.insets = new Insets(0, 25, 0, 10);
    userDataPanel.add(label6, gbc);
    userGroup.setBackground(new Color(-4473925));
    userGroup.setEnabled(false);
    userGroup.setFocusable(false);
    userGroup.setForeground(new Color(-12828863));
    userGroup.setText("Gruppenmitglieder");
    gbc = new GridBagConstraints();
    gbc.gridx = 1;
    gbc.gridy = 15;
    gbc.gridwidth = 4;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.insets = new Insets(2, 0, 2, 15);
    userDataPanel.add(userGroup, gbc);
    jobs.setBackground(new Color(-4473925));
    jobs.setEnabled(false);
    jobs.setForeground(new Color(-12828863));
    jobs.setText("Dienste");
    gbc = new GridBagConstraints();
    gbc.gridx = 2;
    gbc.gridy = 19;
    gbc.weightx = 0.5;
    gbc.weighty = 1.0;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.insets = new Insets(2, 0, 2, 15);
    userDataPanel.add(jobs, gbc);
    final JPanel spacer1 = new JPanel();
    gbc = new GridBagConstraints();
    gbc.gridx = 3;
    gbc.gridy = 19;
    gbc.weightx = 1.0;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    userDataPanel.add(spacer1, gbc);
    final JLabel label7 = new JLabel();
    Font label7Font = this.$$$getFont$$$(null, Font.PLAIN, -1, label7.getFont());
    if (label7Font != null) label7.setFont(label7Font);
    label7.setText("letzte Änderung:");
    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 22;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.insets = new Insets(0, 25, 0, 10);
    userDataPanel.add(label7, gbc);
    Font updateInfoFont = this.$$$getFont$$$(null, Font.ITALIC, -1, updateInfo.getFont());
    if (updateInfoFont != null) updateInfo.setFont(updateInfoFont);
    updateInfo.setForeground(new Color(-12828863));
    gbc = new GridBagConstraints();
    gbc.gridx = 1;
    gbc.gridy = 22;
    gbc.gridwidth = 4;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.insets = new Insets(2, 0, 5, 15);
    userDataPanel.add(updateInfo, gbc);
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
