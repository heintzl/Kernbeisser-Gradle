package kernbeisser.Windows.LogIn.SimpleLogIn;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import javax.swing.*;
import jiconfont.IconCode;
import jiconfont.icons.font_awesome.FontAwesome;
import kernbeisser.Useful.Tools;
import kernbeisser.Windows.PreLoaded;
import kernbeisser.Windows.View;
import org.jetbrains.annotations.NotNull;

public class SimpleLogInView implements View<SimpleLogInController> {
  private JButton logIn;
  private JPasswordField password;
  private JTextField username;
  private JPanel main;


  @PreLoaded
  private SimpleLogInController controller;

  char[] getPassword() {
    return password.getPassword();
  }

  String getUsername() {
    return username.getText();
  }

  public void accessDenied() {
    JOptionPane.showMessageDialog(
        getTopComponent(), "Zugriff verweigert. Anmeldedaten sind ungültig!");
  }

  public void permissionRequired() {
    JOptionPane.showMessageDialog(
        getTopComponent(),
        "Zugriff verweigert.\nIhr Benutzerkonto hat leider nicht die Berechtigung sich anzumelden.\nSie können es bei einem Admin freischalten lassen.");
  }

  @Override
  public void initialize(SimpleLogInController controller) {
    logIn.addActionListener(e -> controller.logIn());
    password.addActionListener(
        e -> {
          controller.logIn();
        });
    username.addActionListener(e -> password.requestFocus());
    // TODO the following lines are for testing only! Remove from production code
    File file = new File("testUser.txt");
    if (file.exists()) {
      try {
        List<String> fileLines = Files.readAllLines(file.toPath());
        username.setText(fileLines.get(0));
        password.setText(fileLines.get(1));
      } catch (IOException e) {
        Tools.showUnexpectedErrorWarning(e);
      }
    }
    // TODO test code; remove  up to here
  }

  @Override
  public @NotNull JComponent getContent() {
    return main;
  }

  @Override
  public IconCode getTabIcon() {
    return FontAwesome.SIGN_IN;
  }
}
