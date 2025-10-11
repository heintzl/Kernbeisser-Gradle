package kernbeisser.Enums;

import kernbeisser.Useful.Named;
import lombok.Getter;

public enum PreOrderCreator implements Named {
  PRE_ORDER_MANAGER("Bestelldienst"),
  SELF("selbst"),
  ONLINE("online"),
  POS("LD");

  @Getter private final String name;

  PreOrderCreator(String name) {
    this.name = name;
  }

  @Override
  public String toString() {
    return this.getName();
  }
}
