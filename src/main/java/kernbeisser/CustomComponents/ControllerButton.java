package kernbeisser.CustomComponents;

import java.awt.*;
import java.util.function.Consumer;
import java.util.function.Supplier;
import javax.swing.*;
import jiconfont.swing.IconFontSwing;
import kernbeisser.Useful.Tools;
import kernbeisser.Windows.LogIn.LogInModel;
import kernbeisser.Windows.MVC.IController;
import kernbeisser.Windows.MVC.IView;
import kernbeisser.Windows.MVC.Utils;

public class ControllerButton extends JButton {

  public <V extends IController<?, ?>> ControllerButton(Supplier<V> controller, Class<V> clazz) {
    this(controller, clazz, (e) -> e.openTab(Utils.getNotInitializedView(clazz).getTitle()));
  }

  public <V extends IController<?, ?>> ControllerButton(
      Supplier<V> controller, Class<V> clazz, Consumer<V> action) {
    IView<?> view = Utils.getNotInitializedView(clazz);
    setIcon(IconFontSwing.buildIcon(view.getTabIcon(), 20, new Color(0xFF00CCFF)));
    setEnabled(
        LogInModel.getLoggedIn()
            .hasPermission(Tools.createWithoutConstructor(clazz).getRequiredKeys()));
    setHorizontalAlignment(SwingConstants.LEFT);
    addActionListener(e -> action.accept(controller.get()));
  }

  public static ControllerButton empty() {
    return new ControllerButton(
        () -> IController.createFakeController(new JPanel()), IController.FakeController.class);
  }
}
