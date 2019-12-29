/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kernbeisser.Windows.LogIn;

import kernbeisser.CustomComponents.ObjectTable.Column;
import kernbeisser.CustomComponents.DBTable.DBTable;
import kernbeisser.CustomComponents.ObjectTable.ObjectTable;
import kernbeisser.DBEntitys.User;
import kernbeisser.Useful.Images;
import kernbeisser.Useful.Tools;
import kernbeisser.Windows.Background;
import kernbeisser.Windows.Controller;
import kernbeisser.Windows.UserMenu.UserMenuView;
import kernbeisser.Windows.View;
import kernbeisser.Windows.Window;

import javax.swing.*;
import java.awt.*;
import java.util.Collection;


public class LogInView extends Window implements View {


    private LogInController controller;
    private JPanel panel1;
    private JButton logIn;
    private JPasswordField password;
    private JTextField username;
    private JTabbedPane users;

    /**
     * Creates new form LogIn from LogInView.form
     * with all Tables from A-Z
     * and a Table with all Users
     */
    public LogInView(kernbeisser.Windows.Window current) {
        super(current);
        controller=new LogInController(this);
        logIn.addActionListener(e -> logIn());
        password.addActionListener(e -> logIn());
        setSize(Tools.getScreenWidth()/2, Tools.getScreenHeight()/2);
        Background b = new Background(Images.getImage("basil.png"));
        b.autoSize(this);
        b.setBounds(0,0,500,300);
        add(b);
        setLocationRelativeTo(null);
    }

    void addTab(String title, Collection<User> users){
        ObjectTable<User> userTable = new ObjectTable<>(
                users,
                Column.create("Username", User::getUsername),
                Column.create("Vorname", User::getFirstName),
                Column.create("Nachname", User::getSurname));
        userTable.getTableHeader().setFont(new Font("arial",Font.BOLD,12));
        userTable.getSelectionModel().addListSelectionListener(e -> username.setText(userTable.getSelectedObject().getUsername()));
        this.users.addTab(title, new JScrollPane(userTable));
    }

    String getUsername(){
        return username.getText();
    }

    char[] getPassword(){
        return password.getPassword();
    }

    private void logIn(){
        switch (controller.logIn()) {
            case LogInController.SUCCESS:
                controller.openUserMenu();
                break;
            case LogInController.INCORRECT_USERNAME:
                Tools.ping(username);
                JOptionPane.showMessageDialog(this,"Benutzername Falsch!");
                return;
            case LogInController.INCORRECT_PASSWORD:
                Tools.ping(password);
                JOptionPane.showMessageDialog(this,"Das von ihnen Angegebene Passwort ist nicht Korrekt");
                return;

        }
        back();
    }

    @Override
    public Controller getController() {
        return controller;
    }


}
