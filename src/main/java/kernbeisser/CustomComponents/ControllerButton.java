package kernbeisser.CustomComponents;

import java.awt.*;
import java.util.function.Consumer;
import javax.swing.*;
import jiconfont.swing.IconFontSwing;
import kernbeisser.Security.MasterPermissionSet;
import kernbeisser.Windows.MVC.Controller;

public class ControllerButton extends JButton {

  private final Controller<?, ?> controller;

  public <V extends Controller<?, ?>> ControllerButton(V controller, Consumer<V> action) {
    this.controller = controller;
    setEnabled(false);
    addActionListener(e -> action.accept(controller));
    setHorizontalAlignment(SwingConstants.LEFT);
  }

  public void loadUI() {
    setIcon(IconFontSwing.buildIcon(controller.getView().getTabIcon(), 20, new Color(0xFF00CCFF)));
    setEnabled(MasterPermissionSet.hasPermissions(controller.getRequiredKeys()));
  }

  public Controller<?, ?> getController() {
    return controller;
  }
}
