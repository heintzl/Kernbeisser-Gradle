package kernbeisser.Windows.MVC;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import kernbeisser.Useful.Tools;

public class ViewFactory {
  public static void initializeView(IController<?, ?> controller) {
    Field viewField = Utils.getLinkedViewField(controller.getClass());
    IView<?> v = (IView<?>) Tools.createWithoutConstructor(viewField.getType());
    linkViewControllerFields(v, controller);
    try {
      viewField.set(controller, v);
    } catch (IllegalAccessException e) {
      Tools.showUnexpectedErrorWarning(e);
    }
    callSetupUiMethod(v);
    callInitializeMethod(v, controller);
  }

  private static void linkViewControllerFields(IView<?> view, IController<?, ?> controller) {
    Collection<Field> fields = Tools.getWithAnnotation(controller.getClass(), Linked.class);
    for (Field declaredField : Tools.getAllFields(view.getClass())) {
      if (declaredField.isAnnotationPresent(Linked.class)) {
        declaredField.setAccessible(true);
        if (declaredField.getType().isAssignableFrom(controller.getClass())) {
          try {
            declaredField.set(view, controller);
            continue;
          } catch (IllegalAccessException e) {
            Tools.showUnexpectedErrorWarning(e);
          }
        }
        for (Field field : fields) {
          if (field.getType().equals(declaredField.getType())) {
            try {
              declaredField.set(view, field.get(controller));
            } catch (IllegalAccessException e) {
              Tools.showUnexpectedErrorWarning(e);
            }
          }
        }
      }
    }
  }

  private static void callInitializeMethod(IView<?> view, IController<?, ?> controller) {
    try {
      Method initMethod = view.getClass().getDeclaredMethod("initialize", IController.class);
      initMethod.setAccessible(true);
      initMethod.invoke(view, controller);
    } catch (NoSuchMethodException | IllegalAccessException e) {
      Tools.showUnexpectedErrorWarning(e);
    } catch (InvocationTargetException e) {
      e.getCause().printStackTrace();
    }
  }

  private static void callSetupUiMethod(IView<?> view) {
    try {
      Method setUpUiComponents = view.getClass().getDeclaredMethod("$$$setupUI$$$");
      setUpUiComponents.setAccessible(true);
      setUpUiComponents.invoke(view);
    } catch (NoSuchMethodException ignored) {
    } catch (IllegalAccessException e) {
      e.printStackTrace();
      Tools.showUnexpectedErrorWarning(e);
    } catch (InvocationTargetException e) {
      e.getCause().printStackTrace();
    }
  }
}
