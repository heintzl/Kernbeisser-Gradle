package kernbeisser.Enums;

import kernbeisser.Useful.Named;

public enum RawPrice implements Named {
  PRODUCE("Obst und Gemüse"),
  BAKERY("Backwaren"),
  DEPOSIT("Pfand"),
  ITEM_DEPOSIT("    > Einzelpfand"),
  CONTAINER_DEPOSIT("    > Gebindepfand"),
  SOLIDARITY("Soli");

  private final String name;

  RawPrice(String name) {
    this.name = name;
  }

  @Override
  public String toString() {
    return this.getName();
  }

  @Override
  public String getName() {
    return name;
  }
}
