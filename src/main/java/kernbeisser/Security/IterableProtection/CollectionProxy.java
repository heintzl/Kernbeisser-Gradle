package kernbeisser.Security.IterableProtection;

import java.util.Collection;
import java.util.Iterator;
import java.util.function.Predicate;
import kernbeisser.Enums.PermissionKey;
import kernbeisser.Security.*;
import org.jetbrains.annotations.NotNull;

public class CollectionProxy<T> implements Collection<T> {
  private Collection<T> values;

  public static <T> CollectionProxy<T> createWithProxyChildren(
      Collection<T> collection, PermissionKey read, PermissionKey modify) {
    return create(Proxy.getSecureInstances(collection), read, modify);
  }

  public static <T> CollectionProxy<T> create(
      Collection<T> collection, PermissionKey read, PermissionKey modify) {
    CollectionProxy<T> out = new CollectionProxy<>();
    out.values = collection;
    PermissionSet permissionSet = new PermissionSet();
    if (MasterPermissionSet.hasPermission(read)) {
      permissionSet.addPermission(PermissionKey.READ_COLLECTION_VALUE);
    }
    if (MasterPermissionSet.hasPermission(modify)) {
      permissionSet.addPermission(PermissionKey.MODIFY_COLLECTION_VALUE);
    }
    return Proxy.injectMethodHandler(out, new CustomKeySetSecurityHandler(permissionSet));
  }

  @Override
  @Key(PermissionKey.READ_COLLECTION_VALUE)
  public int size() {
    return values.size();
  }

  @Override
  @Key(PermissionKey.READ_COLLECTION_VALUE)
  public boolean isEmpty() {
    return values.isEmpty();
  }

  @Override
  @Key(PermissionKey.READ_COLLECTION_VALUE)
  public boolean contains(Object o) {
    return values.contains(o);
  }

  @NotNull
  @Override
  @Key(PermissionKey.READ_COLLECTION_VALUE)
  public Iterator<T> iterator() {
    return values.iterator();
  }

  @NotNull
  @Override
  @Key(PermissionKey.READ_COLLECTION_VALUE)
  public Object[] toArray() {
    return values.toArray();
  }

  @NotNull
  @Override
  @Key(PermissionKey.READ_COLLECTION_VALUE)
  public <T1> T1[] toArray(@NotNull T1[] a) {
    return values.toArray(a);
  }

  @Override
  @Key(PermissionKey.MODIFY_COLLECTION_VALUE)
  public boolean add(T t) {
    return values.add(t);
  }

  @Override
  @Key(PermissionKey.MODIFY_COLLECTION_VALUE)
  public boolean remove(Object o) {
    return values.remove(o);
  }

  @Override
  @Key(PermissionKey.READ_COLLECTION_VALUE)
  public boolean containsAll(@NotNull Collection<?> c) {
    return values.containsAll(c);
  }

  @Override
  @Key(PermissionKey.MODIFY_COLLECTION_VALUE)
  public boolean addAll(@NotNull Collection<? extends T> c) {
    return values.addAll(c);
  }

  @Override
  @Key(PermissionKey.MODIFY_COLLECTION_VALUE)
  public boolean removeAll(@NotNull Collection<?> c) {
    return values.removeAll(c);
  }

  @Override
  @Key({PermissionKey.MODIFY_COLLECTION_VALUE, PermissionKey.READ_COLLECTION_VALUE})
  public boolean removeIf(Predicate<? super T> filter) {
    return values.removeIf(filter);
  }

  @Override
  @Key(PermissionKey.MODIFY_COLLECTION_VALUE)
  public boolean retainAll(@NotNull Collection<?> c) {
    return values.retainAll(c);
  }

  @Override
  public void clear() {
    values.clear();
  }
}
