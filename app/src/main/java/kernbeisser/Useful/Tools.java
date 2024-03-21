package kernbeisser.Useful;

import jakarta.persistence.*;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.text.MessageFormat;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.*;
import java.util.Date;
import java.util.List;
import java.util.function.*;
import java.util.stream.Collectors;
import javax.swing.*;
import javax.swing.text.JTextComponent;
import kernbeisser.DBConnection.DBConnection;
import kernbeisser.DBConnection.PredicateFactory;
import kernbeisser.DBConnection.QueryBuilder;
import kernbeisser.DBEntities.Article;
import kernbeisser.DBEntities.SurchargeGroup;
import kernbeisser.Enums.Setting;
import kernbeisser.Enums.UserSetting;
import kernbeisser.Exeptions.handler.UnexpectedExceptionHandler;
import kernbeisser.Security.SecuredOptional;
import kernbeisser.Security.Utils.*;
import kernbeisser.Windows.LogIn.LogInModel;
import kernbeisser.Windows.MVC.Controller;
import kernbeisser.Windows.ViewContainers.SubWindow;
import lombok.Cleanup;
import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;
import rs.groump.AccessDeniedException;

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
          em.detach(to);
          setId(to, key);
          em.merge(to);
        });
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

  public static void noArticleFoundForBarcodeWarning(Component parentComponent, String barcode) {
    Tools.beep();
    JOptionPane.showMessageDialog(
        parentComponent,
        "Konnte keinen Artikel mit Barcode \"" + barcode + "\" finden",
        "Artikel nicht gefunden",
        JOptionPane.INFORMATION_MESSAGE);
  }

  public static Integer integerInputDialog(Component parentComponent, int initValue) {
    return integerInputDialog(parentComponent, initValue, i -> i > 0);
  }

  public static Integer integerInputDialog(
      Component parentComponent, int initValue, Predicate<Integer> predicate) {
    String response = inputNumber(parentComponent, initValue, false);
    do {
      if (response == null || response.equals("")) {
        return null;
      } else {
        try {
          int alteredValue = Integer.parseInt(response);
          if (predicate.test(alteredValue)) {
            return alteredValue;
          } else {
            throw (new NumberFormatException());
          }
        } catch (NumberFormatException exception) {
          response = inputNumber(parentComponent, initValue, true);
        }
      }
    } while (true);
  }

  private static String inputNumber(Component parentComponent, int initValue, boolean retry) {
    String initValueString = MessageFormat.format("{0, number, 0}", initValue).trim();
    String message = "";
    String response = "";
    if (retry) { // item is piece, first try
      message = "Die Eingabe ist ungültig. Bitte hier eine gültige Anzahl > 0 eingeben:";
    } else { // item is piece later try
      message = "Bitte neue Anzahl eingeben:";
    }
    Tools.beep();
    response =
        (String)
            JOptionPane.showInputDialog(
                parentComponent,
                message,
                "Anzahl anpassen",
                JOptionPane.QUESTION_MESSAGE,
                null,
                null,
                initValueString);
    if (response != null) {
      response = response.trim();
    }
    return response;
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

  public static StackTraceElement getCallerStackTraceElement(int above) {
    return Thread.currentThread().getStackTrace()[2 + above];
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
          throw UnexpectedExceptionHandler.showUnexpectedErrorWarning(e);
        }
      }
    }
    return values;
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
      return (int) (value * UserSetting.FONT_SCALE_FACTOR.getFloatValue(LogInModel.getLoggedIn()));
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
      throw UnexpectedExceptionHandler.showUnexpectedErrorWarning(e);
    }
  }

  public static void beep() {
    if (LogInModel.getLoggedIn() == null
        || UserSetting.CREATE_BEEP_SOUND.getBooleanValue(LogInModel.getLoggedIn()))
      Toolkit.getDefaultToolkit().beep();
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

  public static int calculateStringDifference(String x, String y) {
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

  public static String accessString(AccessSupplier<String> supplier) {
    return runIfPossible(supplier).orElse("[Keine Leseberechtigung]");
  }

  public static <T> SecuredOptional<T> runIfPossible(AccessSupplier<T> supplier) {
    try {
      return SecuredOptional.ofNullable(supplier.get());
    } catch (AccessDeniedException e) {
      return SecuredOptional.empty();
    }
  }

  public static boolean canInvoke(AccessRunnable runnable) {
    try {
      runnable.run();
      return true;
    } catch (AccessDeniedException e) {
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
    return runIfPossible(supplier).orElse(v);
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
        String nbrString = s.substring(numberBeginIndex, i).replace(",", ".");
        if (!nbrString.equals(".")) out[storeIndex++] = Double.parseDouble(nbrString);
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
    AuditReader auditReaderFactory = AuditReaderFactory.get(em);
    List<Number> revisions = auditReaderFactory.getRevisions(o.getClass(), Tools.getId(o));
    return revisions.get(revisions.size() - 1).intValue();
  }

  public static String jasperTaggedStyling(String text, String markup) {
    if (markup.isEmpty()) return text;
    return "<" + markup + ">" + text + "</" + markup + ">";
  }

  public static <T> T ifNull(T value, T defaultValue) {
    if (value != null) {
      return value;
    } else {
      return defaultValue;
    }
  }

  @SafeVarargs
  public static <T> List<T> getAll(Class<T> tableClass, PredicateFactory<T>... whereCondition) {
    return QueryBuilder.selectAll(tableClass).where(whereCondition).getResultList();
  }

  public static String capitalize1st(String s) {
    return (s.substring(0, 1).toUpperCase()) + s.substring(1);
  }
}
