package kernbeisser.CustomComponents.ObjectTable;

import lombok.Getter;

@Getter
public class Property<T> {
  private final T parent;
  private final Object value;

  public Property(T parent, Object value) {
    this.parent = parent;
    this.value = value;
  }

  @Override
  public String toString() {
    return value.toString();
  }

  @Override
  public int hashCode() {
    return value.hashCode();
  }
}
