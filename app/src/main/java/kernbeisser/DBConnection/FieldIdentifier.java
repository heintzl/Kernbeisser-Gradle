package kernbeisser.DBConnection;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.From;
import jakarta.persistence.metamodel.Attribute;
import lombok.Getter;

@Getter
public class FieldIdentifier<P, V> implements ExpressionFactory<P, V> {
  private final String name;

  private final Class<P> tableClass;

  private final Class<V> propertyClass;

  public FieldIdentifier(Class<P> type, Class<V> propertyClass, String name) {
    this.name = name;
    this.tableClass = type;
    this.propertyClass = propertyClass;
  }

  @Override
  public Expression<V> createExpression(From<P,?> from, CriteriaBuilder cb) {
    return from.get(this);
  }
}
