package kernbeisser.Enums;

import kernbeisser.Useful.Named;

public enum StatementType implements Named {
  FULL("Komplettauszug"),
  ANNUAL("Jahresauszug"),
  QUARTER("Quartalsauszug"),
  MONTH("Monatsauszug");

  private final String name;

  StatementType(String name) {
    this.name = name;
  }

  @Override
  public String toString() {
    return name;
  }

  @Override
  public String getName() {
    return null;
  }
}
