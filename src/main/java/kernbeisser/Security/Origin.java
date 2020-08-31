package kernbeisser.Security;

import kernbeisser.Useful.Tools;

public class Origin {

    public static boolean isCaller(Class<?> clazz){
        return clazz.getCanonicalName().equals(Tools.getCallerStackTraceElement(3).getClassName());
    }
}
