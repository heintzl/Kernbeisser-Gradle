package kernbeisser.Enums;

import kernbeisser.Useful.Named;

public enum VAT implements Named {
  LOW(
      "Niedrig (" + Math.round(Setting.VAT_LOW.getDoubleValue() * 100) + "%)",
      Setting.VAT_LOW.getDoubleValue()),
  HIGH(
      "Hoch (" + Math.round(Setting.VAT_HIGH.getDoubleValue() * 100) + "%)",
      Setting.VAT_HIGH.getDoubleValue());

  private final String name;
  private final double value;

  VAT(String name, double value) {
    this.name = name;
    this.value = value;
  }

  public double getValue() {
    return value;
  }

  @Override
  public String toString() {
    return getName();
  }

  @Override
  public String getName() {
    return name;
  }
}
