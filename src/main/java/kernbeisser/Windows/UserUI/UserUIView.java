package kernbeisser.Windows.UserUI;

import kernbeisser.DBEntitys.User;
import kernbeisser.Enums.UserPersistFeedback;
import kernbeisser.Windows.Controller;
import kernbeisser.Windows.JobSelector.JobSelectorView;
import kernbeisser.Windows.View;
import kernbeisser.Windows.Window;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.function.Consumer;
import java.util.function.Function;

public class UserUIView extends Window implements View {
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

    private UserUIController controller;

    public UserUIView(Window current, Function<User, UserPersistFeedback> submitAction, Consumer<UserPersistFeedback> feedbackConsumer) {
        super(current);
        this.controller = new UserUIController(this, feedbackConsumer);
        add(userDataPanel);
        setSize(500, 580);
        setLocationRelativeTo(current);
        chgPassword.addActionListener(e -> {
            String password = JOptionPane.showInputDialog("Bitte geben sie das neue Passwort ein:");
            if (password.length() < 4) {
                JOptionPane.showMessageDialog(null, "Das Passwort ist leider zu kurz, es muss mindestens 4 zeichen lang sein");
            } else {
                controller.changePassword(password);
                JOptionPane.showMessageDialog(null, "Password ge\u00e4ndert!");
            }
        });
        chgJobs.addActionListener(e -> new JobSelectorView(this));
        submit.addActionListener(e -> {
            feedbackConsumer.accept(submitAction.apply(getUser()));
            back();
        });
        cancel.addActionListener(e -> back());
    }

    public void loadUser(User user) {
        controller.loadUser(user);
    }

