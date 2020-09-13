package kernbeisser.Windows.MVC;

import java.lang.reflect.Field;
import java.util.Objects;
import kernbeisser.Useful.Tools;

public class Utils {
  public static Field getLinkedViewField(Class<?> controllerClass) {
    for (Field declaredField : Tools.getAllFields(controllerClass)) {
      try {
        declaredField.getType().getDeclaredMethod("initialize", controllerClass);
        declaredField.setAccessible(true);
        return declaredField;
      } catch (NoSuchMethodException e) {
        try {
          declaredField.getType().getDeclaredMethod("initialize", controllerClass.getSuperclass());
          declaredField.setAccessible(true);
          return declaredField;
        } catch (NoSuchMethodException ignored) {
        }
      }
    }
    return null;
  }

  public static IView<?> getNotInitializedView(Class<? extends IController> controllerClass) {
    return (IView<?>)
        Tools.createWithoutConstructor(
            Objects.requireNonNull(getLinkedViewField(controllerClass)).getType());
  }
}
