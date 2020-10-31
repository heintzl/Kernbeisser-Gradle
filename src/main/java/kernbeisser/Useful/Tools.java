package kernbeisser.Useful;

import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.*;
import java.util.Date;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import javax.persistence.*;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;
import javax.swing.*;
import javax.swing.text.*;
import kernbeisser.DBConnection.DBConnection;
import kernbeisser.Enums.Setting;
import kernbeisser.Enums.UserSetting;
import kernbeisser.Exeptions.PermissionKeyRequiredException;
import kernbeisser.Main;
import kernbeisser.Security.AccessConsumer;
import kernbeisser.Security.AccessSupplier;
import kernbeisser.Security.Proxy;
import kernbeisser.Windows.LogIn.LogInModel;
import lombok.Cleanup;
import lombok.SneakyThrows;
import org.apache.commons.beanutils.BeanUtils;
import sun.misc.Unsafe;

public class Tools {
  public static <A extends Annotation> Collection<Field> getWithAnnotation(
      Class<?> pattern, Class<A> annotation) {
    ArrayList<Field> out = new ArrayList<>();
    for (Field field : Tools.getAllFields(pattern)) {
      field.setAccessible(true);
      if (field.isAnnotationPresent(annotation)) {
        out.add(field);
      }
    }
    return out;
  }

