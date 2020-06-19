package kernbeisser.CustomComponents.AccessChecking;

import kernbeisser.Enums.Colors;
import kernbeisser.Exeptions.AccessDeniedException;
import kernbeisser.Security.AccessConsumer;
import kernbeisser.Security.AccessSupplier;

import javax.swing.*;
import java.awt.*;

public class AccessCheckingLabel extends JLabel {
    void setText(AccessSupplier<String> text){
        try {
            setText(text.get());
            setForeground(Colors.LABEL_FOREGROUND.getColor());
        } catch (AccessDeniedException e) {
            setText("[Keine Leseberechtigung]");
            setForeground(Color.RED);
        }
    }
}
