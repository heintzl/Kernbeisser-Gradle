package kernbeisser.Windows.ManageUser;

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

    public void setData(Permission data) {
    }

    public void getData(Permission data) {
    }

    public boolean isModified(Permission data) {
        return false;
    }
}
