package kernbeisser.CustomComponents;

import java.awt.*;
import java.util.function.Consumer;
import javax.swing.*;
import jiconfont.swing.IconFontSwing;
import kernbeisser.Security.MasterPermissionSet;
import kernbeisser.Windows.Controller;

public class ControllerButton extends JButton {
  public <V extends Controller<?, ?>> ControllerButton(V controller, Consumer<V> action) {
    setEnabled(MasterPermissionSet.hasPermissions(controller.getRequiredKeys()));
    addActionListener(e -> action.accept(controller));
    setIcon(
        IconFontSwing.buildIcon(
            controller.getInitializedView().getTabIcon(), 20, new Color(0xFF00CCFF)));
    setHorizontalAlignment(SwingConstants.LEFT);
  }
}
