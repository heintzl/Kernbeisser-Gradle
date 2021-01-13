package kernbeisser.Enums;

public enum ShopRange {
  NOT_IN_RANGE("Nicht im Sortiment"),
  IN_RANGE("Im Sortiment"),
  PERMANENT_RANGE("Dauersortiment");

  private final String name;

  ShopRange(String name) {
    this.name = name;
  }

  @Override
  public String toString() {
    return name;
  }
}
