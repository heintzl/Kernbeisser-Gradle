package kernbeisser.Security.Access;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
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
  @Setter private static AccessManager defaultManager = AccessManager.ACCESS_DENIED;

  @Setter private static boolean useCustomProtection = true;

  @Getter private static final WeakHashMap<Object, AccessManager> exceptions = new WeakHashMap<>();

  // caches methods which are already analysed
  // log stands for the methods id which is
  // created by the java agent
  private static final HashMap<Long, PermissionKey[]> cache = new HashMap<>();

  static {
    loadUnprotectedInstanceExceptions();
  }

  // via java agent linked method
  public static void hasAccess(Object object, String methodName, String signature, long methodId) {
    PermissionKey[] keys =
        cache.computeIfAbsent(methodId, k -> runCallerAnalyse(object, methodName, methodId));
    if (!defaultManager.hasAccess(object, methodName, signature, keys)) {
      if (useCustomProtection) {
        AccessManager accessManager = exceptions.get(object);
        if (accessManager != null && accessManager.hasAccess(object, methodName, signature, keys))
          return;
      }
      throw new PermissionKeyRequiredException(
          "PermissionSet doesn't contain the following keys:" + Arrays.toString(keys));
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

  private static PermissionKey[] runCallerAnalyse(Object object, String methodName, long methodId) {
    for (Method declaredMethod : object.getClass().getDeclaredMethods()) {
      if (!declaredMethod.getName().equals(methodName)) {
        continue;
      }
      Key key = declaredMethod.getAnnotation(Key.class);
      if (key == null) continue;
      if (key.id() == methodId) {
        return key.value();
      }
    }
    throw new UnsupportedOperationException(
        "cannot find method with name and id: " + methodName + ":" + methodId);
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
      case "java.util.Collections.UnmodifiableCollection":
      case "java.util.Collections.UnmodifiableSet":
      case "java.util.Collections.UnmodifiableSortedSet":
      case "java.util.Collections.UnmodifiableNavigableSet":
      case "java.util.Collections.UnmodifiableList":
      case "java.util.Collections.UnmodifiableRandomAccessList":
      case "java.util.Collections.UnmodifiableMap":
      case "java.util.Collections.UnmodifiableSortedMap":
      case "java.util.Collections.UnmodifiableNavigableMap":
        return false;
      default:
        return true;
    }
  }
}
