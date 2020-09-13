package kernbeisser.CustomComponents;

import java.awt.*;
import java.util.function.Consumer;
import java.util.function.Supplier;
import javax.swing.*;
import jiconfont.swing.IconFontSwing;
import kernbeisser.Windows.LogIn.LogInModel;
import kernbeisser.Windows.MVC.IController;
import kernbeisser.Windows.MVC.IView;
import kernbeisser.Windows.MVC.Utils;

public class ControllerButton extends JButton {

  public static final Supplier<? extends IController<?, ?>> EMPTY =
      () -> IController.createFakeController(new JPanel());

  public <V extends IController<?, ?>> ControllerButton(
      Supplier<V> controller, Consumer<V> action) {
    V instance = controller.get();
    IView<?> view = Utils.getNotInitializedView(instance.getClass());
    setIcon(IconFontSwing.buildIcon(view.getTabIcon(), 20, new Color(0xFF00CCFF)));
    setEnabled(LogInModel.getLoggedIn().hasPermission(instance.getRequiredKeys()));
    setHorizontalAlignment(SwingConstants.LEFT);
    addActionListener(e -> action.accept(controller.get()));
  }
}
