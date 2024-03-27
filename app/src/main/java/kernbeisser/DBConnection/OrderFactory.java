package kernbeisser.DBConnection;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Order;

public interface OrderFactory<T> {
  Order createOrder(Source<T> source, CriteriaBuilder cb);
}
