package kernbeisser.Windows.ManageUser;

import kernbeisser.CustomComponents.ObjectTable.Column;
import kernbeisser.CustomComponents.ObjectTable.ObjectTable;
import kernbeisser.DBEntitys.User;

import javax.swing.*;

public class ManageUserUIView extends JPanel {
    private JPanel mainPanel;
    private ObjectTable<User> userTable;
    private JButton addUser;
    private JButton editUser;
    private JButton deleteUser;
    private JButton close;

    private void createUIComponents() {
        userTable = new ObjectTable<>(
                Column.create("Username", User::getUsername),
                Column.create("Vorname", User::getFirstName),
                Column.create("Nachname", User::getSurname)
        );
    }
}
