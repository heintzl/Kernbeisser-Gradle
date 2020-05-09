package kernbeisser.CustomComponents.Verifier;

import javax.swing.*;
import javax.swing.text.JTextComponent;

public abstract class TextComponentVerifier extends InputVerifier {
    @Override
    public final boolean verify(JComponent input) {
        return verify((JTextComponent)input);
    }

    public abstract boolean verify(JTextComponent component);
}
