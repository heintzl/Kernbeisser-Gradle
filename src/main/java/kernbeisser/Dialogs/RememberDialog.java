package kernbeisser.Dialogs;

import java.awt.*;
import javax.swing.*;
import kernbeisser.DBEntities.User;
import kernbeisser.Useful.Tools;

public class RememberDialog {
  private RememberDialog() {};

  public static void showDialog(User user, Component parent, Object message) {
    showDialog(user, parent, message, "Information");
  }

  public static void showDialog(User user, Component parent, Object message, String title) {
    showDialog(user, parent, message, title, JOptionPane.INFORMATION_MESSAGE);
  }

  public static void showDialog(
      User user, Component parent, Object message, String title, int messageType) {
    StackTraceElement element;
    int i = 1;
    do {
      element = Tools.getCallerStackTraceElement(i++);
    } while (element.getClassName().equals(RememberDialog.class.getName()));
    showDialog(
        user,
        element.getFileName() + ":" + element.getMethodName() + ":" + element.getLineNumber(),
        parent,
        message,
        title,
        messageType);
  }

  public static void showDialog(
      User user, String dialogName, Component parent, Object message, String title) {
    showDialog(user, dialogName, parent, message, title, JOptionPane.INFORMATION_MESSAGE);
  }

  public static void showDialog(
      User user,
      String dialogName,
      Component parent,
      Object message,
      String title,
      int messageType) {
    if (user.isIgnoredDialog(dialogName)) return;
    JCheckBox checkbox = new JCheckBox("Nicht erneut anzeigen.");
    Object[] params = {message, checkbox};
    JOptionPane.showMessageDialog(parent, params, title, messageType);
    if (checkbox.isSelected()) {
      user.ignoreDialog(dialogName);
    }
  }
}
