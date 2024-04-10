package kernbeisser.DBConnection;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.From;
import jakarta.persistence.criteria.Selection;

public interface SelectionFactory<P> {
  Selection<?> createSelection(From<P, ?> source, CriteriaBuilder cb);

  static <P> SelectionFactory<P> selectAll(Class<P> clazz) {
    return ((source, cb) -> source.alias(clazz.getSimpleName()));
  }
}
