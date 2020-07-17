package kernbeisser.Useful;

import kernbeisser.CustomComponents.AccessChecking.Getter;
import kernbeisser.CustomComponents.ViewMainPanel;
import kernbeisser.DBConnection.DBConnection;
import kernbeisser.Exeptions.AccessDeniedException;
import kernbeisser.Enums.Setting;
import kernbeisser.Main;
import kernbeisser.Security.AccessConsumer;
import kernbeisser.Security.AccessSupplier;
import kernbeisser.Security.Proxy;
import org.apache.commons.beanutils.BeanUtils;
import sun.misc.Unsafe;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Id;
import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.*;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class Tools {
    private static Toolkit toolkit = Toolkit.getDefaultToolkit();

    public static <A extends Annotation> Collection<Field> getWithAnnotation(Class pattern, Class<A> annotation) {
        ArrayList<Field> out = new ArrayList<>();
        for (Field field : pattern.getDeclaredFields()) {
            field.setAccessible(true);
            if (field.isAnnotationPresent(annotation)) {
                out.add(field);
            }
        }
        for (Field field : pattern.getSuperclass().getDeclaredFields()) {
            field.setAccessible(true);
            if (field.isAnnotationPresent(annotation)) {
                out.add(field);
            }
        }
        return out;
    }


    public static <T> String toSting(T[] in, Function<T,String> transformer) {
        StringBuilder stringBuilder = new StringBuilder();
        for (T t : in) {
            stringBuilder.append(transformer.apply(t));
        }
        return stringBuilder.toString();
    }

    public static <T> String toSting(Collection<T> in, Function<T,String> transformer) {
        StringBuilder stringBuilder = new StringBuilder();
        for (T t : in) {
            stringBuilder.append(transformer.apply(t));
        }
        return stringBuilder.toString();
    }

    public static <R, T> R build(List<T> in, R r, BiFunction<R,T,R> builder) {
        for (T t : in) {
            r = builder.apply(r, t);
        }
        return r;
    }

    public static int getScreenWidth() {
        return toolkit.getScreenSize().width;
    }

    public static int getScreenHeight() {
        return toolkit.getScreenSize().height;
    }

    public static int add(Integer[] x) {
        int o = 0;
        for (Integer i : x) {
            if (i != null) {
                o += i;
            }
        }
        return o;
    }

    public static void setDoubleFilter(JTextComponent c) {
        ((AbstractDocument) c.getDocument()).setDocumentFilter(new DocumentFilter() {
            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
                    throws BadLocationException {
                if (!(fb.getDocument().getText(0, fb.getDocument().getLength()).contains(".") && text.matches(
                        "[,.]"))) {
                    fb.replace(offset, length, text.replaceAll("[\\D&&[^,.]]", "").replaceAll(",", "."), attrs);
                }
            }
        });
    }

    public static void setRealNumberFilter(JTextComponent c) {
        ((AbstractDocument) c.getDocument()).setDocumentFilter(new DocumentFilter() {
            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
                    throws BadLocationException {
                if (!(fb.getDocument().getText(0, fb.getDocument().getLength()).contains(".") && text.matches(
                        "[,.]"))) {
                    fb.replace(offset, length, text.replaceAll("[^\\d]", ""), attrs);
                }
            }
        });
    }


    public static <T, O extends Collection<T>> O extract(Supplier<O> supplier, String s, String separator,
                                                         Function<String,T> method) {
        String[] columns = s.split(separator);
        O out = supplier.get();
        for (String column : columns) {
            out.add(method.apply(column));
        }
        return out;
    }

    public static <T> T[] extract(Class<T> c, String s, String separator, Function<String,T> method) {
        return extract(ArrayList::new, s, separator, method).toArray((T[]) Array.newInstance(c, 0));
    }

    public static <I, O> O[] transform(I[] in, Class<O> out, Function<I,O> transformer) {
        O[] output = (O[]) Array.newInstance(out, in.length);
        for (int i = 0; i < in.length; i++) {
            output[i] = transformer.apply(in[i]);
        }
        return output;
    }

    public static <I, O> List<O> transform(Collection<I> in, Function<I,O> transformer) {
        List<O> output = new ArrayList<>(in.size());
        for (I i : in) {
            output.add(transformer.apply(i));
        }
        return output;
    }

    public static <T> Function<String,T> findParser(Class<T> c) {
        if (c.equals(Boolean.class) || c.equals(boolean.class)) {
            return e -> e.equals("null") ? null : c.cast(Boolean.parseBoolean(e));
        } else if (c.equals(Integer.class) || c.equals(int.class)) {
            return e -> e.equals("null") ? null : c.cast(Integer.parseInt(e));
        } else if (c.equals(Float.class) || c.equals(float.class)) {
            return e -> e.equals("null") ? null : c.cast(Float.parseFloat(e));
        } else if (c.equals(Double.class) || c.equals(double.class)) {
            return e -> e.equals("null") ? null : c.cast(Double.parseDouble(e));
        } else {
            return c::cast;
        }
    }

    public static <T> void forEach(T[] o, Consumer<T> consumer) {
        for (T object : o) {
            consumer.accept(object);
        }
    }

    public static <T> Collection<T> createCollection(Supplier<T> s, int size) {
        Collection<T> out = new ArrayList<T>();
        for (int i = 0; i < size; i++) {
            out.add(s.get());
        }
        return out;
    }

    public static <I, O> O overwrite(O out, I in) {
        Class<?> oc = out.getClass();
        for (Field declaredField : in.getClass().getDeclaredFields()) {
            try {
                Field target = oc.getDeclaredField(declaredField.getName());
                target.setAccessible(true);
                declaredField.setAccessible(true);
                target.set(out, declaredField.get(in));
            } catch (NoSuchFieldException | IllegalAccessException ignored) {
            }
        }
        return out;
    }

    public static <T> List<T> getAll(Class<T> c, String condition) {
        EntityManager em = DBConnection.getEntityManager();
        List<T> out = em.createQuery("select c from " + c.getName() + " c " + (condition != null ? condition : ""), c)
                        .getResultList();
        em.close();
        return Proxy.getSecureInstances(out);
    }

    public static <T> T mergeWithoutId(T in) {
        try {
            return mergeWithoutId(in, (T) in.getClass().getDeclaredConstructor().newInstance());
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            Tools.showUnexpectedErrorWarning(e);
            return null;
        }
    }



    public static <T> T setId(T t,long id){
        for (Field field : t.getClass().getDeclaredFields()) {
            if(field.getAnnotation(Id.class)!=null){
                field.setAccessible(true);
                try {
                    if (field.getType().equals(Integer.TYPE) || field.getType().equals(int.class))
                        field.set(t, (int) id);
                    else
                        field.set(t, id);
                } catch (IllegalAccessException e) {
                    Tools.showUnexpectedErrorWarning(e);
                }
            }
        }
        return t;
    }

    public static <T> T mergeWithoutId(T in, T toOverride) {
        try {
            long before = getId(in);
            BeanUtils.copyProperties(toOverride,in);
            setId(in,before);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return toOverride;
    }

    private static <T> long getId(T in) {
        for (Field declaredField : in.getClass().getDeclaredFields()) {
            if(declaredField.getAnnotation(Id.class)!=null){
                declaredField.setAccessible(true);
                try {
                    return ((Number) declaredField.get(in)).longValue();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        return Long.MIN_VALUE;
    }

    public static long tryParseLong(String s) {
        try {
            return Long.parseLong(s);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public static int tryParseInteger(String s) {
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return Integer.MIN_VALUE;
        }
    }

    public static <T> void delete(Class<T> t, Object key) {
        runInSession(em -> em.remove(em.find(t, key)));
    }

    public static <T> void edit(Object key, T to) {
        runInSession(em -> em.persist(Tools.mergeWithoutId(to, em.find(to.getClass(), key))));
    }

    public static void runInSession(Consumer<EntityManager> dbAction) {
        EntityManager em = DBConnection.getEntityManager();
        EntityTransaction et = em.getTransaction();
        et.begin();
        dbAction.accept(em);
        em.flush();
        et.commit();
        em.close();
    }

    public static <O, V> void addToCollection(Class<O> c, Object key, Function<O,Collection<V>> collectionSupplier, V value){
        EntityManager em = DBConnection.getEntityManager();
        EntityTransaction et = em.getTransaction();
        et.begin();
        O db = em.find(c, key);
        collectionSupplier.apply(db).add(value);
        em.persist(db);
        em.flush();
        et.commit();
        em.close();
    }
    public static <O, V> void addMultipleToCollection(Class<O> c, Object key, Function<O,Collection<V>> collectionSupplier, Collection<V> value){
        EntityManager em = DBConnection.getEntityManager();
        EntityTransaction et = em.getTransaction();
        et.begin();
        O db = em.find(c, key);
        collectionSupplier.apply(db).addAll(value);
        em.persist(db);
        em.flush();
        et.commit();
        em.close();
    }
    public static <O, V> void removeMultipleFromCollection(Class<O> c, Object key, Function<O,Collection<V>> collectionSupplier, Collection<V> value){
        EntityManager em = DBConnection.getEntityManager();
        EntityTransaction et = em.getTransaction();
        et.begin();
        O db = em.find(c, key);
        collectionSupplier.apply(db).removeAll(value);
        em.persist(db);
        em.flush();
        et.commit();
        em.close();
    }
    public static <O, V> void removeFromCollection(Class<O> c, Object key, Function<O,Collection<V>> collectionSupplier, V value){
        EntityManager em = DBConnection.getEntityManager();
        EntityTransaction et = em.getTransaction();
        et.begin();
        O db = em.find(c, key);
        collectionSupplier.apply(db).remove(value);
        em.persist(db);
        em.flush();
        et.commit();
        em.close();
    }

    public static void showUnexpectedErrorWarning(Exception e){
        Main.logger.error(e.getMessage(),e);
        JOptionPane.showMessageDialog(null,"Ein Unerwarteter Fehler ist aufgetreten, bitte melden\nsie den Fehler beim Entwiklerteam oder auf\nGithub: https://github.com/julikiller98/Kernbeisser-Gradle/\nFehler:\n"+e.toString(),"Es ist ein unerwarteter Fehler aufgetreten",JOptionPane.ERROR_MESSAGE);
    }

    public static <T> T removeLambda(T from,Supplier<T> original){
        T out = original.get();
        copyInto(from,out);
        return out;
    }


    public static <T> void persist(T value){
        EntityManager em = DBConnection.getEntityManager();
        EntityTransaction et = em.getTransaction();
        et.begin();
        em.persist(value);
        em.flush();
        et.commit();
        em.close();
    }

    public static int error = 0;

    public static void showHint(JComponent component){
        if(!component.isEnabled())return;
        Color originalColor = component.getForeground();
        Color originalBackgroundColor = component.getBackground();
        component.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                e.getComponent().setForeground(originalColor);
                e.getComponent().setBackground(originalBackgroundColor);
                e.getComponent().removeFocusListener(this);
            }
        });
        if(((JTextComponent)component).getText().replace(" ","").equals(""))
            component.setBackground(new Color(0xFF9999));
        else component.setForeground(new Color(0xFF00000));

    }

    public static boolean verify(JComponent ... components){
        boolean result = true;
        for (JComponent component : components) {
            if(component instanceof JTextComponent) {
                if (component.getInputVerifier() != null && !component.getInputVerifier().verify(component)) {
                    result = false;
                    component.getInputVerifier().shouldYieldFocus(component);
                }
            }
        }
        return result;
    }

    public static void copyInto(Object source,Object destination){
        Class<?> clazz = source.getClass();
        boolean isProxy = Proxy.isProxyInstance(source);
        while (!clazz.equals(Object.class)) {
            for (Field field : clazz.getDeclaredFields()) {
                if(Modifier.isStatic(field.getModifiers())) continue;
                if(isProxy && field.getName().equals("handler"))continue;
                field.setAccessible(true);
                try {
                    field.set(destination, field.get(source));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
            clazz = clazz.getSuperclass();
        }
    }


    private static final Unsafe unsafe = createUnsafe();

    private static Unsafe createUnsafe() {
        Field f;
        try {
            f = Unsafe.class.getDeclaredField("theUnsafe");
            f.setAccessible(true);
            return (Unsafe) f.get(null);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
    }


    //I don't know if we should use Java Unsafe like the name already says ...
    public static <T> T clone(T object) {
        Class<?> clazz = object.getClass();
        T instance = null;
        try {
            instance = (T) unsafe.allocateInstance(clazz);
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
        while (!clazz.equals(Object.class)) {
            for (Field field : clazz.getDeclaredFields()) {
                if(Modifier.isStatic(field.getModifiers()))continue;
                if(Modifier.isFinal(field.getModifiers()))continue;
                field.setAccessible(true);
                try {
                    field.set(instance, field.get(object));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
            clazz = clazz.getSuperclass();
        }
        return instance;
    }

    public static <T> T createWithoutConstructor(Class<T> clazz){
        try {
            return (T) unsafe.allocateInstance(clazz);
        } catch (InstantiationException e) {
            Tools.showUnexpectedErrorWarning(e);
            return null;
        }
    }

    public static void invokeWithDefault(AccessConsumer<Object> consumer) throws AccessDeniedException {
        Object[] primitiveObjects = new Object[]{
                null,
                (int)0,
                (long)0,
                (double)0,
                (float)0,
                (char)0,
                (byte)0,
                (short)0,
                false,
                };
        for (Object primitiveObject : primitiveObjects) {
            try {
                consumer.accept(primitiveObject);
                return;
            }catch (NullPointerException | ClassCastException ignored ){}
        }
    }

    public static StackTraceElement getCallerStackTraceElement(int above){
        return Thread.currentThread().getStackTrace()[2+above];
    }

    public static void activateKeyboardListener() {
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(new KeyEventDispatcher() {
            @Override
            public boolean dispatchKeyEvent(KeyEvent e) {
                ViewMainPanel viewMainPanel = (ViewMainPanel) SwingUtilities.getAncestorOfClass(ViewMainPanel.class, e.getComponent());
                if (viewMainPanel != null) {
                    return viewMainPanel.getView().processKeyboardInput(e);
                } else {
                    return false;
                }
            }
        });
    }

    public static void deactivateKeyboardListener() {
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(new KeyEventDispatcher() {
            @Override
            public boolean dispatchKeyEvent(KeyEvent e) {
                return false;
            }
        });
    }

    public static <T> T decide(AccessSupplier<T> supplier, T t){
        try {
            return supplier.get();
        } catch (AccessDeniedException e) {
            return t;
        }
    }

    public static <T> void tryIt(AccessConsumer<T> consumer, T t){
        try {
            consumer.accept(t);
        } catch (AccessDeniedException e) {
            e.printStackTrace();
        }
    }

    public static <T> T invokeConstructor(Class<T> out){
        try {
            Constructor<T> constructor = out.getDeclaredConstructor(new Class[0]);
            constructor.setAccessible(true);
            return constructor.newInstance();
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            return Tools.createWithoutConstructor(out);
        }
    }
}
