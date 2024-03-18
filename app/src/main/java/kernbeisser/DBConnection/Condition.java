package kernbeisser.DBConnection;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.jetbrains.annotations.NotNull;

public interface Condition <P> {
  Predicate buildPredicate(CriteriaBuilder cb, Root<P> root);
  
  default Condition<P> not(){
    return (cb, root) -> Condition.this.buildPredicate(cb, root).not();
  }
  
  static <P, V> Condition<P> in(Field<P, V> field, Object ... values){
    return (cb, root) -> field.getExpression(root,cb).in(values);
  }
  
  static <P, V> Condition<P> eq(Field<P, V> field, @NotNull Object value){
    return (cb, root) -> cb.equal(field.getExpression(root,cb),value);
  }
  
  static <P, V> Condition<P> isNull(Field<P, V> field){
    return (cb, root) -> cb.isNull(field.getExpression(root,cb));
  }
  
  static <P> Condition<P> like(Field<P, String> stringField, String likeWhat){
    return (cb, root) -> cb.like(stringField.getExpression(root,cb),likeWhat);
  }
}
