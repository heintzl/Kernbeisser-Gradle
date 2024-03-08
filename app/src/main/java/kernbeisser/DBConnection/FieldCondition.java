package kernbeisser.DBConnection;

import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

public class FieldCondition {

  private final String fieldName;
  private final Object[] values;
  @Getter private boolean not = false;

  public FieldCondition(String fieldName, @NotNull Object... values) {
    this.fieldName = fieldName;
    this.values = values;
  }

  public FieldCondition not() {
    not = true;
    return this;
  }

  public static FieldCondition isNull(String fieldName) {
    return new FieldCondition(fieldName) {
      @Override
      public <T> Predicate buildPredicate(Root<T> root) {
        return this.isNot() ? root.get(fieldName).isNull().not() : root.get(fieldName).isNull();
      }
    };
  }

  public <T> Predicate buildPredicate(Root<T> root) {
    if (values == null) {
      return root.get(fieldName).isNull();
    }
    if (not) {
      return root.get(fieldName).in(values).not();
    }
    return root.get(fieldName).in(values);
  }
}
