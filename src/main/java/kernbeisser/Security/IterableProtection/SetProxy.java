package kernbeisser.Security.IterableProtection;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.function.Predicate;
import kernbeisser.Enums.PermissionKey;
import kernbeisser.Security.*;
import org.jetbrains.annotations.NotNull;

public class SetProxy<T> implements Set<T> {
  private Collection<T> values;

  public static <T> SetProxy<T> createWithProxyChildren(
      Collection<T> collection, PermissionKey read, PermissionKey modify) {
    return create(Proxy.getSecureInstances(collection), read, modify);
  }

  public static <T> SetProxy<T> create(
      Collection<T> collection, PermissionKey read, PermissionKey modify) {
    SetProxy<T> out = new SetProxy<>();
    out.values = collection;
    PermissionSet permissionSet = new PermissionSet();
    if (MasterPermissionSet.hasPermission(read)) {
      permissionSet.addPermission(PermissionKey.READ_SET_VALUE);
    }
    if (MasterPermissionSet.hasPermission(modify)) {
      permissionSet.addPermission(PermissionKey.MODIFY_SET_VALUE);
    }
    return Proxy.injectMethodHandler(out, new CustomKeySetSecurityHandler(permissionSet));
  }

  @Override
  @Key(PermissionKey.READ_SET_VALUE)
  public int size() {
    return values.size();
  }

  @Override
  @Key(PermissionKey.READ_SET_VALUE)
  public boolean isEmpty() {
    return values.isEmpty();
  }

  @Override
  @Key(PermissionKey.READ_SET_VALUE)
  public boolean contains(Object o) {
    return values.contains(o);
  }

  @NotNull
  @Override
  @Key(PermissionKey.READ_SET_VALUE)
  public Iterator<T> iterator() {
    return values.iterator();
  }

  @NotNull
  @Override
  @Key(PermissionKey.READ_SET_VALUE)
  public Object[] toArray() {
    return values.toArray();
  }

  @NotNull
  @Override
  @Key(PermissionKey.READ_SET_VALUE)
  public <T1> T1[] toArray(@NotNull T1[] a) {
    return values.toArray(a);
  }

  @Override
  @Key(PermissionKey.MODIFY_SET_VALUE)
  public boolean add(T t) {
    return values.add(t);
  }

  @Override
  @Key(PermissionKey.MODIFY_SET_VALUE)
  public boolean remove(Object o) {
    return values.remove(o);
  }

  @Override
  @Key(PermissionKey.READ_SET_VALUE)
  public boolean containsAll(@NotNull Collection<?> c) {
    return values.containsAll(c);
  }

  @Override
  @Key(PermissionKey.MODIFY_SET_VALUE)
  public boolean addAll(@NotNull Collection<? extends T> c) {
    return values.addAll(c);
  }

  @Override
  @Key(PermissionKey.MODIFY_SET_VALUE)
  public boolean removeAll(@NotNull Collection<?> c) {
    return values.removeAll(c);
  }

  @Override
  @Key({PermissionKey.MODIFY_SET_VALUE, PermissionKey.READ_SET_VALUE})
  public boolean removeIf(Predicate<? super T> filter) {
    return values.removeIf(filter);
  }

  @Override
  @Key(PermissionKey.MODIFY_SET_VALUE)
  public boolean retainAll(@NotNull Collection<?> c) {
    return values.retainAll(c);
  }

  @Override
  public void clear() {
    values.clear();
  }
}
