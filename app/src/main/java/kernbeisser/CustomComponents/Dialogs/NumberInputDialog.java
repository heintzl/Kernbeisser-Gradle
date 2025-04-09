package kernbeisser.CustomComponents.Dialogs;

import java.util.function.Function;
import javax.swing.*;

public class NumberInputDialog {

  private static <T extends Number> T getInput(
      JComponent parentComponent,
      String message,
      String title,
      Function<String, T> numberParser,
      boolean retry) {

    String candidate =
        JOptionPane.showInputDialog(
            parentComponent,
            (retry
                    ? "Eingabe kann nicht verarbeitet werden, bitte noch einmal versuchen.\n%s"
                    : "%s")
                .formatted(message),
            title,
            JOptionPane.QUESTION_MESSAGE);
    if (candidate.isBlank()) {
      return null;
    }
    candidate = candidate.trim();
    try {
      return numberParser.apply(candidate);
    } catch (NumberFormatException e) {
      return getInput(parentComponent, message, title, numberParser, true);
    }
  }

  public static int getInt(JComponent parentComponent, String message, String title) {
    return getInput(parentComponent, message, title, Integer::parseInt, false);
  }
}
