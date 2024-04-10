package kernbeisser.DBConnection;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.From;
import jakarta.persistence.criteria.Order;

public interface OrderFactory<T> {
  Order createOrder(From<T, ?> source, CriteriaBuilder cb);
}
