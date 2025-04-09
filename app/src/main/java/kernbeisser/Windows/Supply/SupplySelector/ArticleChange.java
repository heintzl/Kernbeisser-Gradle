package kernbeisser.Windows.Supply.SupplySelector;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;

@Getter
public class ArticleChange implements Comparable {

  private final int type;
  private final double oldValue;
  private final double newValue;

  public static final int PRICE = 0;
  public static final int SINGLE_DEPOSIT = 1;
  public static final int CONTAINER_DEPOSIT = 2;
  public static final int CONTAINER_SIZE = 3;

  private ArticleChange(int type, double oldValue, double newValue) {
    this.type = type;
    this.oldValue = oldValue;
    this.newValue = newValue;
  }

  public static ArticleChange PRICE(double oldValue, double newValue) {
    return new ArticleChange(PRICE, oldValue, newValue);
  }

  public static ArticleChange SINGLE_DEPOSIT(double oldValue, double newValue) {
    return new ArticleChange(SINGLE_DEPOSIT, oldValue, newValue);
  }

  public static ArticleChange CONTAINER_DEPOSIT(double oldValue, double newValue) {
    return new ArticleChange(CONTAINER_DEPOSIT, oldValue, newValue);
  }

  public static ArticleChange CONTAINER_SIZE(double oldValue, double newValue) {
    return new ArticleChange(CONTAINER_SIZE, oldValue, newValue);
  }

  public String log() {
    String designation;
    switch (type) {
      case PRICE -> designation = "price";
      case SINGLE_DEPOSIT -> designation = "single deposit";
      case CONTAINER_DEPOSIT -> designation = "container deposit";
      case CONTAINER_SIZE -> designation = "container size";
      default -> designation = "";
    }

    return "%s change [%.2f -> %.2f] -".formatted(designation, oldValue, newValue);
  }

  @Override
  public int compareTo(@NotNull Object o) {
    if (!(o instanceof ArticleChange other)) {
      throw new ClassCastException();
    }
    int result = Integer.compare(getType(), other.getType());
    if (result != 0) {
      return result;
    }
    result = Double.compare(oldValue, other.getOldValue());
    if (result != 0) {
      return result;
    }
    return Double.compare(newValue, other.getNewValue());
  }

  @Override
  public boolean equals(Object o) throws ClassCastException {
    if (!(o instanceof ArticleChange other)) {
      throw new ClassCastException();
    }
    return type == other.type && oldValue == other.oldValue && newValue == other.newValue;
  }

  @Override
  public int hashCode() {
    return ((Integer) type).hashCode()
        + ((Double) oldValue).hashCode()
        + ((Double) newValue).hashCode();
  }
}
