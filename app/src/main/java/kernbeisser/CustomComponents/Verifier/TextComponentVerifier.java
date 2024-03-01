package kernbeisser.CustomComponents.Verifier;

import java.awt.*;
import javax.swing.*;
import javax.swing.text.JTextComponent;
import kernbeisser.Useful.Tools;

public abstract class TextComponentVerifier extends InputVerifier {
  @Override
  public final boolean verify(JComponent input) {
    return verify((JTextComponent) input);
  }

  public abstract boolean verify(JTextComponent component);

  @Override
  public boolean shouldYieldFocus(JComponent input) {
    boolean isOkay = verify(input);
    input.setForeground(new Color(isOkay ? 0x0 : 0xFF0000));
    if (!isOkay) {
      showHint(input);
    }
    return true;
  }

  public void showHint(JComponent component) {
    Tools.showHint(component);
  }
}
