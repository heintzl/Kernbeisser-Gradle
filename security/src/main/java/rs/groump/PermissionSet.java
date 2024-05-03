package rs.groump;

import java.math.BigInteger;
import java.util.*;
import java.util.function.LongBinaryOperator;

/**
 * loads given permission set into a bit field saved as long array, so we can easily check a
 * permission with bit field comparision against it.
 */
public class PermissionSet implements Set<PermissionKey> {

  private static final int ARRAY_SIZE = ((PermissionKey.values().length / Long.SIZE) + 1);

  /** the java option for a c bitfield */
  private final long[] bits = new long[ARRAY_SIZE];
  

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

  /** does simple bitwise or operator */
  public PermissionSet or(PermissionSet permissionSet) {
    PermissionSet out = new PermissionSet();
    for (int i = 0; i < bits.length; i++) {
      out.bits[i] = bits[i] | permissionSet.bits[i];
    }
    return out;
  }

  public void addAll(PermissionSet permissionSet) {
    for (int i = 0; i < bits.length; i++) {
      bits[i] = bits[i] | permissionSet.bits[i];
    }
  }

  public PermissionSet operator(PermissionSet ps, LongBinaryOperator operator) {
    PermissionSet out = new PermissionSet();
    for (int i = 0; i < bits.length; i++) {
      out.bits[i] = operator.applyAsLong(bits[i], ps.bits[i]);
    }
    return out;
  }

  /** returns the current set without the permissions delivered in the @Arg permissionSet */
  public PermissionSet minus(PermissionSet permissionSet) {
    PermissionSet out = new PermissionSet();
    for (int i = 0; i < bits.length; i++) {
      out.bits[i] = bits[i] & (~permissionSet.bits[i]);
    }
    return out;
  }

  /**
   * enables / disable all bits(Permissions)
   *
   * @param b the value which the values become set
   */
  public void setAllBits(boolean b) {
    Arrays.fill(bits, b ? -1L : 0L);
  }

  /** returns the PermissionSet as a binary String */
  public String asBinaryString() {
    StringBuilder sb = new StringBuilder();
    for (long bit : bits) {
      sb.append(String.format("%032d", new BigInteger(Long.toBinaryString(bit))));
    }
    return sb.toString();
  }

  /** returns a permissionSet with all the Permissions */
  public static PermissionSet asPermissionSet(PermissionKey[] keys) {
    PermissionSet ps = new PermissionSet();
    for (PermissionKey key : keys) {
      ps.addPermission(key);
    }
    return ps;
  }

  @Override
  public int size() {
    int size = 0;
    for (long bit : bits) {
      size += Long.bitCount(bit);
    }
    return size;
  }

  @Override
  public boolean isEmpty() {
    for (long bit : bits) {
      if (bit != 0) {
        return false;
      }
    }
    return true;
  }

  @Override
  public boolean contains(Object o) {
    return hasPermission((PermissionKey) o);
  }

  
  @Override
  public Iterator<PermissionKey> iterator() {
    return new Iterator<PermissionKey>() {

      private final PermissionKey[] all = PermissionKey.values();

      private final PermissionKey last = findLast();
      private int index = 0;

      private PermissionKey findLast() {
        for (int i = all.length - 1; i != 0; i--) {
          if (hasPermission(all[i])) {
            return all[i];
          }
        }
        return null;
      }

      @Override
      public boolean hasNext() {
        return last != null && last.ordinal() + 1 != index;
      }

      @Override
      public PermissionKey next() {
        if (hasPermission(all[index])) {
          return all[index++];
        }
        index++;
        return next();
      }
    };
  }

  
  @Override
  public PermissionKey [] toArray() {
    PermissionKey[] out = new PermissionKey[size()];
    int index = 0;
    for (PermissionKey permissionKey : this) {
      out[index++] = permissionKey;
    }
    return out;
  }
  
  @Override
  public <T> T [] toArray(T [] a) {
    PermissionKey[] out = new PermissionKey[size()];
    int index = 0;
    for (PermissionKey permissionKey : this) {
      out[index++] = permissionKey;
    }
    return (T[]) out;
  }

  @Override
  public boolean add(PermissionKey permissionKey) {
    int bitIndex = permissionKey.ordinal() / Long.SIZE;
    long bitBefore = bits[bitIndex];
    bits[bitIndex] = (bits[bitIndex] | (1L << (permissionKey.ordinal() % Long.SIZE)));
    return bitBefore != bits[bitIndex];
  }

  @Override
  public boolean remove(Object o) {
    PermissionKey permissionKey = (PermissionKey) o;
    int bitIndex = permissionKey.ordinal() / Long.SIZE;
    long bitBefore = bits[bitIndex];
    bits[bitIndex] = (bits[bitIndex] & (~(1L << (permissionKey.ordinal() % Long.SIZE))));
    return bitBefore != bits[bitIndex];
  }

  @Override
  public boolean containsAll(Collection<?> c) {
    for (Object o : c) {
      if (!(o instanceof PermissionKey) || !hasPermission((PermissionKey) o)) return false;
    }
    return true;
  }

  @Override
  public boolean addAll(Collection<? extends PermissionKey> c) {
    boolean changed = false;
    for (PermissionKey permissionKey : c) {
      changed = changed | add(permissionKey);
    }
    return changed;
  }

  @Override
  public boolean retainAll(Collection<?> c) {
    boolean changed = false;
    for (Object o : c) {
      if (add((PermissionKey) o)) {
        changed = changed | remove(o);
      }
    }
    return changed;
  }

  /**
   * removes all Permission containing in the given set from the current set
   *
   * @param set
   */
  public void removeAll(PermissionSet set) {
    for (int i = 0; i < bits.length; i++) {
      bits[i] = bits[i] & ~set.bits[i];
    }
  }

  @Override
  @Deprecated
  /**
   * @Depreccated use removeAll(PermissionSet) instead much faster implementation
   */
  public boolean removeAll(Collection<?> c) {
    boolean changed = false;
    for (Object permissionKey : c) {
      changed = changed | remove(permissionKey);
    }
    return changed;
  }

  @Override
  // stolen from the default hashSet impl
  public String toString() {
    Iterator<PermissionKey> it = iterator();
    if (!it.hasNext()) return "[]";
    StringBuilder sb = new StringBuilder();
    sb.append('[');
    for (; ; ) {
      sb.append(it.next().name());
      if (!it.hasNext()) return sb.append(']').toString();
      sb.append(',').append(' ');
    }
  }

  @Override
  public void clear() {
    setAllBits(false);
  }

  public boolean addAll(PermissionKey[] keys) {
    boolean changed = false;
    for (PermissionKey permissionKey : keys) {
      changed = changed | add(permissionKey);
    }
    return changed;
  }
  
  @Override
  public boolean equals(Object obj) {
    if(!(obj instanceof Collection<?>))return false;
    if(obj.getClass() == PermissionSet.class) return Arrays.equals(((PermissionSet) obj).bits, this.bits);
    try {
      return ((Collection<PermissionKey>) obj).containsAll(this) && this.containsAll((Collection<?>) obj);
    }catch (ClassCastException classCastException){
      //Collections do not match type -> therefore are not equal
      return false;
    }
    
  }
}
