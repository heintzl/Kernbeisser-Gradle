package kernbeisser.Exeptions.handler;

import java.awt.*;
import java.util.Arrays;
import java.util.stream.Collectors;
import javax.swing.*;
import jiconfont.icons.font_awesome.FontAwesome;
import jiconfont.swing.IconFontSwing;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class UnexpectedExceptionHandler {
  public static RuntimeException showUnexpectedErrorWarning(Throwable error) {
    log.error(error.getMessage(), error);
    showErrorWarning(
        error,
        "Ein unerwarteter Fehler ist aufgetreten.\n"
            + "Bitte melde den Fehler beim Entwicklerteam\n"
            + "oder auf Github:\n"
            + "https://github.com/julikiller98/Kernbeisser-Gradle/\n"
            + "Fehler:");
    throw new RuntimeException(error);
  }

  public static void showErrorWarning(Throwable error, String explainingMessage)
      throws RuntimeException {
    JTextArea textArea = new JTextArea();
    textArea.setText(explainingMessage + "\n" + error.toString());
    textArea.setEditable(false);
    textArea.setLineWrap(true);
    textArea.setWrapStyleWord(true);
    JScrollPane errorMessagePane = new JScrollPane(textArea);
    JPanel messagePanel = new JPanel(new BorderLayout());
    Color buttonColor = new Color(133, 0, 16);
    JButton stackTraceButton = new JButton("Fehlerstapel");
    stackTraceButton.setIcon(IconFontSwing.buildIcon(FontAwesome.LIST, 20, buttonColor));
    stackTraceButton.addActionListener(e -> showStackTrace(error));
    messagePanel.add(errorMessagePane, BorderLayout.CENTER);
    messagePanel.add(stackTraceButton, BorderLayout.SOUTH);
    messagePanel.setPreferredSize(new Dimension(480, 380));
    JOptionPane.showMessageDialog(null, messagePanel, "Fehlermeldung", JOptionPane.ERROR_MESSAGE);
  }

  private static void showStackTrace(Throwable error) {
    JTextArea textArea = new JTextArea(error.getMessage() + "\n");
    textArea.append(
        Arrays.stream(error.getStackTrace())
            .map(StackTraceElement::toString)
            .collect(Collectors.joining("\n")));
    textArea.setEditable(false);
    textArea.setLineWrap(true);
    textArea.setWrapStyleWord(true);
    JScrollPane message = new JScrollPane(textArea);
    message.setPreferredSize(new Dimension(480, 340));
    JOptionPane.showMessageDialog(null, message, "Fehlerstapel", JOptionPane.ERROR_MESSAGE);
  }

  public static void showPrintAbortedWarning(Exception e, boolean logEvent) {
    if (logEvent) {
      log.error(e.getMessage(), e);
    }
    JOptionPane.showMessageDialog(
        null, "Der Ausdruck wurde abgebrochen!", "Drucken", JOptionPane.WARNING_MESSAGE);
  }
}
