package kernbeisser.StartUp.LogIn;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import java.awt.*;
import java.util.Locale;
import javax.swing.*;
import javax.swing.plaf.FontUIResource;
import javax.swing.text.StyleContext;
import kernbeisser.Config.Config;
import kernbeisser.Config.Config.DBAccess;
import kernbeisser.Windows.MVC.IView;
import kernbeisser.Windows.MVC.Linked;
import org.jetbrains.annotations.NotNull;

public class DBLogInView implements IView<DBLogInController> {

  private JButton logIn;
  private JTextField url;
  private JTextField username;
  private JPasswordField password;
  private JButton cancel;
  private JPanel main;
  private JTextField encoding;

  @Linked
  private DBLogInController controller;

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

  void connectionValid() {
    JOptionPane.showMessageDialog(getTopComponent(), "Die Verbindung wurde erfolgreich erstellt!");
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

  public void setConnectionValid(boolean serviceAvailable) {
    username.setForeground(serviceAvailable ? Color.GREEN : Color.RED);
    password.setForeground(serviceAvailable ? Color.GREEN : Color.RED);
    url.setForeground(serviceAvailable ? Color.GREEN : Color.RED);
    encoding.setForeground(serviceAvailable ? Color.GREEN : Color.RED);
  }

  private void defaultColor() {
    username.setForeground(Color.BLACK);
    password.setForeground(Color.BLACK);
    url.setForeground(Color.BLACK);
    encoding.setForeground(Color.BLACK);
  }

}
