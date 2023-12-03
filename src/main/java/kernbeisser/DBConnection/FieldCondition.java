package kernbeisser.DBConnection;

import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

public class FieldCondition {

  private final String fieldName;
  private final Object[] values;
  private boolean not = false;

  public FieldCondition(String fieldName, Object... values) {
    this.fieldName = fieldName;
    this.values = values;
  }

  public FieldCondition not() {
    not = true;
    return this;
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
