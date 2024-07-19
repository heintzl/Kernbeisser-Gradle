package kernbeisser.CustomComponents;

import java.awt.Color;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CancellationException;
import java.util.function.Consumer;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import jiconfont.IconCode;
import jiconfont.swing.IconFontSwing;
import kernbeisser.Exeptions.ClassIsSingletonException;
import kernbeisser.Security.Utils.AccessSupplier;
import kernbeisser.Useful.Icons;
import kernbeisser.Useful.Tools;
import kernbeisser.Windows.LogIn.LogInModel;
import kernbeisser.Windows.MVC.*;
import kernbeisser.Windows.MVC.ComponentController.ComponentController;
import kernbeisser.Windows.TabbedPane.TabbedPaneModel;
import org.jetbrains.annotations.Nullable;
import rs.groump.*;

public class ControllerButton extends JButton {

  @Nullable private String confirmMessage;

  public <
          V extends IView<? extends Controller<? extends V, ? extends M>>,
          M extends IModel<? extends Controller<? extends V, ? extends M>>,
          C extends Controller<V, M>>
      ControllerButton(AccessSupplier<C> controllerInitializer, Class<C> clazz) {
    this(controllerInitializer, clazz, Controller::openTab);
  }

  public <
          V extends IView<? extends Controller<? extends V, ? extends M>>,
          M extends IModel<? extends Controller<? extends V, ? extends M>>,
          C extends Controller<V, M>>
      ControllerButton(
          AccessSupplier<C> controllerInitializer, Class<C> clazz, Consumer<C> action) {

    setHorizontalAlignment(SwingConstants.LEFT);
    addActionListener(
        e -> {
          Optional<Integer> tabbedPaneIndexOfView =
              tabbedPaneIndexOfView((Class<Controller<?, ?>>) clazz);
          if (tabbedPaneIndexOfView.isPresent()) {
            try {
              TabbedPaneModel.getMainPanel().setSelectedIndex(tabbedPaneIndexOfView.get());
              return;
            } catch (ArrayIndexOutOfBoundsException ignored) {
            }
          }
          if (!confirmConfirmMessage()) return;
          try {
            C controller = controllerInitializer.get();
            action.accept(controller);
          } catch (CancellationException ignored) {
          } catch (AccessDeniedException exception) {
            exception.printStackTrace();
            JOptionPane.showMessageDialog(
                this,
                "Das Fenster kann nicht geöffnet werden,\nda du nicht die benötigte Berechtigung hast.");
          } catch (ClassIsSingletonException exception) {
            JOptionPane.showMessageDialog(
                this,
                "Das Fenster kann nicht geöffnet werden, da dieses Fenster nur einmal geöffnet werden darf.");
          }
        });
    setEnabled(checkControllerAccess(controllerInitializer));
  }

  private boolean confirmConfirmMessage() {
    if (confirmMessage == null) {
      return true;
    }
    return JOptionPane.showConfirmDialog(
            null,
            confirmMessage,
            "Öffnen",
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.QUESTION_MESSAGE)
        == JOptionPane.OK_OPTION;
  }

  private Optional<Integer> tabbedPaneIndexOfView(Class<Controller<?, ?>> controllerClass) {
    int index = TabbedPaneModel.getMainPanel().indexOf(controllerClass);
    return Optional.ofNullable(index != -1 ? index : null);
  }

  public ControllerButton withIcon(IconCode iconCode) {
    setIcon(Icons.defaultIcon(iconCode, new Color(0xFF00CCFF)));
    setRolloverIcon(
        IconFontSwing.buildIcon(
            iconCode, Tools.scaleWithLabelScalingFactor(16), new Color(0x04ACCD)));
    return this;
  }

  public ControllerButton withConfirmMessage(String confirmMessage) {
    this.confirmMessage = confirmMessage;
    return this;
  }

  public static ControllerButton empty() {
    return new ControllerButton(
        () -> new ComponentController(new JPanel()),
        ComponentController.class,
        Controller::openTab);
  }

  // sadly this ist extremely slow, because every window is initialized just to check accessibility.
  private <C> boolean checkControllerAccess(AccessSupplier<C> controllerInitializer) {
    try {
      return Tools.canInvoke(controllerInitializer::get);
    } catch (ClassIsSingletonException e) {
      return false;
    }
  }

  private <C> boolean checkControllerAccess(Class<C> clazz) {
    PermissionSet userPermissions = new PermissionSet();
    Access.runWithAccessManager(
        AccessManager.ACCESS_GRANTED,
        () -> userPermissions.addAll(LogInModel.getLoggedIn().getPermissionSet()));
    for (Constructor<?> c : clazz.getDeclaredConstructors()) {
      Annotation keyAnnotation = c.getAnnotation(Key.class);
      if (keyAnnotation == null) {
        continue;
      }
      PermissionKey[] keys = ((Key) c.getAnnotation(Key.class)).value();
      if (keys.length > 0 && !userPermissions.hasPermissions(keys)) {
        return false;
      }
    }
    return true;
  }
}
