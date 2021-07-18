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
import java.util.function.*;
import java.util.stream.Collectors;
import javax.persistence.*;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;
import javax.swing.*;
import javax.swing.text.JTextComponent;
import kernbeisser.DBConnection.DBConnection;
import kernbeisser.DBEntities.Article;
import kernbeisser.DBEntities.SurchargeGroup;
import kernbeisser.DBEntities.User;
import kernbeisser.Enums.PermissionConstants;
import kernbeisser.Enums.Setting;
import kernbeisser.Enums.UserSetting;
import kernbeisser.Exeptions.PermissionKeyRequiredException;
import kernbeisser.Main;
import kernbeisser.Security.SecuredOptional;
import kernbeisser.Security.Utils.*;
import kernbeisser.Windows.LogIn.LogInModel;
import kernbeisser.Windows.MVC.Controller;
import kernbeisser.Windows.ViewContainers.SubWindow;
import lombok.Cleanup;
import lombok.SneakyThrows;
import lombok.var;
import org.hibernate.envers.AuditReaderFactory;
import org.jetbrains.annotations.NotNull;
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
    @Cleanup(value = "commit")
    EntityTransaction et = em.getTransaction();
    et.begin();
    return em.createQuery(
            "select c from " + c.getName() + " c " + (condition != null ? condition : ""), c)
        .getResultList();
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

  public static int tryParseInt(String s) {
    try {
      return Integer.parseInt(s);
    } catch (NumberFormatException e) {
      return Integer.MIN_VALUE;
    }
  }

  public static long tryParseLong(String s) {
    try {
      return Long.parseLong(s);
    } catch (NumberFormatException e) {
      return Long.MIN_VALUE;
    }
  }

  public static <T> void delete(Object o) {
    if (o != null) delete(o.getClass(), getId(o));
  }

  public static <T> void delete(Class<T> t, Object key) {
    runInSession(em -> em.remove(em.find(t, key)));
  }

  public static <T> void edit(Object key, T to) {
    runInSession(
        em -> {
          Object db = em.find(to.getClass(), key);
          Tools.copyInto(to, db);
          em.persist(db);
        });
  }

  public static void resetId(Object o) {
    Tools.setId(o, Tools.getId(Tools.createWithoutConstructor(o.getClass())));
  }

  public static <T, V> Map<V, Collection<T>> group(Iterator<T> collection, Getter<T, V> getter) {
    HashMap<V, Collection<T>> collectionHashMap = new HashMap<>();
    collection.forEachRemaining(
        e -> collectionHashMap.computeIfAbsent(getter.get(e), k -> new ArrayList<>()).add(e));
    return collectionHashMap;
  }

  public static <T, V> void fillUniqueFieldWithNextAvailable(
      Collection<T> collection, Getter<T, V> getter, Setter<T, V> setter, UnaryOperator<V> next) {
    Set<V> values = new HashSet<>(collection.size());
    Collection<T> task = new ArrayList<>();
    for (T t : collection) {
      if (!values.add(getter.get(t))) task.add(t);
    }
    for (T t : task) {
      V value = next.apply(getter.get(t));
      while (values.contains(value)) {
        value = next.apply(value);
      }
      if (!values.add(value)) System.out.println("HOW?");
      setter.set(t, value);
    }
  }

  public static <T, V> Set<V> createSet(Collection<T> collection, Getter<T, V> tvGetter) {
    HashSet<V> out = new HashSet<>(collection.size());
    collection.forEach(e -> out.add(tvGetter.get(e)));
    return out;
  }

  public static void add(Object o) {
    resetId(o);
    persist(o);
  }

  public static void runInSession(Consumer<EntityManager> dbAction) {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    @Cleanup(value = "commit")
    EntityTransaction et = em.getTransaction();
    et.begin();
    dbAction.accept(em);
    em.flush();
  }

  public static <O, V> void addToCollection(
      Class<O> c, Object key, Function<O, Collection<V>> collectionSupplier, V value) {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    @Cleanup(value = "commit")
    EntityTransaction et = em.getTransaction();
    et.begin();
    O db = em.find(c, key);
    collectionSupplier.apply(db).add(value);
    em.persist(db);
    em.flush();
  }

  public static <O, V> void addMultipleToCollection(
      Class<O> c, Object key, Function<O, Collection<V>> collectionSupplier, Collection<V> value) {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    @Cleanup(value = "commit")
    EntityTransaction et = em.getTransaction();
    et.begin();
    O db = em.find(c, key);
    collectionSupplier.apply(db).addAll(value);
    em.persist(db);
    em.flush();
  }

  public static <O, V> void removeMultipleFromCollection(
      Class<O> c, Object key, Function<O, Collection<V>> collectionSupplier, Collection<V> value) {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    @Cleanup(value = "commit")
    EntityTransaction et = em.getTransaction();
    et.begin();
    O db = em.find(c, key);
    collectionSupplier.apply(db).removeAll(value);
    em.persist(db);
    em.flush();
  }

  public static <O, V> void removeFromCollection(
      Class<O> c, Object key, Function<O, Collection<V>> collectionSupplier, V value) {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    @Cleanup(value = "commit")
    EntityTransaction et = em.getTransaction();
    et.begin();
    O db = em.find(c, key);
    collectionSupplier.apply(db).remove(value);
    em.persist(db);
    em.flush();
  }

  public static void showUnexpectedErrorWarning(Throwable e) throws RuntimeException {
    Main.logger.error(e.getMessage(), e);
    JOptionPane.showMessageDialog(
        null,
        "Ein unerwarteter Fehler ist aufgetreten.\n"
            + "Bitte melde den Fehler beim Entwicklerteam\n"
            + "oder auf Github:\n"
            + "https://github.com/julikiller98/Kernbeisser-Gradle/\n"
            + "Fehler:\n"
            + e.toString(),
        "Es ist ein unerwarteter Fehler aufgetreten",
        JOptionPane.ERROR_MESSAGE);
    throw new RuntimeException(e);
  }

  public static void showPrintAbortedWarning(Exception e, boolean logEvent) {
    if (logEvent) {
      Main.logger.error(e.getMessage(), e);
    }
    JOptionPane.showMessageDialog(
        null, "Der Ausdruck wurde abgebrochen!", "Drucken", JOptionPane.WARNING_MESSAGE);
  }

  public static <T> void persist(T value) {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    @Cleanup(value = "commit")
    EntityTransaction et = em.getTransaction();
    et.begin();
    em.persist(value);
    em.flush();
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

  private static final Field modField;

  static {
    try {
      modField = Field.class.getDeclaredField("modifiers");
      modField.setAccessible(true);
    } catch (NoSuchFieldException e) {
      Tools.showUnexpectedErrorWarning(e);
      throw new RuntimeException(e);
    }
  }

  public static Field makeFinalFieldAccessible(Field field) {
    try {
      modField.set(field, modField.getModifiers() & ~Modifier.FINAL);
    } catch (IllegalAccessException e) {
      Tools.showUnexpectedErrorWarning(e);
    }
    field.setAccessible(true);
    return field;
  }

  public static void copyInto(@NotNull Object source, @NotNull Object destination) {
    Class<?> clazz = source.getClass();
    while (!clazz.equals(Object.class)) {
      for (Field field : clazz.getDeclaredFields()) {
        if (Modifier.isStatic(field.getModifiers())) {
          continue;
        }
        if (Modifier.isFinal(field.getModifiers())) {
          makeFinalFieldAccessible(field);
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
    assert instance != null;
    copyInto(object, instance);
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
    @Cleanup(value = "commit")
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
    @Cleanup(value = "commit")
    EntityTransaction et = em.getTransaction();
    et.begin();
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

  public static Dimension floatingSubwindowSize(Controller controller) {
    SubWindow container = (SubWindow) controller.getContainer();
    Dimension maxSize = container.getParent().getSize();
    double factor = Setting.SUBWINDOW_SIZE_FACTOR.getFloatValue();
    return new Dimension((int) (maxSize.width * factor), (int) (maxSize.height * factor));
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

  // reverses a ref map
  // apple -> fruit
  // carrot -> fruit
  // Output:
  // fruit -> (apple,carrot)
  public static <K, V> Map<K, Collection<V>> reverseReference(Map<V, K> map) {
    HashMap<K, Collection<V>> out = new HashMap<>();
    map.forEach(
        (k, v) -> {
          if (v == null) return;
          Collection<V> collection = out.computeIfAbsent(v, k1 -> new ArrayList<>());
          collection.add(k);
        });
    return out;
  }

  public static <K, V> Map<K, V> createMap(Iterator<K> v, Function<K, V> valueGenerator) {
    HashMap<K, V> map = new HashMap<>();
    v.forEachRemaining(
        e -> {
          V value = valueGenerator.apply(e);
          if (value != null) map.put(e, value);
        });
    return map;
  }

  public static <K, V> Map<K, V> createMapByValue(Iterator<V> v, Function<V, K> valueGenerator) {
    HashMap<K, V> map = new HashMap<>();
    v.forEachRemaining(
        e -> {
          K key = valueGenerator.apply(e);
          if (key != null) map.put(key, e);
        });
    return map;
  }

  public static void openFile(String filePath) {
    openFile(new File(filePath));
  }

  public static String userDefaultPath() {
    return System.getProperty("user.home") + File.separator + "Documents" + File.separator;
  }

  public static Map<SurchargeGroup, Map<Double, List<String>>> productSurchargeToGroup() {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    @Cleanup(value = "commit")
    EntityTransaction et = em.getTransaction();
    et.begin();
    Map<SurchargeGroup, Map<Double, List<String>>> result = new HashMap<>();
    Map<SurchargeGroup, List<Article>> articleMap =
        em.createQuery(
                "Select a from Article a where a.obsoleteSurcharge is not null", Article.class)
            .getResultStream()
            .collect(Collectors.groupingBy(Article::getSurchargeGroup, Collectors.toList()));
    Map<SurchargeGroup, Map<Double, Long>> surchargeMap = new HashMap<>();
    articleMap.forEach(
        (k, v) ->
            surchargeMap.put(
                k,
                v.stream()
                    .collect(
                        Collectors.groupingBy(
                            Article::getObsoleteSurcharge, Collectors.counting()))));
    surchargeMap.forEach(
        (k, v) -> {
          k.setSurcharge(
              v.entrySet().stream()
                  .max(Comparator.comparingLong(Map.Entry::getValue))
                  .get()
                  .getKey());
          em.persist(k);
          if (v.size() != 1) {
            System.out.println(
                k.toString()
                    + ": "
                    + v
                    + "\n"
                    + articleMap.get(k).stream()
                        .filter(a -> !a.getObsoleteSurcharge().equals(k.getSurcharge()))
                        .sorted(Comparator.comparingDouble(Article::getObsoleteSurcharge))
                        .map(
                            a ->
                                "  "
                                    + a.getObsoleteSurcharge()
                                    + " ->"
                                    + a.getSupplier().getShortName()
                                    + a.getSuppliersItemNumber()
                                    + ": "
                                    + a.getName())
                        .collect(Collectors.joining("\n")));
            Map<Double, List<String>> groupResult = new HashMap<>();
            v.forEach(
                (d, l) -> {
                  List<String> prodList = new ArrayList();
                  if (d != k.getSurcharge()) {
                    prodList =
                        articleMap.get(k).stream()
                            .filter(a -> a.getObsoleteSurcharge().equals(d))
                            .map(
                                a ->
                                    a.getSupplier().getShortName()
                                        + a.getSuppliersItemNumber()
                                        + ": "
                                        + a.getName())
                            .collect(Collectors.toList());
                  } else {
                    prodList.add("(" + l + " Artikel)");
                  }
                  groupResult.put(d, prodList);
                });
            result.put(k, groupResult);
          }
        });
    em.flush();
    return result;
  }

  public static int calculate(String x, String y) {
    int[][] dp = new int[x.length() + 1][y.length() + 1];

    for (int i = 0; i <= x.length(); i++) {
      for (int j = 0; j <= y.length(); j++) {
        if (i == 0) {
          dp[i][j] = j;
        } else if (j == 0) {
          dp[i][j] = i;
        } else {
          dp[i][j] =
              min(
                  dp[i - 1][j - 1] + costOfSubstitution(x.charAt(i - 1), y.charAt(j - 1)),
                  dp[i - 1][j] + 1,
                  dp[i][j - 1] + 1);
        }
      }
    }

    return dp[x.length()][y.length()];
  }

  @SafeVarargs
  public static <T> T[] asAvailable(boolean filterNull, Class<T> clazz, Supplier<T>... values) {
    T[] out = (T[]) Array.newInstance(clazz, values.length);
    int fillingIndex = 0;
    for (Supplier<T> value : values) {
      try {
        out[fillingIndex++] = filterNull ? Objects.requireNonNull(value.get()) : value.get();
      } catch (Throwable ignored) {
      }
    }
    return Arrays.copyOf(out, fillingIndex);
  }

  public static int min(int a, int b, int c) {
    return Math.min(Math.min(a, b), c);
  }

  public static int costOfSubstitution(char a, char b) {
    return a == b ? 0 : 1;
  }

  public static double roundCurrency(double value) {
    return Math.round(100. * value) / 100.;
  }

  public static void setKeyPermissions() {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    @Cleanup(value = "commit")
    EntityTransaction et = em.getTransaction();
    em.createQuery("select u from User u", User.class)
        .getResultStream()
        .filter(u -> u.getKernbeisserKey() >= 0)
        .peek(u -> u.getPermissions().add(PermissionConstants.KEY_PERMISSION.getPermission()))
        .forEach(em::persist);
    em.flush();
  }

  public static String accessString(AccessSupplier<String> supplier) {
    return optional(supplier).orElse("[Keine Leseberechtigung]");
  }

  public static <T> SecuredOptional<T> optional(AccessSupplier<T> supplier) {
    try {
      return SecuredOptional.ofNullable(supplier.get());
    } catch (PermissionKeyRequiredException e) {
      return SecuredOptional.empty();
    }
  }

  public static <T> Optional<T> optional(TypedQuery<T> supplier) {
    try {
      return Optional.of(supplier.getSingleResult());
    } catch (NoResultException e) {
      return Optional.empty();
    }
  }

  public static boolean canInvoke(AccessRunnable runnable) {
    try {
      runnable.run();
      return true;
    } catch (PermissionKeyRequiredException e) {
      return false;
    }
  }

  public static <T> T[] append(T[] source, T... v) {
    int originalSize = v.length;
    source = Arrays.copyOf(source, originalSize + v.length);
    System.arraycopy(v, 0, source, originalSize, v.length);
    return source;
  }

  public static <T> T or(AccessSupplier<T> supplier, T v) {
    return optional(supplier).orElse(v);
  }

  public static int indexOfFirstNumber(String s) {
    char[] chars = s.toCharArray();
    for (int i = 0; i < chars.length; i++) {
      if (Character.isDigit(chars[i])) return i;
    }
    return -1;
  }

  public static boolean isPartOfNumb(char c) {
    return Character.isDigit(c) || c == ',' || c == '.';
  }

  public static int countNumbers(char[] chars) {
    int counter = 0;
    boolean wasPartOfNumber = false;
    for (char c : chars) {
      boolean partOfNumber = isPartOfNumb(c);
      if (partOfNumber && !wasPartOfNumber) {
        counter++;
      }
      wasPartOfNumber = partOfNumber;
    }
    return counter;
  }

  public static double[] allNumbers(String s) {
    char[] chars = s.toCharArray();
    double[] out = new double[countNumbers(chars)];
    int numberBeginIndex = -1;
    int storeIndex = 0;
    for (int i = 0; i < chars.length; i++) {
      if (isPartOfNumb(chars[i])) {
        if (numberBeginIndex == -1) numberBeginIndex = i;
      } else {
        if (numberBeginIndex == -1) continue;
        out[storeIndex++] = Double.parseDouble(s.substring(numberBeginIndex, i).replace(",", "."));
        numberBeginIndex = -1;
      }
    }
    return out;
  }

  public static void assertPersisted(Object value) {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    @Cleanup(value = "commit")
    EntityTransaction et = em.getTransaction();
    et.begin();
    if (em.find(value.getClass(), Tools.getId(value)) == null)
      throw new IllegalArgumentException("the state of the Object is not persisted");
  }

  public static int lastIndexOfPartOfNumber(char[] chars) {
    for (int i = chars.length - 1; i >= 0; i--) {
      if (isPartOfNumb(chars[i])) return i;
    }
    return -1;
  }

  public static int getNewestRevisionNumber(Object o) {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    @Cleanup("commit")
    EntityTransaction et = em.getTransaction();
    et.begin();
    var auditReaderFactory = AuditReaderFactory.get(em);
    List<Number> revisions = auditReaderFactory.getRevisions(o.getClass(), Tools.getId(o));
    return revisions.get(revisions.size() - 1).intValue();
  }
}
