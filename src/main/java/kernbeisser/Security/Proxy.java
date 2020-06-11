package kernbeisser.Security;

import kernbeisser.DBEntities.User;
import kernbeisser.Exeptions.AccessDeniedException;
import kernbeisser.Windows.LogIn.LogInModel;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class Proxy {
    public static <T> T getSecureInstance(T parent,Class<T> interfacez){
        return (T) java.lang.reflect.Proxy.newProxyInstance(parent.getClass().getClassLoader(),
                                                                   new Class[] { interfacez },
                                                                   new SecurityHandler(parent));
    }
    static class SecurityHandler implements InvocationHandler {
        private final Object original;
        public SecurityHandler(Object original) {
            this.original = original;
        }
        public Object invoke(Object proxy, Method method, Object[] args) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, AccessDeniedException
        {
            Key key = method.getAnnotation(Key.class);
            Object out;
            if(key==null||LogInModel.getLoggedIn().hasPermission(key.value()))
            out = method.invoke(original, args);
            else throw new AccessDeniedException();
            return out;
        }
    }
}
