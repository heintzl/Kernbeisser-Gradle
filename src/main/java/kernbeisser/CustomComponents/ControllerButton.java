package kernbeisser.CustomComponents;

import java.awt.Color;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Supplier;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import jiconfont.swing.IconFontSwing;
import kernbeisser.Exeptions.ClassIsSingletonException;
import kernbeisser.Exeptions.PermissionKeyRequiredException;
import kernbeisser.Security.StaticMethodTransformer.StaticMethodTransformer;
import kernbeisser.Useful.Tools;
import kernbeisser.Windows.MVC.ComponentController.ComponentController;
import kernbeisser.Windows.MVC.Controller;
import kernbeisser.Windows.MVC.IModel;
import kernbeisser.Windows.MVC.IView;

public class ControllerButton extends JButton {

  public <
          V extends IView<? extends Controller<? extends V, ? extends M>>,
          M extends IModel<? extends Controller<? extends V, ? extends M>>,
          C extends Controller<V, M>>
      ControllerButton(Supplier<C> controller, Class<C> clazz) {
    this(controller, clazz, false);
  }

  public <
          V extends IView<? extends Controller<? extends V, ? extends M>>,
          M extends IModel<? extends Controller<? extends V, ? extends M>>,
          C extends Controller<V, M>>
      ControllerButton(Supplier<C> controller, Class<C> clazz, boolean preInit) {
    this(controller, clazz, Controller::openTab, preInit);
  }

  public <
          V extends IView<? extends Controller<? extends V, ? extends M>>,
          M extends IModel<? extends Controller<? extends V, ? extends M>>,
          C extends Controller<V, M>>
      ControllerButton(Supplier<C> controller, Class<C> clazz, Consumer<C> action) {
    this(controller, clazz, action, false);
  }

  public <
          V extends IView<? extends Controller<? extends V, ? extends M>>,
          M extends IModel<? extends Controller<? extends V, ? extends M>>,
          C extends Controller<V, M>>
      ControllerButton(
          Supplier<C> controller, Class<C> clazz, Consumer<C> action, boolean preInit) {
    Class<V> vClass = Controller.getViewClass(clazz);
    IView<?> iView = StaticMethodTransformer.createStaticInterface(IView.class, vClass);
    setIcon(
        IconFontSwing.buildIcon(
            iView.getTabIcon(), Tools.scaleWithLabelScalingFactor(16), new Color(0xFF00CCFF)));
    setRolloverIcon(
        IconFontSwing.buildIcon(
            iView.getTabIcon(), Tools.scaleWithLabelScalingFactor(16), new Color(0x04ACCD)));
    AtomicReference<C> preLoadControllerRef = new AtomicReference<>();
    setHorizontalAlignment(SwingConstants.LEFT);
    Tools.scaleFont(this, 1.1);
    addActionListener(e -> action.accept(controller.get()));
    if (preInit) {
      setEnabled(false);
      SwingUtilities.invokeLater(
          () -> {
            try {
              preLoadControllerRef.set(controller.get());
              setEnabled(true);
            } catch (PermissionKeyRequiredException | ClassIsSingletonException e) {
              setEnabled(false);
            }
          });
    }
  }

  public static ControllerButton empty() {
    return new ControllerButton(
        () -> new ComponentController(new JPanel()),
        ComponentController.class,
        Controller::openTab,
        false);
  }
}
