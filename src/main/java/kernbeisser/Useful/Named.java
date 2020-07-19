package kernbeisser.Useful;

public interface Named {
  static <T extends Named> T toEnum(Class<T> c, String s) {
    if (!c.isEnum()) {
      return null;
    }
    for (T t : c.getEnumConstants()) {
      if (t.getName().equals(s)) {
        return t;
      }
    }
    return null;
  }

  String getName();
}
