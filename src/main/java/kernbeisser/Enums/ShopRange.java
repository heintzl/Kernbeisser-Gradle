package kernbeisser.Enums;

import lombok.Getter;

public enum ShopRange {
  NOT_IN_RANGE("Nicht im Sortiment", false),
  IN_RANGE("Im Sortiment", true),
  PERMANENT_RANGE("Dauersortiment", true);

  private final String name;

  @Getter private final boolean visible;

  ShopRange(String name, boolean visible) {
    this.name = name;
    this.visible = visible;
  }

  @Override
  public String toString() {
    return name;
  }
}
