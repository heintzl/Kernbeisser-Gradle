package kernbeisser.Forms.FormImplemetations.User;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.text.MessageFormat;
import java.util.Set;
import javax.swing.*;
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
    shares.setEnabled(!controller.isTrialMember());
    objectForm.registerUniqueCheck(username, controller::isUsernameUnique);
    objectForm.registerObjectValidators(controller::validateUser, controller::validateFullname);
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

  public void invalidInput() {
    JOptionPane.showMessageDialog(getTopComponent(), "Die eingegeben Werte sind nicht korrekt!");
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
}
