package kernbeisser.CustomComponents.ObjectTable;

public interface RowFilter<T> {
  boolean isDisplayed(T t);
}
