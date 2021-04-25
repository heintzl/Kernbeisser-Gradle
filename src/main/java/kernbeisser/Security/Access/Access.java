package kernbeisser.Security.Access;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Optional;
import java.util.WeakHashMap;
import kernbeisser.Enums.PermissionConstants;
import kernbeisser.Enums.PermissionKey;
import kernbeisser.Exeptions.PermissionKeyRequiredException;
import kernbeisser.Security.Key;
import kernbeisser.Security.PermissionSet;
import kernbeisser.Unsafe.PermissionKeyMethodVisitor;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;

public class Access {

  // default do not allow any access to any field -> normal state if no one is logged in
  @Getter @Setter private static AccessManager defaultManager = AccessManager.NO_ACCESS_CHECKING;

  @Setter private static boolean useCustomProtection = true;

  @Getter private static final WeakHashMap<Object, AccessManager> exceptions = new WeakHashMap<>();

  private static PermissionSet[] arrayCache = new PermissionSet[400];

  static {
    loadUnprotectedInstanceExceptions();
    defaultManager = AccessManager.ACCESS_DENIED;
  }

  // via java agent linked method
  public static void hasAccess(Object object, String methodName, String signature, long methodId) {
    PermissionSet keys = getOrAnalyse(object, methodName, methodId);
    if (!defaultManager.hasAccess(object, methodName, signature, keys)) {
      if (useCustomProtection) {
        AccessManager accessManager = exceptions.get(object);
        if (accessManager != null && accessManager.hasAccess(object, methodName, signature, keys))
          return;
      }
      throw new PermissionKeyRequiredException(
          "PermissionSet doesn't contain the following keys:" + keys);
    }
  }

  public static void loadUnprotectedInstanceExceptions() {
    for (PermissionConstants value : PermissionConstants.values()) {
      exceptions.put(value.getPermission(), AccessManager.NO_ACCESS_CHECKING);
    }
  }

  public static AccessManager getAccessManager(Object o) {
    return getCustomAccessManager(o).orElse(defaultManager);
  }

  public static Optional<AccessManager> getCustomAccessManager(Object o) {
    return Optional.ofNullable(exceptions.get(o));
  }

  private static PermissionSet runCallerAnalyse(Object object, String methodName, long methodId) {
    for (Class<?> target = object.getClass();
        !target.equals(Object.class);
        target = target.getSuperclass()) {
      for (Method declaredMethod : target.getDeclaredMethods()) {
        if (!declaredMethod.getName().equals(methodName)) {
          continue;
        }
        Key key = declaredMethod.getAnnotation(Key.class);
        if (key == null) continue;
        if (key.id() == methodId) {
          return PermissionSet.asPermissionSet(key.value());
        }
      }
      for (Constructor<?> declaredConstructor : target.getDeclaredConstructors()) {
        Key key = declaredConstructor.getAnnotation(Key.class);
        if (key == null) continue;
        if (key.id() == methodId) {
          return PermissionSet.asPermissionSet(key.value());
        }
      }
    }
    throw new UnsupportedOperationException(
        "cannot find method with name and id: " + methodName + ":" + methodId);
  }

  public static boolean expectHasPermission(Serializable serializable) {
    if (defaultManager instanceof PermissionKeyBasedAccessManager)
      return ((PermissionKeyBasedAccessManager) defaultManager)
          .hasPermission(peekPermissions(serializable));
    throw new UnsupportedOperationException("Default access manager is not peek able");
  }

  public static <T, V> boolean hasPermission(
      kernbeisser.Security.Utils.Getter<T, V> getter, T parent) {
    AccessManager accessManager = getAccessManager(parent);

    if (accessManager instanceof PermissionKeyBasedAccessManager)
      return ((PermissionKeyBasedAccessManager) accessManager)
          .hasPermission(peekPermissions(getter));

    throw new UnsupportedOperationException(
        "AccessManager of object " + parent + " is not peekable");
  }

  public static <T, V> boolean hasPermission(
      kernbeisser.Security.Utils.Setter<T, V> setter, T parent) {
    AccessManager accessManager = getAccessManager(parent);

    if (accessManager instanceof PermissionKeyBasedAccessManager)
      return ((PermissionKeyBasedAccessManager) accessManager)
          .hasPermission(peekPermissions(setter));

    throw new UnsupportedOperationException(
        "AccessManager of object " + parent + " is not peekable");
  }

  /**
   * peeks the into the byte code of the specified method and all sub methods. It searches for
   * PermissionKey annotations and collect them in a permissionSet, the result is permissionSet with
   * all possible required permissions. //TODO: maybe soft cache the results of the scans
   */
  @SneakyThrows
  public static PermissionSet peekPermissions(Serializable interfaceLambdaImp) {
    return PermissionKeyMethodVisitor.accessedKeys(interfaceLambdaImp);
  }

  public static boolean isIterableModifiable(@NotNull Iterable<?> iterable) {
    switch (iterable.getClass().getName()) {
      case "java.util.Collections$UnmodifiableCollection":
      case "java.util.Collections$UnmodifiableSet":
      case "java.util.Collections$UnmodifiableSortedSet":
      case "java.util.Collections$UnmodifiableNavigableSet":
      case "java.util.Collections$UnmodifiableList":
      case "java.util.Collections$UnmodifiableRandomAccessList":
      case "java.util.Collections$UnmodifiableMap":
      case "java.util.Collections$UnmodifiableSortedMap":
      case "java.util.Collections$UnmodifiableNavigableMap":
        return false;
      default:
        return true;
    }
  }

  public static PermissionSet getOrAnalyse(Object object, String methodName, long methodId) {
    int arrayIndex = (int) methodId;
    try {
      PermissionSet set = arrayCache[arrayIndex];
      if (set == null) {
        arrayCache[(int) methodId] = runCallerAnalyse(object, methodName, methodId);
        return arrayCache[arrayIndex];
      } else {
        return set;
      }
    } catch (ArrayIndexOutOfBoundsException e) {
      arrayCache = Arrays.copyOf(arrayCache, arrayIndex + 1);
      return getOrAnalyse(object, methodName, methodId);
    }
  }

  @Key(PermissionKey.USER_ID_READ)
  private void hasAccess() {}

  public static boolean isActive() {
    AccessManager before = Access.getDefaultManager();
    try {
      setDefaultManager(AccessManager.ACCESS_DENIED);
      new Access().hasAccess();
      return false;
    } catch (PermissionKeyRequiredException e) {
      if (defaultManager.equals(AccessManager.ACCESS_DENIED)) {
        setDefaultManager(before);
        return true;
      } else throw new RuntimeException("modification of access manager while checking state");
    }
  }
}
