package kernbeisser.CustomComponents;

import java.awt.Color;
import java.lang.ref.SoftReference;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Supplier;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import jiconfont.swing.IconFontSwing;
import kernbeisser.Enums.PermissionKey;
import kernbeisser.Exeptions.ClassIsSingletonException;
import kernbeisser.Exeptions.PermissionKeyRequiredException;
import kernbeisser.Security.PermissionSet;
import kernbeisser.Security.StaticMethodTransformer.RestrictedAccess;
import kernbeisser.Security.StaticMethodTransformer.StaticMethodTransformer;
import kernbeisser.Useful.Tools;
import kernbeisser.Windows.MVC.ComponentController.ComponentController;
import kernbeisser.Windows.MVC.Controller;
import kernbeisser.Windows.MVC.IModel;
import kernbeisser.Windows.MVC.IView;
import kernbeisser.Windows.TabbedPane.TabbedPaneModel;

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
          Supplier<C> controllerInitializer, Class<C> clazz, Consumer<C> action, boolean preInit) {
    this(controllerInitializer, clazz, action, preInit, new PermissionKey[0]);
  }

  public <
          V extends IView<? extends Controller<? extends V, ? extends M>>,
          M extends IModel<? extends Controller<? extends V, ? extends M>>,
          C extends Controller<V, M>>
      ControllerButton(
          Supplier<C> controllerInitializer,
          Class<C> clazz,
          Consumer<C> action,
          boolean preInit,
          PermissionKey[] injected) {
    Class<V> vClass = Controller.getViewClass(clazz);
    IView<?> iView = StaticMethodTransformer.createStaticInterface(IView.class, vClass);
    setIcon(
        IconFontSwing.buildIcon(
            iView.getTabIcon(), Tools.scaleWithLabelScalingFactor(16), new Color(0xFF00CCFF)));
    setRolloverIcon(
        IconFontSwing.buildIcon(
            iView.getTabIcon(), Tools.scaleWithLabelScalingFactor(16), new Color(0x04ACCD)));
    setHorizontalAlignment(SwingConstants.LEFT);
    Tools.scaleFont(this, 1.1);

    // checking if the user has the required access to open up the window
    RestrictedAccess accessModel =
        StaticMethodTransformer.createStaticInterface(RestrictedAccess.class, clazz);
    if (PermissionSet.MASTER.contains(
        PermissionSet.asPermissionSet(injected).or(accessModel.getRequiredKeys()))) {
      AtomicReference<SoftReference<C>> controllerRef =
          new AtomicReference<>(new SoftReference<>(null));
      if (preInit) {
        setEnabled(false);
        SwingUtilities.invokeLater(
            () -> {
              try {
                controllerRef.set(new SoftReference<>(controllerInitializer.get()));
                setEnabled(true);
              } catch (PermissionKeyRequiredException | ClassIsSingletonException e) {
                setEnabled(false);
              }
            });
      }
      addActionListener(
          e -> {
            int index = TabbedPaneModel.MAIN_PANEL.indexOf((Class<Controller<?, ?>>) clazz);
            if (index != -1) {
              TabbedPaneModel.MAIN_PANEL.setSelectedIndex(index);
              return;
            }
            C controller = controllerRef.get().get();
            if (controller == null) {
              try {
                controller = controllerInitializer.get();
              } catch (PermissionKeyRequiredException | ClassIsSingletonException exception) {
                JOptionPane.showMessageDialog(
                    this,
                    "Das Fenster kann nicht geöffnet werden,\nda sie nicht die benötigte Berechtigung haben.");
                return;
              }
            }
            action.accept(controller);
          });
      setEnabled(true);
    } else setEnabled(false);
  }

  public static ControllerButton empty() {
    return new ControllerButton(
        () -> new ComponentController(new JPanel()),
        ComponentController.class,
        Controller::openTab,
        false);
  }
}
