package kernbeisser.CustomComponents.Dialogs;

import java.awt.Component;
import java.util.Collection;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import kernbeisser.DBEntities.User;
import kernbeisser.Windows.LogIn.LogInModel;

public class LogInDialog {
  public static boolean showLogInRequest(Component parent, Collection<User> users) {
    JComboBox<User> userJComboBox = new JComboBox<>();
    users.forEach(userJComboBox::addItem);
    JPasswordField passwordField = new JPasswordField();
    JLabel label = new JLabel("Bitte für einen der folgenden Nutzer anmelden.");
    JLabel passwordLabel = new JLabel("Passwort");
    JOptionPane.showMessageDialog(
        parent,
        new Object[] {label, userJComboBox, passwordLabel, passwordField},
        "LogIn benötigt",
        JOptionPane.INFORMATION_MESSAGE);
    return LogInModel.isValidLogIn(
        ((User) userJComboBox.getSelectedItem()).getUsername(), passwordField.getPassword());
  }
}
