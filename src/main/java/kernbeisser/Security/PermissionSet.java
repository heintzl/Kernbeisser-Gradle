package kernbeisser.Security;

import java.util.Arrays;
import java.util.Collection;
import kernbeisser.DBEntities.Permission;
import kernbeisser.Enums.PermissionKey;

public class PermissionSet {
  /** the java option for a c bitfield */
  private final long[] bits = new long[((PermissionKey.values().length / Long.SIZE) + 1)];

  /**
   * loads all permission from the collection into the bit field
   *
   * @param permissions the collection of all permission which the logged in user has.
   */
  public void loadPermission(Collection<Permission> permissions) {
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
  public boolean hasPermission(PermissionKey key) {
    long bit = (1 << (key.ordinal() % Long.SIZE));
    return (bits[key.ordinal() / Long.SIZE] & bit) == bit;
  }

  /**
   * sets specific key in the PermissionSet to true
   *
   * @param key the key which should added to the permission set
   */
  public void addPermission(PermissionKey key) {
    bits[key.ordinal() / Long.SIZE] =
        (bits[key.ordinal() / Long.SIZE] | (1 << (key.ordinal() % Long.SIZE)));
  }

  /**
   * removes the Key from the permission
   *
   * @param key the key which should be removed
   */
  public void removePermission(PermissionKey key) {
    bits[key.ordinal() / Long.SIZE] =
        (bits[key.ordinal() / Long.SIZE] & (~(1 << (key.ordinal() % Long.SIZE))));
  }

  /**
   * {@link #hasPermission(PermissionKey)} checks if the PermissionSet contains multiple keys
   *
   * @param keys all keys in a var args form
   * @return true if the PermissionSet contains all keys
   */
  public boolean hasPermissions(PermissionKey... keys) {
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
  public void setAllBits(boolean b) {
    Arrays.fill(bits, b ? -1L : 0);
  }
}
