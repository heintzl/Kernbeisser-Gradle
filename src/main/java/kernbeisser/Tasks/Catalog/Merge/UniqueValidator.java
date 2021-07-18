package kernbeisser.Tasks.Catalog.Merge;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import kernbeisser.Security.Utils.Getter;

public class UniqueValidator<T> {

  private final UniquePropertyFactory<T>[] propertyFactories;
  private final HashSet<Object>[] uniqueProperties;

  @SuppressWarnings("unchecked")
  public UniqueValidator(UniquePropertyFactory<T>... propertyFactories) {
    this.propertyFactories = propertyFactories;
    this.uniqueProperties = new HashSet[propertyFactories.length];
    for (int i = 0; i < this.uniqueProperties.length; i++) {
      this.uniqueProperties[i] = new HashSet<>(propertyFactories[i].startElements());
    }
  }

  public boolean brakesUniqueConstraints(T obj) {
    int ps = propertyFactories.length;
    for (int i = 0; i < ps; i++) {
      UniquePropertyFactory<T> factory = propertyFactories[i];
      Object value = factory.obtainProperty(obj);
      if (!(value == null && factory.allowNull()) && !uniqueProperties[i].add(value)) {
        rollback(i, obj);
        return true;
      }
    }
    return false;
  }

  public void rollback(int index, T obj) {
    for (int i = 0; i < index + 1; i++) {
      if (!uniqueProperties[i].remove(propertyFactories[i].obtainProperty(obj)))
        throw new RuntimeException("rollback hasn't work properly");
    }
  }

  interface UniquePropertyFactory<T> {
    Object obtainProperty(T parent);

    boolean allowNull();

    Collection<?> startElements();
  }

  public static <T> UniquePropertyFactory<T> allowNull(Getter<T, Object> property) {
    return allowNull(property, Collections.emptyList());
  }

  public static <T> UniquePropertyFactory<T> forbidNull(Getter<T, Object> property) {
    return forbidNull(property, Collections.emptyList());
  }

  public static <T> UniquePropertyFactory<T> allowNull(
      Getter<T, Object> property, Collection<?> startElements) {
    return create(property, true, startElements);
  }

  public static <T> UniquePropertyFactory<T> forbidNull(
      Getter<T, Object> property, Collection<?> startElements) {
    return create(property, false, startElements);
  }

  public static <T> UniquePropertyFactory<T> create(
      Getter<T, Object> property, boolean allowNull, Collection<?> startElements) {
    return new UniquePropertyFactory<T>() {
      @Override
      public Object obtainProperty(T parent) {
        return property.get(parent);
      }

      @Override
      public boolean allowNull() {
        return allowNull;
      }

      @Override
      public Collection<?> startElements() {
        return startElements;
      }
    };
  }
}
