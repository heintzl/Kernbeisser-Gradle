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
        boolean verified = verify(input);
        if(!verified){
            JOptionPane.showMessageDialog(input,"Sie müssen das folgende feld ausfüllen");
            Tools.ping(input);
        }
        return verified;
    }
}
