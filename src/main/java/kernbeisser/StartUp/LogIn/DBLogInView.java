package kernbeisser.StartUp.LogIn;

import javax.swing.*;
import kernbeisser.Config.Config;
import kernbeisser.Config.Config.DBAccess;
import kernbeisser.DBConnection.DBConnection;
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

  @Linked private DBLogInController controller;

  @Override
  public void initialize(DBLogInController controller) {
    DBAccess access = Config.getConfig().getDbAccess();
    url.setText(access.getUrl());
    username.setText(access.getUsername());
    logIn.addActionListener(
        e -> {
          DBAccess newAccess =
              new DBAccess(url.getText(), username.getText(), new String(password.getPassword()));
          if (DBConnection.tryLogIn(newAccess)) {
            Config.getConfig().setDbAccess(newAccess);
            Config.safeFile();
            JOptionPane.showMessageDialog(
                getTopComponent(), "Die Verbindung wurde erfolgreich erstellt!");
            back();
          } else {
            JOptionPane.showMessageDialog(
                getTopComponent(),
                "Es kann leider keine Verbindung hergestellt werden,\n bitte \u00fcberpr\u00fcfen sie die Eingaben nach Fehlern");
          }
        });
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
}
