package kernbeisser.DBConnection;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.Arrays;

public abstract class CombinedCondition <P> implements Condition <P>{
  public static <P> CombinedCondition<P> and(Condition<P> ... conditions) {
    return new CombinedCondition<P>() {
      @Override
      public Predicate buildPredicate(CriteriaBuilder cb, Root<P> root) {
        return cb.and(
                Arrays.stream(conditions)
                        .map(e -> e.buildPredicate(cb, root))
                        .toArray(Predicate[]::new));
      }
    };
  }
  
  public static <P> CombinedCondition<P> or(Condition<P> ... conditions) {
    return new CombinedCondition<P>() {
      @Override
      public Predicate buildPredicate(CriteriaBuilder cb, Root<P> root) {
        return cb.or(
                Arrays.stream(conditions)
                        .map(e -> e.buildPredicate(cb, root))
                        .toArray(Predicate[]::new));
      }
    };
  }
}
