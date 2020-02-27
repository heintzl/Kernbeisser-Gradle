package kernbeisser.Windows.EditUser;

import kernbeisser.DBEntities.Permission;
import kernbeisser.DBEntities.User;
import kernbeisser.Windows.View;
import kernbeisser.Windows.Window;

import javax.swing.*;
import java.util.Collection;

class EditUserView extends Window implements View {
    private JLabel lblVorname;
    private JLabel lblNachname;
    private JTextField firstName;
    private JTextField lastName;
    private JLabel lblStrasse;
    private JTextField street;
    private JLabel lblPlz;
    private JLabel lblOrt;
    private JLabel grpUser;
    private JLabel grpAddress;
    private JLabel lblTelefon1;
    private JLabel lblTelefon2;
    private JLabel grpLogin;
    private JLabel lblUsername;
    private JLabel lblPasswort;
    private JTextField postalCode;
    private JTextField town;
    private JTextField phone1;
    private JTextField phone2;
    private JTextField unserName;
    private JButton chgPassword;
    private JLabel lblRolle;
    private JComboBox<Permission> roles;
    private JLabel lblHasKey;
    private JLabel lblIsEmployee;
    private JCheckBox hasKey;
    private JCheckBox isEmployee;
    private JLabel lblZusatzdienste;
    private JLabel lblAnteile;
    private JLabel grpGenossenschaft;
    private JSpinner shares;
    private JSpinner solidarySupplement;
    private JButton chgJobs;
    private JLabel lblDienste;
    private JPanel userDataPanel;
    private JTextField extraJobs;
    private JButton cancel;
    private JButton submit;
    private JPanel buttonPanel;

    public EditUserView(EditUserController controller, Window current) {
        super(current);
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
    }


    void setData(User data) {
        firstName.setText(data.getFirstName());
        lastName.setText(data.getSurname());
        street.setText(data.getStreet());
        town.setText(data.getTown());
        phone1.setText(data.getPhoneNumber1());
        phone2.setText(data.getPhoneNumber2());
        unserName.setText(data.getUsername());
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
        data.setUsername(unserName.getText());
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
