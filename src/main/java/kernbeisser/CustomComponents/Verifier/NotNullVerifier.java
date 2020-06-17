package kernbeisser.CustomComponents.Verifier;

import kernbeisser.Useful.Tools;

import javax.swing.*;
import javax.swing.text.JTextComponent;

public class NotNullVerifier extends TextComponentVerifier {
    @Override
    public boolean verify(JTextComponent component) {
        return !component.getText().replace(" ","").equals("");
    }

    @Override
    public boolean shouldYieldFocus(JComponent input) {
        if(!verify(input)) Tools.showHint(input);
        return true;
    }
}
