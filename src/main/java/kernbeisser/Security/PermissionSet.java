package kernbeisser.Security;

import java.math.BigInteger;
import java.util.*;
import kernbeisser.DBEntities.Permission;
import kernbeisser.Enums.PermissionKey;

/**
 * loads given permission set into a bit field saved as long array so we can easily check a
 * permission with bit field comparision against it.
 */
public class PermissionSet {
  public static final PermissionSet MASTER = new PermissionSet();

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
        addPermission(key);
      }
    }
  }

  public void loadKeys(Iterable<PermissionKey> keys) {
    for (PermissionKey key : keys) {
      addPermission(key);
    }
  }

  /**
   * checks if the PermissionSet contains a Key by the ordinal value of it
   *
   * @param key the key which should get tested against the bit field
   * @return the boolean if the PermissionSet contains the permission
   */
  public boolean hasPermission(PermissionKey key) {
    long bit = (1L << (key.ordinal() % Long.SIZE));
    return (bits[key.ordinal() / Long.SIZE] & bit) == bit;
  }

  /** checks if a PermissionSet has all permissions of another one */
  public boolean contains(PermissionSet permissionSet) {
    if (permissionSet.bits.length != bits.length) {
      throw new RuntimeException(
          "fatal error occurred, the PermissionSet doesn't haven the same length! should not happen in normal cases");
    }
    for (int i = 0; i < bits.length; i++) {
      if ((permissionSet.bits[i] & bits[i]) != permissionSet.bits[i]) return false;
    }
    return true;
  }

  /**
   * sets specific key in the PermissionSet to true
   *
   * @param key the key which should added to the permission set
   */
  public void addPermission(PermissionKey key) {
    bits[key.ordinal() / Long.SIZE] =
        (bits[key.ordinal() / Long.SIZE] | (1L << (key.ordinal() % Long.SIZE)));
  }

  /**
   * removes the Key from the permission
   *
   * @param key the key which should be removed
   */
  public void removePermission(PermissionKey key) {
    bits[key.ordinal() / Long.SIZE] =
        (bits[key.ordinal() / Long.SIZE] & (~(1L << (key.ordinal() % Long.SIZE))));
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
    Arrays.fill(bits, b ? -1L : 0L);
  }

  public Set<PermissionKey> asSet() {
    HashSet<PermissionKey> permissionKeys = new HashSet<>();
    for (PermissionKey value : PermissionKey.values()) {
      if (hasPermission(value)) permissionKeys.add(value);
    }
    return permissionKeys;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    for (long bit : bits) {
      sb.append(String.format("%032d", new BigInteger(Long.toBinaryString(bit))));
    }
    return sb.toString();
  }
}
