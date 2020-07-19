package kernbeisser.CustomComponents.Verifier;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import kernbeisser.Useful.Tools;

public class NotNullVerifier extends TextComponentVerifier {
  @Override
  public boolean verify(JTextComponent component) {
    return !component.getText().replace(" ", "").equals("");
  }

  @Override
  public boolean shouldYieldFocus(JComponent input) {
    if (!verify(input)) {
      Tools.showHint(input);
    }
    return true;
  }
}
