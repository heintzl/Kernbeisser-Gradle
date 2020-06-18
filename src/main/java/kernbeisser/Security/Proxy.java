package kernbeisser.Security;

import javassist.bytecode.DuplicateMemberException;
import javassist.util.proxy.MethodHandler;
import javassist.util.proxy.ProxyFactory;
import kernbeisser.DBEntities.Permission;
import kernbeisser.DBEntities.User;
import kernbeisser.Exeptions.AccessDeniedException;
import kernbeisser.Useful.Tools;
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
            T proxy = (T) factory.create(new Class[0], new Object[0], new SecurityHandler());
            Tools.copyInto(parent,proxy);
            return proxy;
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
                V proxy = (V) factory.create(new Class[0], new Object[0], new SecurityHandler());
                Tools.copyInto(parent,proxy);
                collection.add(proxy);
            } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        });
        return collection;
    }

    public static boolean isProxyInstance(Object o) {
        return ProxyFactory.isProxyClass(o.getClass());
    }

    static class SecurityHandler implements MethodHandler {
        public Object invoke(Object proxy, Method proxyMethod,Method original, Object[] args) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, AccessDeniedException
        {
            Key key = proxyMethod.getAnnotation(Key.class);
            Object out;
            if(key==null || PermissionSet.hasPermissions(key.value()))
                out = original.invoke(proxy, args);
            else throw new AccessDeniedException("User["+LogInModel.getLoggedIn().getId() + "] cannot access " + original + " because the user has not the required Keys:" + Arrays.toString(key.value()));
            return out;
        }
    }
}
