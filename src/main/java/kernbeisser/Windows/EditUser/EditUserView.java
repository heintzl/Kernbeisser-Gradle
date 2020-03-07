package kernbeisser.Windows.EditUser;

import kernbeisser.DBEntities.Permission;
import kernbeisser.DBEntities.User;
import kernbeisser.Enums.Key;
import kernbeisser.Windows.View;
import kernbeisser.Windows.Window;

import javax.swing.*;
import java.util.Collection;

class EditUserView extends Window implements View {
    private JLabel lblVorname;
    private JLabel lblNachname;
    private kernbeisser.CustomComponents.TextFields.PermissionField firstName;
    private kernbeisser.CustomComponents.TextFields.PermissionField lastName;
    private JLabel lblStrasse;
    private kernbeisser.CustomComponents.TextFields.PermissionField street;
    private JLabel lblPlz;
    private JLabel lblOrt;
    private JLabel grpUser;
    private JLabel grpAddress;
    private JLabel lblTelefon1;
    private JLabel lblTelefon2;
    private JLabel grpLogin;
    private JLabel lblUsername;
    private JLabel lblPasswort;
    private kernbeisser.CustomComponents.TextFields.PermissionField postalCode;
    private kernbeisser.CustomComponents.TextFields.PermissionField town;
    private kernbeisser.CustomComponents.TextFields.PermissionField phone1;
    private kernbeisser.CustomComponents.TextFields.PermissionField phone2;
    private kernbeisser.CustomComponents.TextFields.PermissionField username;
    private kernbeisser.CustomComponents.PermissionButton chgPassword;
    private JLabel lblRolle;
    private kernbeisser.CustomComponents.PermissionComboBox roles;
    private JLabel lblHasKey;
    private JLabel lblIsEmployee;
    private kernbeisser.CustomComponents.PermissionCheckBox hasKey;
    private kernbeisser.CustomComponents.PermissionCheckBox isEmployee;
    private JLabel lblZusatzdienste;
    private JLabel lblAnteile;
    private JLabel grpGenossenschaft;
    private JSpinner shares;
    private JSpinner solidarySupplement;
    private kernbeisser.CustomComponents.PermissionButton chgJobs;
    private JLabel lblDienste;
    private JPanel userDataPanel;
    private kernbeisser.CustomComponents.TextFields.PermissionField extraJobs;
    private JButton cancel;
    private JButton submit;
    private JPanel buttonPanel;

    public EditUserView(EditUserController controller, Window current) {
        super(current, Key.ACTION_EDIT_USER);
        add(userDataPanel);
        setSize(500, 580);
        setLocationRelativeTo(current);
        chgPassword.addActionListener(e -> {
            controller.requestChangePassword();

        });
        chgJobs.addActionListener(e -> controller.openJobSelector());
        submit.addActionListener(e -> {
            controller.doAction();
        });
        cancel.addActionListener(e -> back());

        postalCode.setRequiredKeys(Key.USER_TOWN_CODE_READ, Key.USER_TOWN_CODE_WRITE);
        town.setRequiredKeys(Key.USER_TOWN_READ, Key.USER_TOWN_WRITE);
        phone1.setRequiredKeys(Key.USER_PHONE_NUMBER1_READ, Key.USER_PHONE_NUMBER1_WRITE);
        phone2.setRequiredKeys(Key.USER_PHONE_NUMBER2_READ, Key.USER_PHONE_NUMBER2_WRITE);
        username.setRequiredKeys(Key.USER_USERNAME_READ, Key.USER_USERNAME_WRITE);
        street.setRequiredKeys(Key.USER_STREET_READ, Key.USER_STREET_WRITE);
    }


    void setData(User data) {
        firstName.setText(data.getFirstName());
        lastName.setText(data.getSurname());
        street.setText(data.getStreet());
        town.setText(data.getTown());
        phone1.setText(data.getPhoneNumber1());
        phone2.setText(data.getPhoneNumber2());
        username.setText(data.getUsername());
        hasKey.setSelected(data.isKernbeisserKey());
        isEmployee.setSelected(data.isEmployee());
        extraJobs.setText(data.getExtraJobs());
    }

    User getData(User data) {
        data.setFirstName(firstName.getText());
        data.setSurname(lastName.getText());
        data.setStreet(street.getText());
        data.setTown(town.getText());
        data.setPhoneNumber1(phone1.getText());
        data.setPhoneNumber2(phone2.getText());
        data.setUsername(username.getText());
        data.setKernbeisserKey(hasKey.isSelected());
        data.setEmployee(isEmployee.isSelected());
        data.setExtraJobs(extraJobs.getText());
        return data;
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
        JOptionPane.showMessageDialog(this, "Der Benutzername ist bereits vergeben");
    }

    void setPermissions(Collection<Permission> permission) {
        this.roles.removeAllItems();
        for (Permission p : permission) {
            roles.addItem(p);
        }
    }

}