  public static <R, T> R build(List<T> in, R r, BiFunction<R, T, R> builder) {
    for (T t : in) {
      r = builder.apply(r, t);
    }
    return r;
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

  public static <T, O extends Collection<T>> O extract(
      Supplier<O> outputContainerSupplier,
      String sourceString,
      String separator,
      Function<String, T> stringTransformer) {
    String[] columns = sourceString.split(separator);
    O out = outputContainerSupplier.get();
    for (String column : columns) {
      out.add(stringTransformer.apply(column));
    }
    return out;
  }

  public static <T> T[] extract(
      Class<T> outputClass,
      String arrayString,
      String separator,
      Function<String, T> stringValueExtractor) {
    return extract(ArrayList::new, arrayString, separator, stringValueExtractor)
        .toArray((T[]) Array.newInstance(outputClass, 0));
  }

  public static <I, O> O[] transform(I[] in, Class<O> out, Function<I, O> ioTransformer) {
    O[] output = (O[]) Array.newInstance(out, in.length);
    for (int i = 0; i < in.length; i++) {
      output[i] = ioTransformer.apply(in[i]);
    }
    return output;
  }

  public static <I, O> List<O> transform(Collection<I> in, Function<I, O> transformer) {
    List<O> output = new ArrayList<>(in.size());
    for (I i : in) {
      output.add(transformer.apply(i));
    }
    return output;
  }

  public static <T> Function<String, T> findParser(Class<T> targetClass) {
    if (targetClass.equals(Boolean.class) || targetClass.equals(boolean.class)) {
      return e -> e.equals("null") ? null : targetClass.cast(Boolean.parseBoolean(e));
    } else if (targetClass.equals(Integer.class) || targetClass.equals(int.class)) {
      return e -> e.equals("null") ? null : targetClass.cast(Integer.parseInt(e));
    } else if (targetClass.equals(Float.class) || targetClass.equals(float.class)) {
      return e -> e.equals("null") ? null : targetClass.cast(Float.parseFloat(e));
    } else if (targetClass.equals(Double.class) || targetClass.equals(double.class)) {
      return e -> e.equals("null") ? null : targetClass.cast(Double.parseDouble(e));
    } else {
      return targetClass::cast;
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

  public static <T> List<T> getAll(Class<T> c, String condition) {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    List<T> out =
        em.createQuery(
                "select c from " + c.getName() + " c " + (condition != null ? condition : ""), c)
            .getResultList();
    em.close();
    return Proxy.getSecureInstances(out);
  }

  public static <T> List<T> getAllUnProxy(Class<T> c) {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    CriteriaBuilder cb = em.getCriteriaBuilder();
    CriteriaQuery<T> cq = cb.createQuery(c);
    Root<T> rootEntry = cq.from(c);
    CriteriaQuery<T> all = cq.select(rootEntry);
    TypedQuery<T> allQuery = em.createQuery(all);
    return allQuery.getResultList();
  }

  public static <T> T mergeWithoutId(T in) {
    try {
      return mergeWithoutId(in, (T) in.getClass().getDeclaredConstructor().newInstance());
    } catch (InstantiationException
        | IllegalAccessException
        | InvocationTargetException
        | NoSuchMethodException e) {
      Tools.showUnexpectedErrorWarning(e);
      return null;
    }
  }

  public static <T> T setId(T t, Object id) {
    Class<?> clazz = t.getClass();
    while (!clazz.equals(Object.class)) {
      for (Field declaredField : clazz.getDeclaredFields()) {
        if (declaredField.isAnnotationPresent(Id.class)) {
          declaredField.setAccessible(true);
          try {
            declaredField.set(t, id);
            return t;
          } catch (IllegalAccessException e) {
            e.printStackTrace();
          }
        }
      }
      clazz = clazz.getSuperclass();
    }
    return t;
  }

  public static <T> T mergeWithoutId(T in, T toOverride) {
    try {
      Object before = getId(in);
      BeanUtils.copyProperties(toOverride, in);
      setId(in, before);
    } catch (IllegalAccessException | InvocationTargetException e) {
      e.printStackTrace();
    }
    return toOverride;
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

  public static <T> void delete(Object o) {
    if (o != null) delete(o.getClass(), getId(o));
  }

  public static <T> void delete(Class<T> t, Object key) {
    runInSession(em -> em.remove(em.find(t, key)));
  }

  public static <T> void edit(Object key, T to) {
    runInSession(em -> em.persist(Tools.mergeWithoutId(to, em.find(to.getClass(), key))));
  }

  public static void add(Object o) {
    Tools.setId(o, Tools.getId(Tools.createWithoutConstructor(o.getClass())));
    persist(o);
  }

  public static void runInSession(Consumer<EntityManager> dbAction) {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    EntityTransaction et = em.getTransaction();
    et.begin();
    dbAction.accept(em);
    em.flush();
    et.commit();
    em.close();
  }

  public static <O, V> void addToCollection(
      Class<O> c, Object key, Function<O, Collection<V>> collectionSupplier, V value) {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    EntityTransaction et = em.getTransaction();
    et.begin();
    O db = em.find(c, key);
    collectionSupplier.apply(db).add(value);
    em.persist(db);
    em.flush();
    et.commit();
    em.close();
  }

  public static <O, V> void addMultipleToCollection(
      Class<O> c, Object key, Function<O, Collection<V>> collectionSupplier, Collection<V> value) {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    EntityTransaction et = em.getTransaction();
    et.begin();
    O db = em.find(c, key);
    collectionSupplier.apply(db).addAll(value);
    em.persist(db);
    em.flush();
    et.commit();
    em.close();
  }

  public static <O, V> void removeMultipleFromCollection(
      Class<O> c, Object key, Function<O, Collection<V>> collectionSupplier, Collection<V> value) {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    EntityTransaction et = em.getTransaction();
    et.begin();
    O db = em.find(c, key);
    collectionSupplier.apply(db).removeAll(value);
    em.persist(db);
    em.flush();
    et.commit();
    em.close();
  }

  public static <O, V> void removeFromCollection(
      Class<O> c, Object key, Function<O, Collection<V>> collectionSupplier, V value) {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    EntityTransaction et = em.getTransaction();
    et.begin();
    O db = em.find(c, key);
    collectionSupplier.apply(db).remove(value);
    em.persist(db);
    em.flush();
    et.commit();
    em.close();
  }

  public static void showUnexpectedErrorWarning(Exception e) {
    Main.logger.error(e.getMessage(), e);
    JOptionPane.showMessageDialog(
        null,
        "Ein Unerwarteter Fehler ist aufgetreten, bitte melden\nsie den Fehler beim Entwiklerteam oder auf\nGithub: https://github.com/julikiller98/Kernbeisser-Gradle/\nFehler:\n"
            + e.toString(),
        "Es ist ein unerwarteter Fehler aufgetreten",
        JOptionPane.ERROR_MESSAGE);
  }

  public static void showPrintAbortedWarning(Exception e, boolean logEvent) {
    if (logEvent) {
      Main.logger.error(e.getMessage(), e);
    }
    JOptionPane.showMessageDialog(
        null, "Der Ausdruck wurde abgebrochen!", "Drucken", JOptionPane.WARNING_MESSAGE);
  }

  public static <T> T removeLambda(T from, Supplier<T> original) {
    T out = original.get();
    copyInto(from, out);
    return out;
  }

  public static <T> void persist(T value) {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    EntityTransaction et = em.getTransaction();
    et.begin();
    em.persist(value);
    em.flush();
    et.commit();
    em.close();
  }

  public static int error = 0;

  public static void showHint(JComponent component) {
    if (!component.isEnabled()) {
      return;
    }
    Color originalColor = component.getForeground();
    Color originalBackgroundColor = component.getBackground();
    component.addFocusListener(
        new FocusAdapter() {
          @Override
          public void focusGained(FocusEvent e) {
            e.getComponent().setForeground(originalColor);
            e.getComponent().setBackground(originalBackgroundColor);
            e.getComponent().removeFocusListener(this);
          }
        });

    if (component instanceof JTextComponent
        && !((JTextComponent) component).getText().replace(" ", "").equals("")) {
      component.setForeground(new Color(0xFF00000));
    } else {
      component.setBackground(new Color(0xFF9999));
    }
  }

  public static void copyInto(Object source, Object destination) {
    Class<?> clazz = source.getClass();
    boolean isProxy = Proxy.isProxyInstance(source);
    while (!clazz.equals(Object.class)) {
      for (Field field : clazz.getDeclaredFields()) {
        if (Modifier.isStatic(field.getModifiers())) {
          continue;
        }
        if (isProxy && field.getName().equals("handler")) {
          continue;
        }
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

  // I don't know if we should use Java Unsafe like the name already says ...
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
        if (Modifier.isStatic(field.getModifiers())) {
          continue;
        }
        if (Modifier.isFinal(field.getModifiers())) {
          continue;
        }
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

  public static <T> T createWithoutConstructor(Class<T> clazz) {
    if (clazz == null) throw new NullPointerException("cannot create instance from null");
    try {
      return (T) unsafe.allocateInstance(clazz);
    } catch (InstantiationException e) {
      throw new UnsupportedOperationException("cannot create instance without constructor :(");
    }
  }

  public static void invokeWithDefault(AccessConsumer<Object> consumer) {
    Object[] primitiveObjects =
        new Object[] {
          null, 0, (long) 0, (double) 0, (float) 0, (char) 0, (byte) 0, (short) 0, false,
        };
    for (Object primitiveObject : primitiveObjects) {
      try {
        consumer.accept(primitiveObject);
        return;
      } catch (NullPointerException | ClassCastException ignored) {
      }
    }
  }

  public static StackTraceElement getCallerStackTraceElement(int above) {
    return Thread.currentThread().getStackTrace()[2 + above];
  }

  public static <T> T decide(AccessSupplier<T> supplier, T t) {
    try {
      return supplier.get();
    } catch (PermissionKeyRequiredException e) {
      return t;
    }
  }

  public static <T> void tryIt(AccessConsumer<T> consumer, T t) {
    try {
      consumer.accept(t);
    } catch (PermissionKeyRequiredException e) {
      e.printStackTrace();
    }
  }

  public static <T> T invokeConstructor(Class<T> out) {
    try {
      Constructor<T> constructor = out.getDeclaredConstructor();
      constructor.setAccessible(true);
      return constructor.newInstance();
    } catch (NoSuchMethodException
        | InstantiationException
        | IllegalAccessException
        | InvocationTargetException e) {
      return Tools.createWithoutConstructor(out);
    }
  }

  public static Collection<Field> getAllUniqueFields(Class<?> clazz) {
    Collection<Field> uniqueFields = new HashSet<>();
    for (Field field : clazz.getDeclaredFields()) {
      if (isUnique(field)) {
        field.setAccessible(true);
        uniqueFields.add(field);
      }
    }
    return uniqueFields;
  }

  public static <T> void persistIfNotUnique(Class<T> clazz, Collection<T> collection) {
    Field[] uniqueFieldsArray = getAllUniqueFields(clazz).toArray(new Field[0]);
    Object[][] uniqueValues = getAllValues(clazz, uniqueFieldsArray);
    HashSet<?>[] hashSets = new HashSet<?>[uniqueFieldsArray.length];
    for (int i = 0; i < hashSets.length; i++) {
      hashSets[i] = new HashSet<>(Arrays.asList(uniqueValues[i]));
    }
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    EntityTransaction et = em.getTransaction();
    et.begin();
    collection.forEach(
        (value) -> {
          boolean passed = true;
          for (int i = 0; i < uniqueFieldsArray.length; i++) {
            try {
              if (hashSets[i].contains(uniqueFieldsArray[i].get(value))) {
                Main.logger.info(
                    "Ignored "
                        + clazz
                        + " because unique field "
                        + uniqueFieldsArray[i]
                        + " contained the value "
                        + uniqueFieldsArray[i].get(value)
                        + " which is already taken");
                passed = false;
                break;
              }
            } catch (IllegalAccessException e) {
              e.printStackTrace();
            }
          }
          if (passed) {
            em.persist(value);
          }
        });
    et.commit();
    em.close();
    System.out.println("persistence finished");
  }

  public static boolean isUnique(Field f) {
    if (f.isAnnotationPresent(Id.class)) return true;
    for (Annotation annotation : f.getAnnotations()) {
      try {
        Method uniqueCheck = annotation.annotationType().getDeclaredMethod("unique");
        uniqueCheck.setAccessible(true);
        return (boolean) uniqueCheck.invoke(annotation);
      } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException ignored) {
      }
    }
    return false;
  }

  public static <P> Object[][] getAllValues(Class<P> parent, Field... fields) {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    CriteriaBuilder cb = em.getCriteriaBuilder();
    CriteriaQuery<Object> query = cb.createQuery(Object.class);
    Root<P> root = query.from(parent);
    query.multiselect(Tools.transform(fields, Selection.class, field -> root.get(field.getName())));
    List<Object> queryReturn = em.createQuery(query).getResultList();
    Object[][] out = new Object[fields.length][queryReturn.size()];
    int index = 0;
    for (Object objects : queryReturn) {
      for (int field = 0; field < fields.length; field++) {
        out[field][index] = ((Object[]) objects)[field];
      }
    }
    em.close();
    return out;
  }

  public static Field getIdField(Class<?> clazz) {
    while (!clazz.equals(Object.class)) {
      for (Field declaredField : clazz.getDeclaredFields()) {
        if (declaredField.isAnnotationPresent(Id.class)) {
          declaredField.setAccessible(true);
          return declaredField;
        }
      }
      clazz = clazz.getSuperclass();
    }
    return null;
  }

  public static <I, O> O[] transformToArray(
      Collection<I> input, Class<O> outClass, Function<I, O> transformer) {
    O[] out = (O[]) Array.newInstance(outClass, input.size());
    int c = 0;
    for (I i : input) {
      out[c++] = transformer.apply(i);
    }
    return out;
  }

  public static Object getId(Object o) {
    try {
      return getIdField(o.getClass()).get(o);
    } catch (IllegalAccessException e) {
      e.printStackTrace();
      return null;
    }
  }

  public static Date toDate(YearMonth yearMonth) {
    return java.util.Date.from(yearMonth.atDay(1).atStartOfDay(ZoneId.systemDefault()).toInstant());
  }

  public static <T> T findById(Iterable<T> collection, Object id) {
    Iterator<T> iterator = collection.iterator();
    if (!iterator.hasNext()) return null;
    Field field = getIdField(collection.iterator().next().getClass());
    while (iterator.hasNext()) {
      try {
        T t = iterator.next();
        if (field.get(t).equals(id)) return t;
      } catch (IllegalAccessException e) {
        e.printStackTrace();
      }
    }
    return null;
  }

  public static void scaleLabelSize(float scaleFactor) {
    Enumeration<Object> keys = UIManager.getDefaults().keys();
    while (keys.hasMoreElements()) {
      Object key = keys.nextElement();
      Font before = UIManager.getFont(key);
      if (key.toString().endsWith(".font")) {
        UIManager.put(
            key,
            new Font(
                before.getName(), before.getStyle(), Math.round(before.getSize() * scaleFactor)));
      }
    }
    // maybe work not for all LAFs
    UIManager.put("Table.rowHeight", (int) ((int) UIManager.get("Table.rowHeight") * scaleFactor));
    System.out.println(UIManager.get("Table.font"));
  }

  public static Field findInAllSuperClasses(Class<?> clazz, String name)
      throws NoSuchFieldException {
    while (!clazz.equals(Object.class)) {
      try {
        return clazz.getDeclaredField(name);
      } catch (NoSuchFieldException ignored) {
      }
      clazz = clazz.getSuperclass();
    }
    throw new NoSuchFieldException("cannot find Field " + name);
  }

  public static Collection<Field> getAllFields(Class<?> clazz) {
    ArrayList<Field> out = new ArrayList<Field>(Arrays.asList(clazz.getDeclaredFields()));
    clazz = clazz.getSuperclass();
    while (clazz != null && !clazz.equals(Object.class)) {
      out.addAll(Arrays.asList(clazz.getDeclaredFields()));
      clazz = clazz.getSuperclass();
    }
    return out;
  }

  public static <T> Collection<T> collect(Object target, Class<T> clazz) {
    ArrayList<T> values = new ArrayList<>();
    for (Field declaredField : target.getClass().getDeclaredFields()) {
      if (clazz.isAssignableFrom(declaredField.getType())) {
        declaredField.setAccessible(true);
        try {
          values.add((T) declaredField.get(target));
        } catch (IllegalAccessException e) {
          Tools.showUnexpectedErrorWarning(e);
        }
      }
    }
    return values;
  }

  @SneakyThrows
  public static Object callPrivateFunction(
      Class<?> clazz, Object obj, String name, Object... args) {
    Method method =
        clazz.getDeclaredMethod(name, Tools.transform(args, Class.class, Object::getClass));
    method.setAccessible(true);
    return method.invoke(obj, args);
  }

  public static <T> void forEach(Enumeration<T> enumeration, Consumer<T> consumer) {
    while (enumeration.hasMoreElements()) {
      consumer.accept(enumeration.nextElement());
    }
  }

  public static void scaleFont(Component label, double fac) {
    label.setFont(label.getFont().deriveFont(Math.round(label.getFont().getSize() * fac)));
  }

  public static void scaleFonts(double fac, Component... labels) {
    for (Component label : labels) {
      scaleFont(label, fac);
    }
  }

  public static int scaleWithLabelScalingFactor(int value) {
    try {
      return (int) (value * Float.parseFloat(Setting.LABEL_SCALE_FACTOR.getStringValue()));
    } catch (NumberFormatException e) {
      return value;
    }
  }

  public static void openFile(File file) {
    try {
      Desktop desktop = Desktop.getDesktop();
      desktop.open(file);
    } catch (IOException e) {
      Tools.showUnexpectedErrorWarning(e);
    }
  }

  public static void beep() {
    if (LogInModel.getLoggedIn() == null
        || UserSetting.CREATE_BEEP_SOUND.getBooleanValue(LogInModel.getLoggedIn()))
      Toolkit.getDefaultToolkit().beep();
  }

  public static void openFile(String filePath) {
    openFile(new File(filePath));
  }
}
