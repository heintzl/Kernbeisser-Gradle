package kernbeisser.Unsafe;

import java.lang.reflect.Field;
import sun.misc.Unsafe;

public class UnsafeUtils {
  public static sun.misc.Unsafe unsafe = create();

  public static <T> T setClass(Object o, Class<T> clazz) throws InstantiationException {
    Object patternObject = unsafe.allocateInstance(clazz);
    unsafe.getAndSetInt(o, 8, unsafe.getInt(patternObject, 8));
    return (T) o;
  }

  private static sun.misc.Unsafe create() {
    Field field;
    try {
      field = Unsafe.class.getDeclaredField("theUnsafe");
    } catch (NoSuchFieldException e) {
      throw new RuntimeException();
    }
    field.setAccessible(true);
    try {
      return (sun.misc.Unsafe) field.get(null);
    } catch (IllegalAccessException e) {
      throw new RuntimeException();
    }
  }
}
