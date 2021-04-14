package kernbeisser.Security.IterableProtection;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.function.Predicate;
import kernbeisser.Enums.PermissionKey;
import kernbeisser.Security.*;
import kernbeisser.Security.MethodHandlers.AbstractSecurityHandler;
import kernbeisser.Security.MethodHandlers.ProtectedIterablePermissionSetHandler;
import org.jetbrains.annotations.NotNull;

public class SetProxy<T> implements Set<T>, ProtectedIterable {
  private Collection<T> values;

  private SetProxy() {}

  public static <T> SetProxy<T> create(
      Collection<T> collection,
      PermissionKey[] read,
      PermissionKey[] modify,
      AbstractSecurityHandler methodHandler) {
    SetProxy<T> out = new SetProxy<>();
    out.values = collection;
    return Proxy.injectMethodHandler(
        out, new ProtectedIterablePermissionSetHandler(read, modify, methodHandler));
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
