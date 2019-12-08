package kernbeisser.Windows.ManageUser;

import kernbeisser.DBEntitys.User;
import kernbeisser.Enums.Permission;

import javax.swing.*;

public class EditUserUIView {
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
    private JComboBox roles;
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


    public void setData(User data) {
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

    public void getData(User data) {
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
    }

    public boolean isModified(User data) {
        if (firstName.getText() != null ? !firstName.getText().equals(data.getFirstName()) : data.getFirstName() != null)
            return true;
        if (lastName.getText() != null ? !lastName.getText().equals(data.getSurname()) : data.getSurname() != null)
            return true;
        if (street.getText() != null ? !street.getText().equals(data.getStreet()) : data.getStreet() != null)
            return true;
        if (town.getText() != null ? !town.getText().equals(data.getTown()) : data.getTown() != null) return true;
        if (phone1.getText() != null ? !phone1.getText().equals(data.getPhoneNumber1()) : data.getPhoneNumber1() != null)
            return true;
        if (phone2.getText() != null ? !phone2.getText().equals(data.getPhoneNumber2()) : data.getPhoneNumber2() != null)
            return true;
        if (unserName.getText() != null ? !unserName.getText().equals(data.getUsername()) : data.getUsername() != null)
            return true;
        if (hasKey.isSelected() != data.isKernbeisserKey()) return true;
        if (isEmployee.isSelected() != data.isEmployee()) return true;
        if (extraJobs.getText() != null ? !extraJobs.getText().equals(data.getExtraJobs()) : data.getExtraJobs() != null)
            return true;
        return false;
    }
}
