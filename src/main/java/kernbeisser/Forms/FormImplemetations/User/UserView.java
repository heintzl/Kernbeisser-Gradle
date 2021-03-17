package kernbeisser.Forms.FormImplemetations.User;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Set;
import javax.swing.*;
import kernbeisser.CustomComponents.ObjectTable.Column;
import kernbeisser.CustomComponents.Verifier.IntegerVerifier;
import kernbeisser.CustomComponents.Verifier.NotNullVerifier;
import kernbeisser.DBEntities.Job;
import kernbeisser.DBEntities.Permission;
import kernbeisser.DBEntities.User;
import kernbeisser.Enums.PermissionKey;
import kernbeisser.Forms.ObjectForm.Components.AccessCheckBox;
import kernbeisser.Forms.ObjectForm.Components.AccessCheckingCollectionEditor;
import kernbeisser.Forms.ObjectForm.Components.AccessCheckingField;
import kernbeisser.Forms.ObjectForm.ObjectForm;
import kernbeisser.Useful.Tools;
import kernbeisser.Windows.MVC.IView;
import kernbeisser.Windows.MVC.Linked;
import org.jetbrains.annotations.NotNull;

public class UserView implements IView<UserController> {
  private JLabel lblVorname;
  private JLabel lblNachname;
  private kernbeisser.Forms.ObjectForm.Components.AccessCheckingField<User, String> firstName;
  private kernbeisser.Forms.ObjectForm.Components.AccessCheckingField<User, String> lastName;
  private JLabel lblStrasse;
  private kernbeisser.Forms.ObjectForm.Components.AccessCheckingField<User, String> street;
  private JLabel lblPlz;
  private JLabel lblOrt;
  private JLabel grpUser;
  private JLabel grpAddress;
  private JLabel lblTelefon1;
  private JLabel lblTelefon2;
  private JLabel grpLogin;
  private JLabel lblUsername;
  private kernbeisser.Forms.ObjectForm.Components.AccessCheckingField<User, Long> postalCode;
  private kernbeisser.Forms.ObjectForm.Components.AccessCheckingField<User, String> town;
  private kernbeisser.Forms.ObjectForm.Components.AccessCheckingField<User, String> phone1;
  private kernbeisser.Forms.ObjectForm.Components.AccessCheckingField<User, String> phone2;
  private kernbeisser.Forms.ObjectForm.Components.AccessCheckingField<User, String> username;
  private JLabel lblRolle;
  private JLabel lblHasKey;
  private JLabel lblIsEmployee;
  private kernbeisser.CustomComponents.PermissionCheckBox hasKey;
  private kernbeisser.Forms.ObjectForm.Components.AccessCheckBox<User> isEmployee;
  private JLabel lblZusatzdienste;
  private JLabel lblAnteile;
  private JLabel grpGenossenschaft;
  private kernbeisser.Forms.ObjectForm.Components.AccessCheckingField<User, Integer> shares;
  private AccessCheckingCollectionEditor<User, Set<Job>, Job> chgJobs;
  private JLabel lblDienste;
  private JPanel userDataPanel;
  private kernbeisser.Forms.ObjectForm.Components.AccessCheckingField<User, String> extraJobs;
  private AccessCheckingCollectionEditor<User, Set<Permission>, Permission> editPermission;
  private kernbeisser.Forms.ObjectForm.Components.AccessCheckingField<User, Integer> keyNumber;
  private kernbeisser.Forms.ObjectForm.Components.AccessCheckingField<User, String> email;

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
            isEmployee,
            shares,
            extraJobs,
            keyNumber,
            email,
            chgJobs);
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
    hasKey.setReadWrite(PermissionKey.USER_KERNBEISSER_KEY_READ);
    hasKey.setRequiredWriteKeys(PermissionKey.USER_KERNBEISSER_KEY_WRITE);
    shares.setInputVerifier(IntegerVerifier.from(1, 1, 3, 10));
    objectForm.registerUniqueCheck(username, controller::isUsernameUnique);
    objectForm.registerObjectValidators(controller::validateUser);
  }

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
        new AccessCheckingField<>(
            User::getTownCode, User::setTownCode, AccessCheckingField.LONG_FORMER);
    town = new AccessCheckingField<>(User::getTown, User::setTown, AccessCheckingField.NONE);
    phone1 =
        new AccessCheckingField<>(
            User::getPhoneNumber1, User::setPhoneNumber1, AccessCheckingField.NONE);
    phone2 =
        new AccessCheckingField<>(
            User::getPhoneNumber2, User::setPhoneNumber2, AccessCheckingField.NONE);
    username =
        new AccessCheckingField<>(User::getUsername, User::setUsername, AccessCheckingField.NONE);
    isEmployee = new AccessCheckBox<>(User::isEmployee, User::setEmployee);
    shares =
        new AccessCheckingField<>(User::getShares, User::setShares, AccessCheckingField.INT_FORMER);
    extraJobs =
        new AccessCheckingField<>(User::getExtraJobs, User::setExtraJobs, AccessCheckingField.NONE);
    keyNumber =
        new AccessCheckingField<>(
            User::getKernbeisserKey, User::setKernbeisserKey, AccessCheckingField.INT_FORMER);
    email =
        new AccessCheckingField<>(User::getEmail, User::setEmail, AccessCheckingField.EMAIL_FORMER);
    editPermission =
        new AccessCheckingCollectionEditor<>(
            User::getPermissions,
            User::setPermissions,
            Permission.getAll(null),
            Column.create("Name", Permission::getName));
    chgJobs =
        new AccessCheckingCollectionEditor<>(
            User::getJobs,
            User::setJobs,
            Job.getAll(null),
            Column.create("Name", Job::getName),
            Column.create("Beschreibung", Job::getDescription));
  }

  public void invalidInput() {
    JOptionPane.showMessageDialog(getTopComponent(), "Der eingegeben Werte sind nicht korrekt!");
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

  public void showPasswordToken(String resetPassword) {
    Object message =
        new Object[] {
          "Das genererierte Passwort ist folgendes:\n",
          new JTextField(resetPassword) {
            {
              setEditable(false);
            }
          },
          "Bitte Loggen sie sich möglichst Zeitnah ein,\num das Passwort zu ändern."
        };
    JOptionPane.showMessageDialog(
        getTopComponent(), message, "Generiertes Password", JOptionPane.INFORMATION_MESSAGE);
  }

  public boolean askForAddPermissionFullMember(int no) {
    Tools.beep();
    return JOptionPane.showConfirmDialog(
            getTopComponent(),
            no
                + " Anteile sind eingetragen - \n"
                + "Soll der Mitglied-Status zu \"Mitglied\" geändert werden?")
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
}
