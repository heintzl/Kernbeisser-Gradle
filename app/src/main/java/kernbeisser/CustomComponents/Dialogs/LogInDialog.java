package kernbeisser.CustomComponents.Dialogs;

import java.awt.Component;
import java.util.Collection;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import kernbeisser.CustomComponents.ComboBox.AdvancedComboBox;
import kernbeisser.DBEntities.User;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class LogInDialog {
  public static boolean showLogInRequest(
      @Nullable Component parent, @NotNull Collection<User> users) {
    AdvancedComboBox<User> userJComboBox = new AdvancedComboBox<>();
    users.forEach(userJComboBox::addItem);
    JPasswordField passwordField = new JPasswordField();
    JLabel label = new JLabel("Bitte für einen der folgenden Nutzer anmelden.");
    JLabel passwordLabel = new JLabel("Passwort");
    JOptionPane.showMessageDialog(
        parent,
        new Object[] {label, userJComboBox, passwordLabel, passwordField},
        "LogIn benötigt",
        JOptionPane.INFORMATION_MESSAGE);
    return userJComboBox
        .getSelected()
        .map(user -> user.verifyPassword(passwordField.getPassword()))
        .orElse(false);
  }
}
