package kernbeisser.Security;

import java.util.Arrays;
import java.util.Collection;
import kernbeisser.DBEntities.Permission;
import kernbeisser.Enums.PermissionKey;

/**
 * loads given permission set into a bit field saved as long array so we can easily check a
 * permission with bit field comparision against it.
 */
public final class MasterPermissionSet {

  /** the java option for a c++ bitfield */
  private static final long[] bits = new long[((PermissionKey.values().length / Long.SIZE) + 1)];

  /**
   * loads all permission from the collection into the bit field
   *
   * @param permissions the collection of all permission which the logged in user has.
   */
  public static void loadPermission(Collection<Permission> permissions) {
    Arrays.fill(bits, 0L);
    for (Permission permission : permissions) {
      for (PermissionKey key : permission.getKeySet()) {
        int o = key.ordinal();
        bits[o / Long.SIZE] = (bits[o / Long.SIZE] | (1 << (o % Long.SIZE)));
      }
    }
  }

  /**
   * checks if the PermissionSet contains a Key by the ordinal value of it
   *
   * @param key the key which should get tested against the bit field
   * @return the boolean if the PermissionSet contains the permission
   */
  public static boolean hasPermission(PermissionKey key) {
    long bit = (1 << (key.ordinal() % Long.SIZE));
    return (bits[key.ordinal() / Long.SIZE] & bit) == bit;
  }

  /**
   * sets specific key in the PermissionSet to true
   *
   * @param key the key which should added to the permission set
   */
  public static void addPermission(PermissionKey key) {
    int o = key.ordinal();
    bits[o / Long.SIZE] = (bits[o / Long.SIZE] | (1 << (o % Long.SIZE)));
  }

  /**
   * removes the Key from the permission
   *
   * @param key the key which should be removed
   */
  public static void removePermission(PermissionKey key) {
    int o = key.ordinal();
    long bit = (1 << (key.ordinal() % Long.SIZE));
    if ((bits[key.ordinal() / Long.SIZE] & bit) == bit) {
      bits[o / Long.SIZE] = (bits[o / Long.SIZE] & (1 << (o % Long.SIZE)));
    }
  }

  /**
   * {@link #hasPermission(PermissionKey)} checks if the PermissionSet contains multiple keys
   *
   * @param keys all keys in a var args form
   * @return true if the PermissionSet contains all keys
   */
  public static boolean hasPermissions(PermissionKey... keys) {
    for (PermissionKey key : keys) {
      if (!hasPermission(key)) {
        return false;
      }
    }
    return true;
  }

  /**
   * enables / disable all bits(Permissions)
   *
   * @param b the value which the values become set
   */
  public static void setAllBits(boolean b) {
    Arrays.fill(bits, b ? -1L : 0);
  }
}
