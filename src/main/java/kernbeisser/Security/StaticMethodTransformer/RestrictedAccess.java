package kernbeisser.Security.StaticMethodTransformer;

import kernbeisser.Enums.KeyCollection;
import kernbeisser.Enums.PermissionKey;
import kernbeisser.Security.PermissionSet;
import kernbeisser.Security.Requires;

public interface RestrictedAccess extends StaticInterface {

  @StaticAccessPoint
  /*
   method becomes invoked on a null-reference object, its like calling a static method
   but its a normal method, any kind of reference to a class member will cause nullptr.
   exceptions or on primitive members which cant be null the default value almost every
   time 0
  */
  default PermissionSet getRequiredKeys() {
    PermissionSet permissionSet = new PermissionSet();
    Class<?> scanTarget = getClass();
    while (!scanTarget.equals(Object.class)) {
      Requires annotation = scanTarget.getAnnotation(Requires.class);
      if (annotation != null) {
        for (KeyCollection collection : annotation.collections()) {
          for (PermissionKey key : collection.get()) {
            permissionSet.addPermission(key);
          }
        }
        for (PermissionKey permissionKey : annotation.value()) {
          permissionSet.addPermission(permissionKey);
        }
      }
      scanTarget = scanTarget.getSuperclass();
    }
    return permissionSet;
  }
}
