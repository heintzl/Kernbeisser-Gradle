package kernbeisser.Enums;

import java.util.Arrays;
import java.util.Collection;
import lombok.Getter;

public enum ShopRange {
  NOT_IN_RANGE("Nicht im Ladensortiment", false),
  IN_RANGE("Ladensortiment(Temporär)", true),
  PERMANENT_RANGE("Ladensortiment(Dauerhaft)", true);

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

  public static Collection<ShopRange> visibleRanges() {
    return Arrays.stream(ShopRange.values()).filter(ShopRange::isVisible).toList();
  }
}
