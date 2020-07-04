package kernbeisser.Security;

import javassist.util.proxy.MethodHandler;
import javassist.util.proxy.ProxyFactory;
import kernbeisser.Exeptions.AccessDeniedException;
import kernbeisser.Useful.Tools;
import kernbeisser.Windows.LogIn.LogInModel;
import lombok.SneakyThrows;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

public class Proxy {

    /**
     * creates a empty instance of a security checked object, which is used to test functions for accessibility
     * @param parent the parent for the Object
     * @param <T> the type of the secure instance
     * @return a empty proxy object
     */
    public static <T> T getEmptySecurityInstance(T parent){
        if (ProxyFactory.isProxyClass(parent.getClass()))return parent;
        ProxyFactory factory = new ProxyFactory();
        factory.setSuperclass(parent.getClass());
        try {
            return (T) factory.create(new Class[0], new Object[0], new SecurityHandler());
        } catch (InstantiationException | InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {
            e.printStackTrace();
            return parent;
        }
    }

    /**
     * creates a empty instance of a security checked object, which is used to test functions for accessibility
     * @param clazz the clazz for the Object
     * @param <T> the type of the secure instance
     * @return a empty proxy object
     */
    public static <T> T getEmptySecurityInstance(Class<T> clazz){
        if(ProxyFactory.isProxyClass(clazz))return Tools.createWithoutConstructor(clazz);
        ProxyFactory factory = new ProxyFactory();
        factory.setSuperclass(clazz);
        try {
            return (T) factory.create(new Class[0], new Object[0], new SecurityHandler());
        } catch (InstantiationException | InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {
            e.printStackTrace();
            return Tools.createWithoutConstructor(clazz);
        }
    }



    /**
     * wraps a Object with a Proxy which is used to check if the PermissionSet contains the required Keys to
     * run the Function else it throws a AccessDeniedException
     * @param parent the Object which should get wrap into a Proxy Object
     * @param <T> the type of the Object
     * @return a Proxy which extends T
     */
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

    /**
     * do the same like getSecureInstance only for a whole collection
     * @param collection collection  with Objects
     * @param <C> the collection with the values
     * @param <V> the type of the Object which gets transformed
     * @return the collection c with Proxy extends v values
     */
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


    /**
     * returns if an Object is a ProxyInstance
     * @param o the object
     * @return if the object is a ProxyInstance
     */
    public static boolean isProxyInstance(Object o) {
        return ProxyFactory.isProxyClass(o.getClass());
    }


    /**
     * reutns the SecurityHandler of an specified object
     * @param o the ProxyInstance
     * @return the SecurityHandler from the object
     */
    @SneakyThrows
    public static SecurityHandler getHandler(Object o){
        Field handler = o.getClass().getDeclaredField("handler");
        handler.setAccessible(true);
        return (SecurityHandler) handler.get(o);
    }



    /**
     * the class which handel when a method is called by a proxy instance
     * SecurityHandler checks if the current load PermissionSet contains all keys
     * which specified in Key annotation to run the method when it doesn't than
     * the security handler throws a AccessDeniedException
     */
    static class SecurityHandler implements MethodHandler {
        public Object invoke(Object proxy, Method proxyMethod,Method original, Object[] args) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, AccessDeniedException
        {
            Key key = proxyMethod.getAnnotation(Key.class);
            Object out;
            if(key==null || MasterPermissionSet.hasPermissions(key.value()))
                    out = original.invoke(proxy, args);
                else throw new AccessDeniedException("User["+LogInModel.getLoggedIn().getId() + "] cannot access " + original + " because the user has not the required Keys:" + Arrays.toString(key.value()));
            return out;
        }
    }
}
