package kernbeisser.CustomComponents;

import java.awt.Color;
import java.util.function.Consumer;
import java.util.function.Supplier;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import jiconfont.swing.IconFontSwing;
import kernbeisser.Useful.Tools;
import kernbeisser.Windows.LogIn.LogInModel;
import kernbeisser.Windows.MVC.ComponentController.ComponentController;
import kernbeisser.Windows.MVC.Controller;
import kernbeisser.Windows.MVC.IView;

public class ControllerButton extends JButton {

  public <V extends Controller<?, ?>> ControllerButton(Supplier<V> controller, Class<V> clazz) {
    this(controller, clazz, (e) -> e.openTab());
  }

  public <V extends Controller<?, ?>> ControllerButton(
      Supplier<V> controller, Class<V> clazz, Consumer<V> action) {
    IView<?> view =
        Tools.createWithoutConstructor(Tools.createWithoutConstructor(clazz).getViewClass());
    setIcon(
        IconFontSwing.buildIcon(
            view.getTabIcon(), Tools.scaleWithLabelScalingFactor(16), new Color(0xFF00CCFF)));
    setRolloverIcon(
        IconFontSwing.buildIcon(
            view.getTabIcon(), Tools.scaleWithLabelScalingFactor(16), new Color(0x04ACCD)));
    setEnabled(
        LogInModel.getLoggedIn()
            .hasPermission(Tools.createWithoutConstructor(clazz).getRequiredKeys()));
    setHorizontalAlignment(SwingConstants.LEFT);
    Tools.scaleFont(this, 1.1);
    addActionListener(e -> action.accept(controller.get()));
  }

  public static ControllerButton empty() {
    return new ControllerButton(
        () -> new ComponentController(new JPanel()),
        ComponentController.class,
        Controller::openTab);
  }
}
