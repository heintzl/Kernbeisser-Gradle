package kernbeisser.Security.IterableProtection;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import kernbeisser.Enums.PermissionKey;
import kernbeisser.Security.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MapProxy<K, V> implements Map<K, V>, ProtectedIterable {

  private final boolean proxify;

  private Map<K, V> map;

  private MapProxy(boolean proxify) {
    this.proxify = proxify;
  }

  public static <K, V> MapProxy<K, V> create(
      Map<K, V> map,
      PermissionSet ps,
      PermissionKey[] read,
      PermissionKey[] modify,
      boolean proxyfy) {
    MapProxy<K, V> out = new MapProxy<>(proxyfy);
    out.map = map;
    return Proxy.injectMethodHandler(
        out, new PermissionSetSecurityHandler(ProtectedIterable.transform(ps, read, modify)));
  }

  @Override
  @Key(PermissionKey.READ_ITERABLE_VALUE)
  public int size() {
    return map.size();
  }

  @Override
  @Key(PermissionKey.READ_ITERABLE_VALUE)
  public boolean isEmpty() {
    return map.isEmpty();
  }

  @Override
  @Key(PermissionKey.READ_ITERABLE_VALUE)
  public boolean containsKey(Object key) {
    return map.containsValue(key);
  }

  @Override
  @Key(PermissionKey.READ_ITERABLE_VALUE)
  public boolean containsValue(Object value) {
    return map.containsValue(value);
  }

  @Override
  @Key(PermissionKey.READ_ITERABLE_VALUE)
  public V get(Object key) {
    return proxify ? Proxy.getSecureInstance(map.get(key)) : map.get(key);
  }

  @Nullable
  @Override
  @Key(PermissionKey.MODIFY_ITERABLE_VALUE)
  public V put(K key, V value) {
    return map.put(key, value);
  }

  @Override
  @Key(PermissionKey.MODIFY_ITERABLE_VALUE)
  public V remove(Object key) {
    return map.remove(key);
  }

  @Override
  @Key(PermissionKey.MODIFY_ITERABLE_VALUE)
  public void putAll(@NotNull Map<? extends K, ? extends V> m) {
    map.putAll(m);
  }

  @Override
  @Key(PermissionKey.MODIFY_ITERABLE_VALUE)
  public void clear() {
    map.clear();
  }

  @NotNull
  @Override
  @Key(PermissionKey.READ_ITERABLE_VALUE)
  public Set<K> keySet() {
    return map.keySet();
  }

  @NotNull
  @Override
  @Key(PermissionKey.READ_ITERABLE_VALUE)
  public Collection<V> values() {
    return map.values();
  }

  @NotNull
  @Override
  @Key(PermissionKey.READ_ITERABLE_VALUE)
  public Set<Entry<K, V>> entrySet() {
    return map.entrySet();
  }
}
