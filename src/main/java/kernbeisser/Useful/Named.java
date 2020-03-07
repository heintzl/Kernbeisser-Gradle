package kernbeisser.Useful;

public interface Named {
    String getName();

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
}
