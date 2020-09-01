package kernbeisser.Windows.ChangePassword;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.*;
import jiconfont.IconCode;
import jiconfont.icons.font_awesome.FontAwesome;
import kernbeisser.Windows.MVC.IView;
import org.jetbrains.annotations.NotNull;

public class ChangePasswordView implements IView<ChangePasswordController> {
  private JPanel main;
  private JButton changePassword;
  private JPasswordField currentPassword;
  private JPasswordField newPassword;
  private JPasswordField repeatedNewPassword;
  private JLabel message;
  private JLabel passwordHint;
  private JLabel currentPasswordLable;

  @Override
  public void initialize(ChangePasswordController controller) {
    changePassword.addActionListener(e -> controller.changePassword());
    newPassword.addKeyListener(
        new KeyAdapter() {
          @Override
          public void keyReleased(KeyEvent e) {
            controller.refreshPasswordStrength();
          }
        });
  }

  @Override
  public @NotNull JComponent getContent() {
    return main;
  }

  String getNewPassword() {
    return new String(newPassword.getPassword());
  }

  String getCurrentPassword() {
    return new String(currentPassword.getPassword());
  }

  String getRepeatedPassword() {
    return new String(repeatedNewPassword.getPassword());
  }

  public void passwordsDontMatch() {
    message.setText("Bitte Wiederholen sie das Passwort korrekt.");
  }

  void setVerifyWithOldEnable(boolean enable) {
    currentPasswordLable.setVisible(enable);
    currentPassword.setVisible(enable);
  }

  void setPasswordStrength(PasswordStrength passwordStrength) {
    passwordHint.setText("Stärke: " + passwordStrength.getHint());
    passwordHint.setForeground(passwordStrength.getColor());
  }

  public void passwordsMatch() {
    message.setText("");
  }

  public void passwordCannotChanged() {
    JOptionPane.showMessageDialog(
        getTopComponent(), "Das Password entspricht nicht den Vorraussetzungen");
  }

  public void passwordChanged() {
    JOptionPane.showMessageDialog(getTopComponent(), "Das Password wurde erfolgreich geändert");
  }

  public void currentPasswordEnteredWrong() {
    JOptionPane.showMessageDialog(
        getTopComponent(), "Um das Passwort zu ändern müssen\n sie ihr altes Passwort bestätigen");
  }

  @Override
  public IconCode getTabIcon() {
    return FontAwesome.KEY;
  }

  @Override
  public @NotNull Dimension getSize() {
    return new Dimension(500, 500);
  }

  @Override
  public String getTitle() {
    return "Passwort ändern";
  }
}
