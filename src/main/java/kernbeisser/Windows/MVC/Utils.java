package kernbeisser.Windows.MVC;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Objects;
import jiconfont.IconCode;
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

  public static IconCode getTabIcon(Class<?> controllerClass) {
    Class<?> viewClass = Objects.requireNonNull(getLinkedViewField(controllerClass)).getType();
    Method method;
    try {
      method = viewClass.getDeclaredMethod("getTabIcon");
    } catch (NoSuchMethodException e) {
      throw new UnsupportedOperationException("cannot find getTabIcon in " + viewClass);
    }
    method.setAccessible(true);
    try {
      return (IconCode) method.invoke(Tools.createWithoutConstructor(viewClass));
    } catch (IllegalAccessException | InvocationTargetException e) {
      throw new UnsupportedOperationException("cannot access getTabIcon()");
    }
  }
}
