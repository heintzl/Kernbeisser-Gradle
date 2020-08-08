package kernbeisser.StartUp.LogIn;

import javax.swing.*;
import kernbeisser.Config.ConfigManager;
import kernbeisser.DBConnection.DBConnection;
import kernbeisser.Windows.MVC.View;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

public class DBLogInView implements View<DBLogInController> {
  private JButton logIn;
  private JTextField url;
  private JTextField username;
  private JPasswordField password;
  private JButton cancel;
  private JPanel main;

  private final DBLogInController controller;

  public DBLogInView(DBLogInController controller) {
    this.controller = controller;
  }

  @Override
  public void initialize(DBLogInController controller) {
    JSONObject access = ConfigManager.getDBAccess();
    url.setText(access.getString("URL"));
    username.setText(access.getString("Username"));
    logIn.addActionListener(
        e -> {
          String newUrl = url.getText();
          String newUsername = username.getText();
          String newPassword = new String(password.getPassword());
          if (DBConnection.tryLogIn(newUrl, newUsername, newPassword)) {
            access.put("URL", newUrl);
            access.put("Username", newUsername);
            access.put("Password", newPassword);
            ConfigManager.updateFile();
            JOptionPane.showMessageDialog(
                getTopComponent(), "Die Verbindung wurde erfolgreich erstellt!");
            back();
          } else {
            JOptionPane.showMessageDialog(
                getTopComponent(),
                "Es kann leider keine Verbindung hergestellt werden,\n bitte \u00fcberpr\u00fcfen sie die Eingaben nach Fehlern");
          }
        });
    cancel.addActionListener(
        e -> {
          back();
        });
  }

  @Override
  public @NotNull JComponent getContent() {
    return main;
  }
}
