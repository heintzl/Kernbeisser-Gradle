package kernbeisser.Windows.ChangePassword;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.*;
import jiconfont.IconCode;
import jiconfont.icons.font_awesome.FontAwesome;
import kernbeisser.Windows.MVC.IView;
import kernbeisser.Windows.MVC.Linked;
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
  @Linked private ChangePasswordController controller;

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
    message.setText("Bitte wiederhole das Passwort korrekt.");
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
        getTopComponent(), "Das Password entspricht nicht den Vorraussetzungen.");
  }

  public void passwordChanged() {
    JOptionPane.showMessageDialog(getTopComponent(), "Das Password wurde erfolgreich geändert.");
  }

  public void currentPasswordEnteredWrong() {
    JOptionPane.showMessageDialog(
        getTopComponent(),
        "Um das Passwort zu ändern, musst\n" + "du dein altes Passwort bestätigen.");
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

  // @spotless:off

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /** Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        main = new JPanel();
        main.setLayout(new GridLayoutManager(14, 2, new Insets(5, 10, 5, 10), -1, -1));
        changePassword = new JButton();
        changePassword.setText("Passwort ändern");
        main.add(changePassword, new GridConstraints(13, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer1 = new Spacer();
        main.add(spacer1, new GridConstraints(13, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final Spacer spacer2 = new Spacer();
        main.add(spacer2, new GridConstraints(12, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        currentPasswordLable = new JLabel();
        currentPasswordLable.setText("Altes Passwort");
        main.add(currentPasswordLable, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        currentPassword = new JPasswordField();
        main.add(currentPassword, new GridConstraints(1, 0, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JLabel label1 = new JLabel();
        label1.setText("Neues Passwort");
        main.add(label1, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        newPassword = new JPasswordField();
        main.add(newPassword, new GridConstraints(3, 0, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JLabel label2 = new JLabel();
        label2.setText("Neues Passwort wiederholen");
        main.add(label2, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        repeatedNewPassword = new JPasswordField();
        main.add(repeatedNewPassword, new GridConstraints(5, 0, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        message = new JLabel();
        message.setForeground(new Color(-4567784));
        message.setText("");
        main.add(message, new GridConstraints(4, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        passwordHint = new JLabel();
        passwordHint.setText("");
        main.add(passwordHint, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label3 = new JLabel();
        label3.setText("Starke Passwörter beinhalten:");
        main.add(label3, new GridConstraints(6, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label4 = new JLabel();
        label4.setText("Zahlen [0-9]");
        main.add(label4, new GridConstraints(7, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label5 = new JLabel();
        label5.setText("Kleinbuchstaben [a-z]");
        main.add(label5, new GridConstraints(8, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label6 = new JLabel();
        label6.setText("Großbuchstaben [A-Z]");
        main.add(label6, new GridConstraints(9, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label7 = new JLabel();
        label7.setText("Sonderzeichen [@,#,$,%,^,,*]");
        label7.setDisplayedMnemonic(',');
        label7.setDisplayedMnemonicIndex(25);
        main.add(label7, new GridConstraints(10, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label8 = new JLabel();
        label8.setText("Und sind am besten länger als 8 Zeichen");
        main.add(label8, new GridConstraints(11, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    }

    /** @noinspection ALL */
    public JComponent $$$getRootComponent$$$() {
        return main;
    }

    // @spotless:on
}
