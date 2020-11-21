package kernbeisser.Windows.EditUser;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Set;
import javax.swing.*;
import kernbeisser.CustomComponents.AccessChecking.AccessCheckBox;
import kernbeisser.CustomComponents.AccessChecking.AccessCheckingCollectionEditor;
import kernbeisser.CustomComponents.AccessChecking.AccessCheckingField;
import kernbeisser.CustomComponents.AccessChecking.ObjectForm;
import kernbeisser.CustomComponents.ObjectTable.Column;
import kernbeisser.CustomComponents.Verifier.EmailVerifier;
import kernbeisser.CustomComponents.Verifier.IntegerVerifier;
import kernbeisser.CustomComponents.Verifier.NotNullVerifier;
import kernbeisser.CustomComponents.Verifier.RegexVerifier;
import kernbeisser.DBEntities.Job;
import kernbeisser.DBEntities.Permission;
import kernbeisser.DBEntities.User;
import kernbeisser.Enums.PermissionKey;
import kernbeisser.Windows.MVC.IView;
import kernbeisser.Windows.MVC.Linked;
import org.jetbrains.annotations.NotNull;

public class EditUserView implements IView<EditUserController> {
  private JLabel lblVorname;
  private JLabel lblNachname;
  private kernbeisser.CustomComponents.AccessChecking.AccessCheckingField<User, String> firstName;
  private kernbeisser.CustomComponents.AccessChecking.AccessCheckingField<User, String> lastName;
  private JLabel lblStrasse;
  private kernbeisser.CustomComponents.AccessChecking.AccessCheckingField<User, String> street;
  private JLabel lblPlz;
  private JLabel lblOrt;
  private JLabel grpUser;
  private JLabel grpAddress;
  private JLabel lblTelefon1;
  private JLabel lblTelefon2;
  private JLabel grpLogin;
  private JLabel lblUsername;
  private kernbeisser.CustomComponents.AccessChecking.AccessCheckingField<User, Long> postalCode;
  private kernbeisser.CustomComponents.AccessChecking.AccessCheckingField<User, String> town;
  private kernbeisser.CustomComponents.AccessChecking.AccessCheckingField<User, String> phone1;
  private kernbeisser.CustomComponents.AccessChecking.AccessCheckingField<User, String> phone2;
  private kernbeisser.CustomComponents.AccessChecking.AccessCheckingField<User, String> username;
  private JLabel lblRolle;
  private JLabel lblHasKey;
  private JLabel lblIsEmployee;
  private kernbeisser.CustomComponents.PermissionCheckBox hasKey;
  private kernbeisser.CustomComponents.AccessChecking.AccessCheckBox<User> isEmployee;
  private JLabel lblZusatzdienste;
  private JLabel lblAnteile;
  private JLabel grpGenossenschaft;
  private kernbeisser.CustomComponents.AccessChecking.AccessCheckingField<User, Integer> shares;
  private AccessCheckingCollectionEditor<User, Set<Job>, Job> chgJobs;
  private JLabel lblDienste;
  private JPanel userDataPanel;
  private kernbeisser.CustomComponents.AccessChecking.AccessCheckingField<User, String> extraJobs;
  private JButton cancel;
  private JButton submit;
  private JPanel buttonPanel;
  private AccessCheckingCollectionEditor<User, Set<Permission>, Permission> editPermission;
  private kernbeisser.CustomComponents.AccessChecking.AccessCheckingField<User, Integer> keyNumber;
  private kernbeisser.CustomComponents.AccessChecking.AccessCheckingField<User, String> email;

  @Linked private EditUserController controller;

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

  void usernameAlreadyExists() {
    JOptionPane.showMessageDialog(getTopComponent(), "Der Benutzername ist bereits vergeben");
  }

  @Override
  public void initialize(EditUserController controller) {
    objectForm =
        new ObjectForm<>(
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
            chgJobs,
            editPermission);
    submit.addActionListener(e -> controller.doAction());
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
    cancel.addActionListener(e -> back());
    email.setInputVerifier(new EmailVerifier());
    phone1.setInputVerifier(new RegexVerifier(".+"));
    street.setInputVerifier(new NotNullVerifier());
    firstName.setInputVerifier(new NotNullVerifier());
    lastName.setInputVerifier(new NotNullVerifier());
    hasKey.setReadWrite(PermissionKey.USER_KERNBEISSER_KEY_READ);
    hasKey.setRequiredWriteKeys(PermissionKey.USER_KERNBEISSER_KEY_WRITE);
    shares.setInputVerifier(IntegerVerifier.from(1, 1, 3, 10));
    submit.setVerifyInputWhenFocusTarget(true);
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
    buttonPanel =
        new JPanel() {
          @Override
          protected void paintComponent(Graphics g) {}
        };
    firstName =
        new AccessCheckingField<>(
            User::getFirstName, User::setFirstName, AccessCheckingField.NOT_NULL);
    lastName =
        new AccessCheckingField<>(User::getSurname, User::setSurname, AccessCheckingField.NOT_NULL);
    street =
        new AccessCheckingField<>(User::getStreet, User::setStreet, AccessCheckingField.NOT_NULL);
    postalCode =
        new AccessCheckingField<>(
            User::getTownCode, User::setTownCode, AccessCheckingField.LONG_FORMER);
    town = new AccessCheckingField<>(User::getTown, User::setTown, AccessCheckingField.NOT_NULL);
    phone1 =
        new AccessCheckingField<>(
            User::getPhoneNumber1, User::setPhoneNumber1, AccessCheckingField.NOT_NULL);
    phone2 =
        new AccessCheckingField<>(
            User::getPhoneNumber2, User::setPhoneNumber2, AccessCheckingField.NONE);
    username =
        new AccessCheckingField<>(
            User::getUsername, User::setUsername, controller::validateUsername);
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
    this.username.inputChanged();
  }

  @Override
  public String getTitle() {
    switch (controller.getModel().getMode()) {
      case REMOVE:
        return "Benutzer löschen";
      case EDIT:
        return "Benutzer bearbeiten";
      case ADD:
        return "Benutzer hinzufügen";
      default:
        return "Benutzer-Formular";
    }
  }
}
