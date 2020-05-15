package kernbeisser.Useful;

import kernbeisser.DBConnection.DBConnection;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Id;
import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
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

    public static void ping(JComponent component) {
        ping(component, false);
    }

    public static void ping(JComponent component, boolean forceGround) {
        new Thread(() -> {
            Toolkit.getDefaultToolkit().beep();
            component.requestFocus();
            for (int i = forceGround ? 0 : 100; i < 256; i++) {
                try {
                    Thread.sleep(4);
                    if (forceGround) {
                        component.setForeground(new Color(255 - i, 0, 0));
                    } else {
                        component.setBackground(new Color(255, i, i));
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }).start();
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
        return out;
    }

    public static <T extends Collection> T filterNull(T in) {
        in.removeIf(Objects::isNull);
        return in;
    }

    public static <T> T mergeWithoutId(T in) {
        try {
            return mergeWithoutId(in, (T) in.getClass().getDeclaredConstructor().newInstance());
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static <T> T createNewPersistenceInstance(T t,Supplier<T> newInstancePattern){
        T newInstance = newInstancePattern.get();
        for (Field field : newInstance.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            if (Collection.class.isAssignableFrom(field.getType())) {
                try {
                    ((Collection<?>)field.get(newInstance)).addAll((Collection) field.get(t));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }else {
                try {
                    field.set(newInstance,field.get(t));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        setId(newInstance,0);
        return newInstance;
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
                    e.printStackTrace();
                }
            }
        }
        return t;
    }

    public static <T> T mergeWithoutId(T in, T toOverride) {
        for (Field field : toOverride.getClass().getDeclaredFields()) {
            if (field.getAnnotation(Id.class) == null) {
                field.setAccessible(true);
                try {
                    field.set(toOverride, field.get(in));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        return toOverride;
    }

    public static void setPromptText(JTextField jTextField, String promptText) {
        jTextField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (jTextField.getText().equals(promptText)) {
                    jTextField.setText("");
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (jTextField.getText().equals("")) {
                    jTextField.setText(promptText);
                }
            }
        });
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

    public static <T> T removeLambda(T from,Supplier<T> original){
        return Tools.overwrite(original.get(),from);
    }
}
