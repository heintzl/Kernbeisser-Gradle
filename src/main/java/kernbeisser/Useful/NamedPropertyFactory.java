package kernbeisser.Useful;

public interface NamedPropertyFactory<T, V> {
  String getPropertyName();

  V getValue(T t);
}
