package kernbeisser.CustomComponents.Dialogs;

import java.awt.*;
import java.util.function.Predicate;
import javax.swing.*;

public class TextAreaDialog {

  private static String getInput(
      JComponent parentComponent,
      String initialValue,
      String title,
      Predicate<String> stringValidator,
      boolean retry) {

    JPanel messagePanel = new JPanel();
    JTextArea textArea = new JTextArea(initialValue);
    textArea.setPreferredSize(new Dimension(500, 80));
    if (retry) {
      messagePanel.add(
          new JLabel("Eingabe kann nicht verarbeitet werden, bitte noch einmal versuchen."));
    }
    messagePanel.add(textArea);
    int response =
        JOptionPane.showConfirmDialog(
            parentComponent,
            messagePanel,
            title,
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.PLAIN_MESSAGE);
    if (response == JOptionPane.CANCEL_OPTION) {
      return initialValue;
    }
    String candidate = textArea.getText();
    if (candidate.isBlank()) {
      return "";
    }
    candidate = candidate.trim();
    if (stringValidator.test(candidate)) {
      return candidate;
    } else {
      return getInput(parentComponent, initialValue, title, stringValidator, true);
    }
  }

  public static String getText(JComponent parentComponent, String initialValue, String title) {
    return getInput(parentComponent, initialValue, title, s -> true, false);
  }

  public static String getValidatedText(
      JComponent parentComponent,
      String initialValue,
      String title,
      Predicate<String> stringValidator) {
    return getInput(parentComponent, initialValue, title, stringValidator, false);
  }
}
