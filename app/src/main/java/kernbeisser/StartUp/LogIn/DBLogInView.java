package kernbeisser.StartUp.LogIn;

import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import jiconfont.icons.font_awesome.FontAwesome;
import jiconfont.swing.IconFontSwing;
import kernbeisser.Config.Config;
import kernbeisser.Config.Config.DBAccess;
import kernbeisser.CustomComponents.ObjectTable.Columns.Columns;
import kernbeisser.CustomComponents.ObjectTable.ObjectTable;
import kernbeisser.Windows.MVC.IView;
import kernbeisser.Windows.MVC.Linked;
import org.apache.commons.collections.KeyValue;
import org.jetbrains.annotations.NotNull;

public class DBLogInView implements IView<DBLogInController> {

  private JButton logIn;
  private JTextField url;
  private JTextField username;
  private JPasswordField password;
  private JButton cancel;
  private JPanel main;
  private JTextField encoding;
  private JButton cSVImport;
  private JTextField filePath;
  private JButton fileChooser;
  private JScrollPane logPane;
  private ObjectTable<KeyValue> logTable;

  @Linked private DBLogInController controller;

  private void createUIComponents() {
    logTable =
        new ObjectTable<>(
            Columns.<KeyValue>create("Level", e -> e.getKey().toString()).withPreferredWidth(80),
            Columns.create("Meldungen", KeyValue::getValue));
  }

  @Override
  public void initialize(DBLogInController controller) {
    DBAccess access = Config.getConfig().getDBAccessData();
    url.setText(access.getUrl());
    username.setText(access.getUsername());
    logIn.addActionListener(e -> controller.logIn());
    username.addActionListener(e -> controller.logIn());
    password.addActionListener(e -> controller.logIn());
    password.setText(access.getPassword());
    cancel.addActionListener(e -> cancel());
    fileChooser.addActionListener(e -> openFileExplorer());
    fileChooser.setIcon(IconFontSwing.buildIcon(FontAwesome.FOLDER, 20, new Color(255, 192, 3)));
    cSVImport.addActionListener(e -> controller.readFile(filePath.getText()));
    logTable.addComponentListener(
        new ComponentAdapter() {
          public void componentResized(ComponentEvent e) {
            logTable.scrollRectToVisible(logTable.getCellRect(logTable.getRowCount() - 1, 0, true));
          }
        });
  }

  private void cancel() {
    if (controller.isStartUp()) {
      System.exit(0);
    } else {
      back();
    }
  }

  @Override
  public @NotNull JComponent getContent() {
    return main;
  }

  @Override
  public String getTitle() {
    return "Datenbankverbindung";
  }

  void connectionRefused() {
    JOptionPane.showMessageDialog(
        getTopComponent(),
        "Es kann leider keine Verbindung hergestellt werden,\n bitte \u00fcberpr\u00fcfen sie die Eingaben nach Fehlern");
  }

  public DBAccess getDBAccess() {
    return new DBAccess(
        url.getText(), username.getText(), new String(password.getPassword()), encoding.getText());
  }

  void openFileExplorer() {
    Path importPath = Config.getConfig().getDefaultBnnInboxDir().toPath();
    String chooserRoot = Files.exists(importPath) ? importPath.toString() : ".";
    JFileChooser jFileChooser = new JFileChooser("");
    jFileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
    jFileChooser.setFileFilter(
        new FileNameExtensionFilter("CSV-Datei", "ASC", "asc", "CSV", "csv", "TXT", "txt"));
    jFileChooser.addActionListener(
        e -> {
          if (jFileChooser.getSelectedFile() == null) {
            return;
          }
          String choosenFile = jFileChooser.getSelectedFile().getAbsolutePath();
          filePath.setText(choosenFile);
        });
    jFileChooser.showOpenDialog(getContent());
  }

  public void clearLogMessages() {
    logTable.clear();
    // logTable.repaint();
  }

  public void showLogMessages(java.util.List<KeyValue> messages) {

    logPane.setVisible(true);
    logTable.addAll(messages);
    // logTable.repaint();
  }

  public void messagePathNotFound(String path) {
    JOptionPane.showMessageDialog(
        getContent(),
        "Die Datei \"" + path + "\" konnte nicht gefunden werden.",
        "Datei nicht gefunden",
        JOptionPane.ERROR_MESSAGE);
  }

  public boolean confirmCSVImport() {
    return JOptionPane.showConfirmDialog(
            getContent(),
            "Mit diesem Import-Werkzeug können große Datenmengen innerhalb von Sekunden \n"
                + "geschrottet werden. Du solltest wirklich genau wissen, was du tust und \n"
                + "sicherstellen, dass direkt vor dem Import ein Datenbank-Backup erstellt wurde.\n"
                + "Schau dir genau das Importprotokoll an und prüfe anschließend das Importergebnis\n"
                + "in den entsprechend Objektansichten, bevor wieder irgendwelche Daten erfasst \n"
                + "oder geändert werden, denn diese gehen bei einem Rollback auf das Backup verloren!\n\n"
                + "Willst du wirklich fortfahren?",
            "Ich weiß genau, was ich tue!",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE)
        == JOptionPane.YES_OPTION;
  }
}
