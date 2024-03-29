package kernbeisser.Windows.ChangePassword;

import java.awt.*;
import kernbeisser.Enums.Setting;

public enum PasswordStrength {
  LENGTH_TO_SMALL(
      "Das Passwort muss mindestens "
          + Setting.MIN_PASSWORD_LENGTH.getIntValue()
          + " Zeichen lang sein",
      new Color(0xEE0000)),
  TO_LOW("Zu niedrig", new Color(0xEE0000)),
  LOW("Niedrig", new Color(0xFF7300)),
  NORMAL("Normal", new Color(0x00AEFF)),
  GOOD("Gut", new Color(0x44FF00)),
  OPTIMAL("Optimal", new Color(0x00FF89)),
  LEGENDARY("Besser geht's nicht :)", new Color(0xFF00E0));

  PasswordStrength(String hint, Color color) {
    this.color = color;
    this.hint = hint;
  }

  private final Color color;

  private final String hint;

  public String getHint() {
    return hint;
  }

  public Color getColor() {
    return color;
  }
}
