package kernbeisser.CustomComponents.Verifier;

import java.awt.*;
import javax.swing.*;
import javax.swing.text.JTextComponent;

public abstract class ParseVerifier<T extends Comparable<T>> extends TextComponentVerifier {

  public abstract T parse(String s) throws NumberFormatException;

  @Override
  public boolean verify(JTextComponent input) {
    try {
      T parsed = parse(input.getText());
      return (max() == null || parsed.compareTo(max()) <= 0)
          && (min() == null || parsed.compareTo(min()) >= 0);
    } catch (NumberFormatException e) {
      return false;
    }
  }

  public T min() {
    return null;
  }

  public T max() {
    return null;
  }

  public T checkLowerThan() {
    return null;
  }

  public T checkHigherThan() {
    return null;
  }

  @Override
  public boolean shouldYieldFocus(JComponent input) {
    boolean verified = verify(input);
    if (verified) {
      input.setForeground(new Color(0x0));
      T parsed = parse(((JTextComponent) input).getText());
      if ((checkHigherThan() != null && parsed.compareTo(checkHigherThan()) > 0)
          || (checkLowerThan() != null && parsed.compareTo(checkLowerThan()) < 0)) {
        return JOptionPane.showConfirmDialog(
                input, "Ist die eingegebene Zahl " + parsed + " korrekt?")
            == 0;
      }
    } else {
      input.setForeground(new Color(0xA00606));
    }
    return verified;
  }
}
