package kernbeisser.CustomComponents.Verifier;

import kernbeisser.Useful.Tools;

import javax.swing.*;
import javax.swing.text.JTextComponent;

public class EmailVerifier extends TextComponentVerifier {
    @Override
    public boolean verify(JTextComponent input) {
        return input.getText().matches(
                "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])");
    }

    @Override
    public boolean shouldYieldFocus(JComponent input) {
        boolean isOkay = verify(input);
        if(!isOkay){
            JOptionPane.showMessageDialog(input,"Die eingegebene Email Adresse is nicht korrekt");
            Tools.ping(input);
        }
        return isOkay;
    }
}
