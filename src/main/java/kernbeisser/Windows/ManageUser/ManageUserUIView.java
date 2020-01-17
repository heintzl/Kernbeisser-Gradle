package kernbeisser.Windows.ManageUser;

import kernbeisser.CustomComponents.ObjectTable.Column;
import kernbeisser.CustomComponents.ObjectTable.ObjectTable;
import kernbeisser.DBEntitys.User;
import kernbeisser.Enums.Permission;
import kernbeisser.Enums.UserPersistFeedback;
import kernbeisser.Windows.Controller;
import kernbeisser.Windows.View;
import kernbeisser.Windows.Window;

import javax.swing.*;
import java.awt.*;
import java.util.Collection;

public class ManageUserUIView extends Window implements View {
    private JPanel mainPanel;
    private ObjectTable<User> userTable;
    private JButton addUser;
    private JButton editUser;
    private JButton deleteUser;
    private JButton close;

    private ManageUserUIController controller;

    public ManageUserUIView(Window current, Permission permission) {
        super(current);
        add(mainPanel);
        setSize(500, 500);
        setLocationRelativeTo(null);
        controller = new ManageUserUIController(this);
        deleteUser.addActionListener(e -> {
            if (JOptionPane.showConfirmDialog(this, "Wollen sie diesen Benutzer wirklich entfernen?") == 0)
                controller.remove();
        });
        editUser.addActionListener(e -> controller.edit());
        addUser.addActionListener(e -> controller.add());
        close.addActionListener(e -> back());
    }

    boolean applyFeedback(UserPersistFeedback feedback) {
        switch (feedback) {
            case NO_USER_SELECTED:
                JOptionPane.showMessageDialog(this, "Zum bearbeiten eines Nutzers muss dieser ausgewählt sein,\nbitte wählen sie den zu bearbeitenden Nutzer aus");
                return false;
            case USERNAME_ALREADY_EXISTS:
                JOptionPane.showMessageDialog(this, "Der Nutzer kann diesen Nutzernamen nicht erhalten,\nda dieser bereits vergeben ist");
                return false;
            case UN_COMPLETE_USER:
                JOptionPane.showMessageDialog(this, "Das Nutzer konnte nich aktualisert werden,\nda das Nutzervormular wurde nicht vollst\u00e4ndig ausgefüllt wurde");
                return false;
            case USERNAME_TO_SHORT:
                JOptionPane.showMessageDialog(this, "Der Nutzername des eingegeben Nutzers ist zu kurz!");
                return false;
            case SUCCESS:
                JOptionPane.showMessageDialog(this, "Der Vorgang war erfolgreich!");
                return true;
            default:
                JOptionPane.showMessageDialog(this, "Ein undefenierter Fehler ist aufgetreten,\n bitte wenden sie sich an einen Adminestrator");
                return false;
        }
    }

    void setUsers(Collection<User> users) {
        userTable.setObjects(users);
    }

    User getSelectedUser() {
        return userTable.getSelectedObject();
    }

    private void createUIComponents() {
        userTable = new ObjectTable<>(
                Column.create("Username", User::getUsername),
                Column.create("Vorname", User::getFirstName),
                Column.create("Nachname", User::getSurname)
        );
    }

    @Override
    public Controller getController() {
        return controller;
    }

}
