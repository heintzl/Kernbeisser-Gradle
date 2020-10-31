package kernbeisser.Windows.MVC;

import java.awt.Component;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.function.Predicate;
import javax.swing.JComponent;
import lombok.Getter;

public class ControllerReference<C extends Controller<?, ?>> implements ComponentListener {
  @Getter private final C controller;

  private ControllerReference(C controller) {
    this.controller = controller;
  }

  public static <C extends Controller<?, ?>> void putOn(JComponent component, C controller) {
    component.addComponentListener(new ControllerReference<>(controller));
  }

  public static ControllerReference<?> traceBack(Component start) {
    for (Component component = start; component != null; component = component.getParent()) {
      ComponentListener[] controllerReferences = component.getListeners(ComponentListener.class);
      for (ComponentListener componentListener : controllerReferences) {
        if (componentListener instanceof ControllerReference) {
          return (ControllerReference<?>) componentListener;
        }
      }
    }
    throw new UnsupportedOperationException("cannot trace view back");
  }

  public static ControllerReference<?> traceBack(
      Component start, Predicate<ControllerReference<?>> validator) {
    for (Component component = start; component != null; component = component.getParent()) {
      ComponentListener[] controllerReferences = component.getListeners(ComponentListener.class);
      for (ComponentListener componentListener : controllerReferences) {
        if (componentListener instanceof ControllerReference) {
          ControllerReference<?> reference = (ControllerReference<?>) componentListener;
          if (validator.test(reference)) return reference;
        }
      }
    }
    throw new UnsupportedOperationException("cannot trace view back");
  }

  public static boolean isOn(Component component, Controller<?, ?> controller) {
    ComponentListener[] controllerReferences = component.getListeners(ComponentListener.class);
    for (ComponentListener componentListener : controllerReferences) {
      if (componentListener instanceof ControllerReference
          && ((ControllerReference<?>) componentListener).controller == controller) {
        return true;
      }
    }
    return false;
  }

  @Override
  public void componentResized(ComponentEvent e) {}

  @Override
  public void componentMoved(ComponentEvent e) {}

  @Override
  public void componentShown(ComponentEvent e) {}

  @Override
  public void componentHidden(ComponentEvent e) {}
}
