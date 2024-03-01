package kernbeisser.Enums;

import java.util.function.Supplier;
import kernbeisser.Useful.Named;

public enum VAT implements Named {
  LOW("Niedrig (%f%%)", Setting.VAT_LOW::getDoubleValue),
  HIGH("Hoch (%f%%)", Setting.VAT_HIGH::getDoubleValue);

  private final String name;
  private Double lazy_value;

  private final Supplier<Double> doubleSupplier;

  VAT(String name, Supplier<Double> doubleSupplier) {
    this.name = name;
    this.doubleSupplier = doubleSupplier;
  }

  public double getValue() {
    if (lazy_value == null) {
      lazy_value = doubleSupplier.get();
    }
    return lazy_value;
  }

  @Override
  public String toString() {
    return getName();
  }

  @Override
  public String getName() {
    return name.formatted(getValue());
  }
}