    public User getUser() {
        return controller.getUser();
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

    void getData(User data) {
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

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        userDataPanel = new JPanel();
        userDataPanel.setLayout(new GridBagLayout());
        userDataPanel.setMinimumSize(new Dimension(250, 250));
        userDataPanel.setPreferredSize(new Dimension(400, 545));
        userDataPanel.setBorder(BorderFactory.createTitledBorder(null, "Benutzerdaten", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, this.$$$getFont$$$(null, Font.BOLD | Font.ITALIC, -1, userDataPanel.getFont()), new Color(-16773480)));
        lblVorname = new JLabel();
        Font lblVornameFont = this.$$$getFont$$$(null, Font.PLAIN, -1, lblVorname.getFont());
        if (lblVornameFont != null) lblVorname.setFont(lblVornameFont);
        lblVorname.setText("Vorname:");
        GridBagConstraints gbc;
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 20, 0, 0);
        userDataPanel.add(lblVorname, gbc);
        final JPanel spacer1 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        userDataPanel.add(spacer1, gbc);
        lblNachname = new JLabel();
        Font lblNachnameFont = this.$$$getFont$$$(null, Font.PLAIN, -1, lblNachname.getFont());
        if (lblNachnameFont != null) lblNachname.setFont(lblNachnameFont);
        lblNachname.setText("Nachname:");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 20, 0, 0);
        userDataPanel.add(lblNachname, gbc);
        lblStrasse = new JLabel();
        Font lblStrasseFont = this.$$$getFont$$$(null, Font.PLAIN, -1, lblStrasse.getFont());
        if (lblStrasseFont != null) lblStrasse.setFont(lblStrasseFont);
        lblStrasse.setText("Strasse:");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 20, 0, 0);
        userDataPanel.add(lblStrasse, gbc);
        lblPlz = new JLabel();
        Font lblPlzFont = this.$$$getFont$$$(null, Font.PLAIN, -1, lblPlz.getFont());
        if (lblPlzFont != null) lblPlz.setFont(lblPlzFont);
        lblPlz.setText("PLZ:");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 20, 0, 0);
        userDataPanel.add(lblPlz, gbc);
        lblOrt = new JLabel();
        Font lblOrtFont = this.$$$getFont$$$(null, Font.PLAIN, -1, lblOrt.getFont());
        if (lblOrtFont != null) lblOrt.setFont(lblOrtFont);
        lblOrt.setText("Ort:");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 20, 0, 0);
        userDataPanel.add(lblOrt, gbc);
        firstName = new JTextField();
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 1;
        gbc.weightx = 0.7;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(2, 0, 2, 15);
        userDataPanel.add(firstName, gbc);
        lastName = new JTextField();
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 2;
        gbc.weightx = 0.7;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(2, 0, 2, 15);
        userDataPanel.add(lastName, gbc);
        street = new JTextField();
        street.setText("");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 4;
        gbc.weightx = 0.7;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(2, 0, 2, 15);
        userDataPanel.add(street, gbc);
        final JLabel label1 = new JLabel();
        Font label1Font = this.$$$getFont$$$(null, Font.BOLD, -1, label1.getFont());
        if (label1Font != null) label1.setFont(label1Font);
        label1.setText("Kontakt");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 15, 0, 0);
        userDataPanel.add(label1, gbc);
        grpUser = new JLabel();
        Font grpUserFont = this.$$$getFont$$$(null, Font.BOLD, -1, grpUser.getFont());
        if (grpUserFont != null) grpUser.setFont(grpUserFont);
        grpUser.setText("Benutzer");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 15, 0, 0);
        userDataPanel.add(grpUser, gbc);
        grpAddress = new JLabel();
        Font grpAddressFont = this.$$$getFont$$$(null, Font.BOLD, -1, grpAddress.getFont());
        if (grpAddressFont != null) grpAddress.setFont(grpAddressFont);
        grpAddress.setText("Adresse");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 15, 0, 0);
        userDataPanel.add(grpAddress, gbc);
        lblTelefon1 = new JLabel();
        Font lblTelefon1Font = this.$$$getFont$$$(null, Font.PLAIN, -1, lblTelefon1.getFont());
        if (lblTelefon1Font != null) lblTelefon1.setFont(lblTelefon1Font);
        lblTelefon1.setText("Telefon (fest):");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 8;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 20, 0, 0);
        userDataPanel.add(lblTelefon1, gbc);
        lblTelefon2 = new JLabel();
        Font lblTelefon2Font = this.$$$getFont$$$(null, Font.PLAIN, -1, lblTelefon2.getFont());
        if (lblTelefon2Font != null) lblTelefon2.setFont(lblTelefon2Font);
        lblTelefon2.setText("Telefon (mobil):");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 9;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 20, 0, 0);
        userDataPanel.add(lblTelefon2, gbc);
        grpLogin = new JLabel();
        Font grpLoginFont = this.$$$getFont$$$(null, Font.BOLD, -1, grpLogin.getFont());
        if (grpLoginFont != null) grpLogin.setFont(grpLoginFont);
        grpLogin.setText("Anmeldung");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 10;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 15, 0, 0);
        userDataPanel.add(grpLogin, gbc);
        lblUsername = new JLabel();
        Font lblUsernameFont = this.$$$getFont$$$(null, Font.PLAIN, -1, lblUsername.getFont());
        if (lblUsernameFont != null) lblUsername.setFont(lblUsernameFont);
        lblUsername.setText("Nutzername:");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 11;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 20, 0, 0);
        userDataPanel.add(lblUsername, gbc);
        lblPasswort = new JLabel();
        Font lblPasswortFont = this.$$$getFont$$$(null, Font.PLAIN, -1, lblPasswort.getFont());
        if (lblPasswortFont != null) lblPasswort.setFont(lblPasswortFont);
        lblPasswort.setText("Passwort:");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 12;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 20, 0, 0);
        userDataPanel.add(lblPasswort, gbc);
        postalCode = new JTextField();
        postalCode.setText("passwort");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 5;
        gbc.weightx = 0.7;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(2, 0, 2, 15);
        userDataPanel.add(postalCode, gbc);
        town = new JTextField();
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 6;
        gbc.weightx = 0.7;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(2, 0, 2, 15);
        userDataPanel.add(town, gbc);
        phone1 = new JTextField();
        phone1.setText("telefon1");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 8;
        gbc.weightx = 0.7;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(2, 0, 2, 15);
        userDataPanel.add(phone1, gbc);
        phone2 = new JTextField();
        phone2.setText("telefon2");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 9;
        gbc.weightx = 0.7;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(2, 0, 2, 15);
        userDataPanel.add(phone2, gbc);
        unserName = new JTextField();
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 11;
        gbc.weightx = 0.7;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(2, 0, 2, 15);
        userDataPanel.add(unserName, gbc);
        chgPassword = new JButton();
        chgPassword.setText("Ändern...");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 12;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(2, 0, 2, 15);
        userDataPanel.add(chgPassword, gbc);
        lblRolle = new JLabel();
        Font lblRolleFont = this.$$$getFont$$$(null, Font.PLAIN, -1, lblRolle.getFont());
        if (lblRolleFont != null) lblRolle.setFont(lblRolleFont);
        lblRolle.setText("Rolle:");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 13;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 20, 0, 0);
        userDataPanel.add(lblRolle, gbc);
        roles = new JComboBox();
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 13;
        gbc.weightx = 0.7;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(2, 0, 2, 15);
        userDataPanel.add(roles, gbc);
        lblHasKey = new JLabel();
        Font lblHasKeyFont = this.$$$getFont$$$(null, Font.PLAIN, -1, lblHasKey.getFont());
        if (lblHasKeyFont != null) lblHasKey.setFont(lblHasKeyFont);
        lblHasKey.setText("Schlüssel:");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 15;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 20, 0, 0);
        userDataPanel.add(lblHasKey, gbc);
        hasKey = new JCheckBox();
        hasKey.setSelected(false);
        hasKey.setText("");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 15;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(2, 0, 2, 15);
        userDataPanel.add(hasKey, gbc);
        isEmployee = new JCheckBox();
        isEmployee.setText("");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 16;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(2, 0, 2, 15);
        userDataPanel.add(isEmployee, gbc);
        lblIsEmployee = new JLabel();
        Font lblIsEmployeeFont = this.$$$getFont$$$(null, Font.PLAIN, -1, lblIsEmployee.getFont());
        if (lblIsEmployeeFont != null) lblIsEmployee.setFont(lblIsEmployeeFont);
        lblIsEmployee.setText("Mitarbeiter:");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 16;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 20, 0, 0);
        userDataPanel.add(lblIsEmployee, gbc);
        grpGenossenschaft = new JLabel();
        Font grpGenossenschaftFont = this.$$$getFont$$$(null, Font.BOLD, -1, grpGenossenschaft.getFont());
        if (grpGenossenschaftFont != null) grpGenossenschaft.setFont(grpGenossenschaftFont);
        grpGenossenschaft.setText("Genossenschaft");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 14;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 15, 0, 0);
        userDataPanel.add(grpGenossenschaft, gbc);
        lblZusatzdienste = new JLabel();
        Font lblZusatzdiensteFont = this.$$$getFont$$$(null, Font.PLAIN, -1, lblZusatzdienste.getFont());
        if (lblZusatzdiensteFont != null) lblZusatzdienste.setFont(lblZusatzdiensteFont);
        lblZusatzdienste.setText("Zusatzdienste:");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 18;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 20, 0, 0);
        userDataPanel.add(lblZusatzdienste, gbc);
        lblAnteile = new JLabel();
        Font lblAnteileFont = this.$$$getFont$$$(null, Font.PLAIN, -1, lblAnteile.getFont());
        if (lblAnteileFont != null) lblAnteile.setFont(lblAnteileFont);
        lblAnteile.setText("Anteile:");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 19;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 20, 0, 0);
        userDataPanel.add(lblAnteile, gbc);
        final JLabel label2 = new JLabel();
        Font label2Font = this.$$$getFont$$$(null, Font.PLAIN, -1, label2.getFont());
        if (label2Font != null) label2.setFont(label2Font);
        label2.setText("Solidarzuschlag:");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 20;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 20, 0, 0);
        userDataPanel.add(label2, gbc);
        shares = new JSpinner();
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 19;
        gbc.weightx = 0.7;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(2, 0, 2, 15);
        userDataPanel.add(shares, gbc);
        solidarySupplement = new JSpinner();
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 20;
        gbc.weightx = 0.7;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(2, 0, 2, 15);
        userDataPanel.add(solidarySupplement, gbc);
        chgJobs = new JButton();
        chgJobs.setText("Dienste...");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 17;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(2, 0, 2, 15);
        userDataPanel.add(chgJobs, gbc);
        lblDienste = new JLabel();
        Font lblDiensteFont = this.$$$getFont$$$(null, Font.PLAIN, -1, lblDienste.getFont());
        if (lblDiensteFont != null) lblDienste.setFont(lblDiensteFont);
        lblDienste.setText("Dienste:");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 17;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 20, 0, 0);
        userDataPanel.add(lblDienste, gbc);
        final JPanel spacer2 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 22;
        gbc.fill = GridBagConstraints.VERTICAL;
        userDataPanel.add(spacer2, gbc);
        extraJobs = new JTextField();
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 18;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(2, 0, 2, 15);
        userDataPanel.add(extraJobs, gbc);
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new FlowLayout(FlowLayout.RIGHT, 5, 5));
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 21;
        gbc.gridwidth = 3;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(0, 15, 0, 15);
        userDataPanel.add(panel1, gbc);
        submit = new JButton();
        submit.setText("OK");
        panel1.add(submit);
        cancel = new JButton();
        cancel.setText("Abbrechen");
        panel1.add(cancel);
    }

    /**
     * @noinspection ALL
     */
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
        return new Font(resultName, style >= 0 ? style : currentFont.getStyle(), size >= 0 ? size : currentFont.getSize());
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return userDataPanel;
    }

    @Override
    public Controller getController() {
        return controller;
    }
}
