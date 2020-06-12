package kernbeisser.Security;

import javassist.bytecode.DuplicateMemberException;
import javassist.util.proxy.MethodHandler;
import javassist.util.proxy.ProxyFactory;
import kernbeisser.DBEntities.Permission;
import kernbeisser.DBEntities.User;
import kernbeisser.Exeptions.AccessDeniedException;
import kernbeisser.Windows.LogIn.LogInModel;
import org.apache.commons.beanutils.BeanUtils;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

public class Proxy {
    public static <T> T getSecureInstance(T parent){
        if (ProxyFactory.isProxyClass(parent.getClass()))return parent;
        ProxyFactory factory = new ProxyFactory();
        factory.setSuperclass(parent.getClass());
        try {
            return (T) factory.create(new Class[0], new Object[0], new SecurityHandler(parent));
        } catch (InstantiationException | InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {
            e.printStackTrace();
            return parent;
        }
    }

    public static <C extends Collection<V>,V> C getSecureInstances(C collection){
        if(collection.size()==0)return collection;
        V any = collection.iterator().next();
        if (ProxyFactory.isProxyClass(any.getClass()))return collection;
        Collection<V> buffer = new ArrayList<>(collection);
        collection.clear();
        ProxyFactory factory = new ProxyFactory();
        factory.setSuperclass(any.getClass());
        buffer.forEach(parent -> {
            try {
                collection.add((V) factory.create(new Class[0], new Object[0], new SecurityHandler(parent)));
            } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        });
        return collection;
    }

    static class SecurityHandler implements MethodHandler {
        private final Object original;

        public SecurityHandler(Object original) {
            this.original = original;
        }
        public Object invoke(Object proxy, Method method,Method p, Object[] args) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, AccessDeniedException
        {
            Key key = method.getAnnotation(Key.class);
            Object out;
            if(key==null || PermissionSet.hasPermissions(key.value()))
            out = method.invoke(original, args);
            else throw new AccessDeniedException("User["+LogInModel.getLoggedIn().getId() + "] cannot access " + method + " because the user has not the required Keys:" + Arrays.toString(key.value()));
            return out;
        }
    }
}
