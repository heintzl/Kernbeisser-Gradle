package kernbeisser.DBConnection;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Selection;

public interface SelectionFactory<P> {
  Selection<?> createSelection(Source<P> source, CriteriaBuilder cb);

  static <P> SelectionFactory<P> selectAll(Class<P> clazz) {
    return ((source, cb) -> source.getFrom().alias(clazz.getSimpleName()));
  }
}
