package kernbeisser.DBConnection;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Root;
import lombok.Getter;

@Getter
public record FieldOrder<T>(Class<T> talbleClass, String name, boolean ascending) {
  public Order toOrder(Root<T> root, CriteriaBuilder cb) {
    if (ascending) {
      return cb.asc(root.get(name));
    }
    return cb.desc(root.get(name));
  }
}
