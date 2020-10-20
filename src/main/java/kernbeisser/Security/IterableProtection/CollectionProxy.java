package kernbeisser.Security.IterableProtection;

import java.util.Collection;
import java.util.Iterator;
import java.util.function.Predicate;
import kernbeisser.Enums.PermissionKey;
import kernbeisser.Security.Key;
import kernbeisser.Security.PermissionSet;
import kernbeisser.Security.PermissionSetSecurityHandler;
import kernbeisser.Security.Proxy;
import org.jetbrains.annotations.NotNull;

public class CollectionProxy<T> implements Collection<T>, ProtectedIterable {

  private Collection<T> values;

  public static <T> CollectionProxy<T> createWithProxyChildren(
      Collection<T> collection, PermissionSet ps, PermissionKey[] read, PermissionKey[] modify) {
    return create(Proxy.getSecureInstances(collection), ps, read, modify);
  }

  public static <T> CollectionProxy<T> create(
      Collection<T> collection, PermissionSet ps, PermissionKey[] read, PermissionKey[] modify) {
    CollectionProxy<T> out = new CollectionProxy<>();
    out.values = collection;
    return Proxy.injectMethodHandler(
        out, new PermissionSetSecurityHandler(ProtectedIterable.transform(ps, read, modify)));
  }

  @Override
  @Key(PermissionKey.READ_ITERABLE_VALUE)
  public int size() {
    return values.size();
  }

  @Override
  @Key(PermissionKey.READ_ITERABLE_VALUE)
  public boolean isEmpty() {
    return values.isEmpty();
  }

  @Override
  @Key(PermissionKey.READ_ITERABLE_VALUE)
  public boolean contains(Object o) {
    return values.contains(o);
  }

  @NotNull
  @Override
  @Key(PermissionKey.READ_ITERABLE_VALUE)
  public Iterator<T> iterator() {
    return values.iterator();
  }

  @NotNull
  @Override
  @Key(PermissionKey.READ_ITERABLE_VALUE)
  public Object[] toArray() {
    return values.toArray();
  }

  @NotNull
  @Override
  @Key(PermissionKey.READ_ITERABLE_VALUE)
  public <T1> T1[] toArray(@NotNull T1[] a) {
    return values.toArray(a);
  }

  @Override
  @Key(PermissionKey.MODIFY_ITERABLE_VALUE)
  public boolean add(T t) {
    return values.add(t);
  }

  @Override
  @Key(PermissionKey.MODIFY_ITERABLE_VALUE)
  public boolean remove(Object o) {
    return values.remove(o);
  }

  @Override
  @Key(PermissionKey.READ_ITERABLE_VALUE)
  public boolean containsAll(@NotNull Collection<?> c) {
    return values.containsAll(c);
  }

  @Override
  @Key(PermissionKey.MODIFY_ITERABLE_VALUE)
  public boolean addAll(@NotNull Collection<? extends T> c) {
    return values.addAll(c);
  }

  @Override
  @Key(PermissionKey.MODIFY_ITERABLE_VALUE)
  public boolean removeAll(@NotNull Collection<?> c) {
    return values.removeAll(c);
  }

  @Override
  @Key({PermissionKey.MODIFY_ITERABLE_VALUE, PermissionKey.READ_ITERABLE_VALUE})
  public boolean removeIf(Predicate<? super T> filter) {
    return values.removeIf(filter);
  }

  @Override
  @Key(PermissionKey.MODIFY_ITERABLE_VALUE)
  public boolean retainAll(@NotNull Collection<?> c) {
    return values.retainAll(c);
  }

  @Override
  public void clear() {
    values.clear();
  }
}
