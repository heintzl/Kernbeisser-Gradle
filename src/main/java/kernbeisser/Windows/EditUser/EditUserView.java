package kernbeisser.Windows.EditUser;

import kernbeisser.CustomComponents.AccessChecking.*;
import kernbeisser.CustomComponents.PermissionButton;
import kernbeisser.CustomComponents.Verifier.*;
import kernbeisser.DBEntities.User;
import kernbeisser.Enums.PermissionKey;
import kernbeisser.Windows.View;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class EditUserView implements View<EditUserController> {
    private JLabel lblVorname;
    private JLabel lblNachname;
    private kernbeisser.CustomComponents.AccessChecking.AccessCheckingField<User,String> firstName;
    private kernbeisser.CustomComponents.AccessChecking.AccessCheckingField<User,String> lastName;
    private JLabel lblStrasse;
    private kernbeisser.CustomComponents.AccessChecking.AccessCheckingField<User,String> street;
    private JLabel lblPlz;
    private JLabel lblOrt;
    private JLabel grpUser;
    private JLabel grpAddress;
    private JLabel lblTelefon1;
    private JLabel lblTelefon2;
    private JLabel grpLogin;
    private JLabel lblUsername;
    private JLabel lblPasswort;
    private kernbeisser.CustomComponents.AccessChecking.AccessCheckingField<User,Long> postalCode;
    private kernbeisser.CustomComponents.AccessChecking.AccessCheckingField<User,String> town;
    private kernbeisser.CustomComponents.AccessChecking.AccessCheckingField<User,String> phone1;
    private kernbeisser.CustomComponents.AccessChecking.AccessCheckingField<User,String> phone2;
    private kernbeisser.CustomComponents.AccessChecking.AccessCheckingField<User,String> username;
    private kernbeisser.CustomComponents.PermissionButton chgPassword;
    private JLabel lblRolle;
    private JLabel lblHasKey;
    private JLabel lblIsEmployee;
    private kernbeisser.CustomComponents.PermissionCheckBox hasKey;
    private kernbeisser.CustomComponents.AccessChecking.AccessCheckBox<User> isEmployee;
    private JLabel lblZusatzdienste;
    private JLabel lblAnteile;
    private JLabel grpGenossenschaft;
    private kernbeisser.CustomComponents.AccessChecking.AccessCheckingField<User,Integer> shares;
    private kernbeisser.CustomComponents.AccessChecking.AccessCheckingField<User,Double> solidarySupplement;
    private kernbeisser.CustomComponents.PermissionButton chgJobs;
    private JLabel lblDienste;
    private JPanel userDataPanel;
    private kernbeisser.CustomComponents.AccessChecking.AccessCheckingField<User,String> extraJobs;
    private JButton cancel;
    private JButton submit;
    private JPanel buttonPanel;
    private PermissionButton editPermission;
    private kernbeisser.CustomComponents.AccessChecking.AccessCheckingField<User,Integer> keyNumber;
    private kernbeisser.CustomComponents.AccessChecking.AccessCheckingField<User,String> email;


    private ObjectForm<User> objectForm;

    ObjectForm<User> getObjectForm() {
        return objectForm;
    }

    void passwordToShort() {
        JOptionPane.showMessageDialog(null, "Das Passwort ist leider zu kurz, es muss mindestens 4 zeichen lang sein");
    }

    String requestPassword() {
        return JOptionPane.showInputDialog("Bitte geben sie das neue Passwort ein:");
    }

    void passwordChanged() {
        JOptionPane.showMessageDialog(null, "Password ge\u00e4ndert!");
    }

    void usernameAlreadyExists() {
        JOptionPane.showMessageDialog(getTopComponent(), "Der Benutzername ist bereits vergeben");
    }

    @Override
    public void initialize(EditUserController controller) {
        objectForm = new ObjectForm<>(
                controller.getModel().getUser(),
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
                solidarySupplement,
                extraJobs,
                keyNumber,
                email
        );
        chgPassword.addActionListener(e -> {
            controller.requestChangePassword();

        });
        chgJobs.addActionListener(e -> controller.openJobSelector());
        submit.addActionListener(e -> controller.doAction());
        KeyAdapter refreshUsername = new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                controller.refreshUsername();
            }
        };
        firstName.addKeyListener(refreshUsername);
        lastName.addKeyListener(refreshUsername);
        hasKey.addActionListener(e -> keyNumber.setEnabled(!keyNumber.isEnabled()));
        editPermission.addActionListener(e -> controller.openPermissionSelector());
        cancel.addActionListener(e -> back());
        email.setInputVerifier(new EmailVerifier());
        phone1.setInputVerifier(new RegexVerifier(".+"));
        street.setInputVerifier(new NotNullVerifier());
        firstName.setInputVerifier(new NotNullVerifier());
        lastName.setInputVerifier(new NotNullVerifier());
        chgPassword.setRequiredWriteKeys(PermissionKey.USER_PASSWORD_WRITE);
        editPermission.setRequiredWriteKeys(PermissionKey.USER_PERMISSION_WRITE);
        hasKey.setReadWrite(PermissionKey.USER_KERNBEISSER_KEY_READ);
        chgJobs.setRequiredWriteKeys(PermissionKey.USER_JOBS_WRITE, PermissionKey.USER_JOBS_READ);
        hasKey.setRequiredWriteKeys(PermissionKey.USER_KERNBEISSER_KEY_WRITE);
        shares.setInputVerifier(IntegerVerifier.from(1,1,3,10));
        submit.setVerifyInputWhenFocusTarget(true);
    }

    @Override
    public @NotNull JComponent getContent() {
        return userDataPanel;
    }

    @Override
    public @NotNull Dimension getSize() {
        return new Dimension(500,700);
    }

    private void createUIComponents() {
        //remove background
        userDataPanel = new JPanel(){
            @Override
            protected void paintComponent(Graphics g) { }
        };
        buttonPanel = new JPanel(){
            @Override
            protected void paintComponent(Graphics g) {}
        };
        firstName = new AccessCheckingField<>(User::getFirstName,User::setFirstName, AccessCheckingField.NOT_NULL);
        lastName = new AccessCheckingField<>(User::getSurname,User::setSurname,AccessCheckingField.NOT_NULL);
        street = new AccessCheckingField<>(User::getStreet,User::setStreet,AccessCheckingField.NOT_NULL);
        postalCode = new AccessCheckingField<>(User::getTownCode, User::setTownCode, AccessCheckingField.LONG_FORMER);
        town = new AccessCheckingField<>(User::getTown,User::setTown,AccessCheckingField.NOT_NULL);
        phone1 = new AccessCheckingField<>(User::getPhoneNumber1,User::setPhoneNumber1,AccessCheckingField.NOT_NULL);
        phone2 = new AccessCheckingField<>(User::getPhoneNumber2,User::setPhoneNumber2,AccessCheckingField.NONE);
        username = new AccessCheckingField<>(User::getUsername,User::setUsername,AccessCheckingField.NOT_NULL);
        isEmployee = new AccessCheckBox<>(User::isEmployee, User::setEmployee);
        shares = new AccessCheckingField<>(User::getShares,User::setShares,AccessCheckingField.INT_FORMER);
        solidarySupplement = new AccessCheckingField<>(User::getSolidaritySurcharge,User::setSolidaritySurcharge,AccessCheckingField.DOUBLE_FORMER);
        extraJobs = new AccessCheckingField<>(User::getExtraJobs,User::setExtraJobs,AccessCheckingField.NONE);
        keyNumber = new AccessCheckingField<>(User::getKernbeisserKey, User::setKernbeisserKey, AccessCheckingField.INT_FORMER);
        email = new AccessCheckingField<>(User::getEmail,User::setEmail,AccessCheckingField.EMAIL_FORMER);
    }

    public void invalidInput() {
        JOptionPane.showMessageDialog(getTopComponent(),"Der Eingegeben werte sind nicht korrekt!");
    }

    public void setUsername(String username) {
        this.username.setText(username);
    }
}
