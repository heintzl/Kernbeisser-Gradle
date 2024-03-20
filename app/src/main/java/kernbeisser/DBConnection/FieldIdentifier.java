package kernbeisser.DBConnection;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
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

  public <N> FieldIdentifier<P, N> child(FieldIdentifier<V, N> childField) {
    return new FieldIdentifier<P, N>(tableClass,childField.propertyClass, name) {
      @Override
      public Expression<N> createExpression(Source<P> source, CriteriaBuilder cb) {
        return childField.createExpression(source.join(FieldIdentifier.this), cb);
      }
    };
  }

  @Override
  public Expression<V> createExpression(Source<P> source, CriteriaBuilder cb) {
    return source.get(this);
  }
}
