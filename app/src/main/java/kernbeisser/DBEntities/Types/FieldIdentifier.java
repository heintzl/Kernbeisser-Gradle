package kernbeisser.DBEntities.Types;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Root;
import kernbeisser.DBConnection.Field;
import kernbeisser.DBConnection.FieldOrder;
import lombok.Getter;

@Getter
public class FieldIdentifier<P,V> implements Field<P,V> {
  private final String name;

  private final Class<P> tableClass;
  

  public FieldIdentifier(Class<P> type, String name) {
    this.name = name;
    this.tableClass = type;
  }
  

  public FieldOrder<P> asc() {
    return new FieldOrder<P>(tableClass, name, true);
  }

  public FieldOrder<P> desc() {
    return new FieldOrder<P>(tableClass, name, false);
  }
  
  
  public <N> FieldIdentifier<P, N> child(FieldIdentifier<V, N> childField) {
    return new FieldIdentifier<>(tableClass, name){
      @Override
      public Expression<N> getExpression(Source<P> root, CriteriaBuilder criteriaBuilder) {
        return childField.getExpression(root.join(FieldIdentifier.this), criteriaBuilder);
      }
    };
  }
  
  @Override
  public Expression<V> getExpression(Source<P> root, CriteriaBuilder criteriaBuilder) {
    return root.get(this);
  }
  
}
