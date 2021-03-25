package kernbeisser.Windows.AdminTools;

import java.util.Collection;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import kernbeisser.CustomComponents.ObjectTable.Column;
import kernbeisser.CustomComponents.ObjectTable.ObjectTable;
import kernbeisser.DBEntities.User;
import kernbeisser.Windows.MVC.IView;
import org.jetbrains.annotations.NotNull;

public class AdminToolView implements IView<AdminToolController> {

  private JPanel main;
  private ObjectTable<User> users;
  private JButton resetPassword;
  private JButton editUserGroup;

  @Override
  public void initialize(AdminToolController controller) {
    resetPassword.addActionListener(controller::restedPassword);
    editUserGroup.addActionListener(controller::openUserGroupEditor);
  }

  User getSelectedUser() {
    return users.getSelectedObject();
  }

  public void setUsers(Collection<User> users) {
    this.users.setObjects(users);
  }

  @Override
  public @NotNull JComponent getContent() {
    return main;
  }

  private void createUIComponents() {
    users =
        new ObjectTable<>(
            Column.create("Benutzername", User::getUsername),
            Column.create("Vorname", User::getFirstName),
            Column.create("Nachname", User::getSurname));
  }

  public void showPasswordToken(String resetPassword) {
    Object message =
        new Object[] {
          "Das generierte Passwort ist Folgendes:\n",
          new JTextField(resetPassword) {
            {
              setEditable(false);
            }
          },
          "Bitte logge dich möglichst zeitnah ein,\num das Passwort zu ändern."
        };
    JOptionPane.showMessageDialog(
        getTopComponent(), message, "Generiertes Password", JOptionPane.INFORMATION_MESSAGE);
  }

  public boolean verifyPasswordChange(String username) {
    return JOptionPane.showConfirmDialog(
            getTopComponent(),
            "Soll das Passwort von "
                + username
                + " wirklich zurückgesetzt werden?\nDieser Vorgang kann nicht rückgängig gemacht werden.")
        == 0;
  }
}
