package kernbeisser.StartUp.LogIn;

import java.awt.*;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import kernbeisser.Config.Config;
import kernbeisser.Config.Config.DBAccess;
import kernbeisser.Windows.MVC.IView;
import kernbeisser.Windows.MVC.Linked;
import org.jetbrains.annotations.NotNull;

public class DBLogInView implements IView<DBLogInController>, DocumentListener {
  private JButton logIn;
  private JTextField url;
  private JTextField username;
  private JPasswordField password;
  private JButton cancel;
  private JPanel main;

  @Linked private DBLogInController controller;

  @Override
  public void initialize(DBLogInController controller) {
    DBAccess access = Config.getConfig().getDbAccess();
    url.setText(access.getUrl());
    username.setText(access.getUsername());
    logIn.addActionListener(e -> controller.logIn());
    username.addActionListener(e -> controller.logIn());
    username.getDocument().addDocumentListener(this);
    password.addActionListener(e -> controller.logIn());
    password.getDocument().addDocumentListener(this);
    password.setText(access.getPassword());
    cancel.addActionListener(e -> back());
  }

  @Override
  public @NotNull JComponent getContent() {
    return main;
  }

  @Override
  public String getTitle() {
    return "Datenbankverbindung";
  }

  void connectionValid() {
    JOptionPane.showMessageDialog(getTopComponent(), "Die Verbindung wurde erfolgreich erstellt!");
  }

  void connectionRefused() {
    JOptionPane.showMessageDialog(
        getTopComponent(),
        "Es kann leider keine Verbindung hergestellt werden,\n bitte \u00fcberpr\u00fcfen sie die Eingaben nach Fehlern");
  }

  public DBAccess getDBAccess() {
    return new DBAccess(url.getText(), username.getText(), new String(password.getPassword()));
  }

  public void setConnectionValid(boolean serviceAvailable) {
    username.setForeground(serviceAvailable ? Color.GREEN : Color.RED);
    password.setForeground(serviceAvailable ? Color.GREEN : Color.RED);
    url.setForeground(serviceAvailable ? Color.GREEN : Color.RED);
  }

  private void defaultColor() {
    username.setForeground(Color.BLACK);
    password.setForeground(Color.BLACK);
    url.setForeground(Color.BLACK);
  }

  @Override
  public void insertUpdate(DocumentEvent e) {
    controller.connectionChanged();
    defaultColor();
  }

  @Override
  public void removeUpdate(DocumentEvent e) {
    controller.connectionChanged();
    defaultColor();
  }

  @Override
  public void changedUpdate(DocumentEvent e) {
    controller.connectionChanged();
    defaultColor();
  }
}
