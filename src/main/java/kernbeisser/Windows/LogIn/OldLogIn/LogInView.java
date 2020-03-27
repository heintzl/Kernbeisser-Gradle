/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kernbeisser.Windows.LogIn.OldLogIn;

import jiconfont.icons.font_awesome.FontAwesome;
import jiconfont.swing.IconFontSwing;
import kernbeisser.CustomComponents.ObjectTable.Column;
import kernbeisser.CustomComponents.ObjectTable.ObjectTable;
import kernbeisser.DBEntities.User;
import kernbeisser.Useful.Tools;
import kernbeisser.Windows.View;
import kernbeisser.Windows.Window;

import javax.swing.*;
import java.awt.*;
import java.util.Collection;


public class LogInView extends Window implements View {


    private JPanel main;
    private JButton logIn;
    private JPasswordField password;
    private JTextField username;
    private JTabbedPane users;


    /**
     * Creates new form LogIn from LogInView.form
     * with all Tables from A-Z
     * and a Table with all Users
     */
    public LogInView(Window current, LogInController controller) {
        super(current);
        add(main);
        logIn.addActionListener(e -> controller.logIn());
        password.addActionListener(e -> controller.logIn());
        setSize(Tools.getScreenWidth() / 2, 600);
        setLocationRelativeTo(null);
        logIn.setIcon(IconFontSwing.buildIcon(FontAwesome.SIGN_IN, 15, new Color(0x3C39FF)));
        windowInitialized();
    }

    void addTab(String title, Collection<User> users) {
        ObjectTable<User> userTable = new ObjectTable<>(
                users,
                Column.create("Username", User::getUsername),
                Column.create("Vorname", User::getFirstName),
                Column.create("Nachname", User::getSurname));
        userTable.getTableHeader().setFont(new Font("arial", Font.BOLD, 12));
        userTable.getSelectionModel().addListSelectionListener(e -> {
            username.setText(userTable.getSelectedObject().getUsername());
            password.requestFocus();
        });
        this.users.addTab(title, new JScrollPane(userTable));
    }

    String getUsername() {
        return username.getText();
    }

    char[] getPassword() {
        return password.getPassword();
    }

    public void accessDenied() {
        JOptionPane.showMessageDialog(this,"Zugriff verweigert.\nBitte überprüfen sie ihre Anmeldedaten,\n sollte das Problem weiter bestehen,\n melden sie sich bitte bei einem Admin\n");
    }
}
