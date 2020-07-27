package kernbeisser.CustomComponents.AccessChecking;

import java.awt.*;
import javax.swing.*;
import kernbeisser.Enums.Colors;
import kernbeisser.Exeptions.AccessDeniedException;
import kernbeisser.Security.AccessSupplier;

public class AccessCheckingLabel extends JLabel {
  void setText(AccessSupplier<String> text) {
    try {
      setText(text.get());
      setForeground(Colors.LABEL_FOREGROUND.getColor());
    } catch (AccessDeniedException e) {
      setText("[Keine Leseberechtigung]");
      setForeground(Color.RED);
    }
  }
}
