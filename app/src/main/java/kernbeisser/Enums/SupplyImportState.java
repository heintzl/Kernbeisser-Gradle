package kernbeisser.Enums;

import kernbeisser.Useful.Named;

public enum SupplyImportState implements Named {
  ALL("Ja"),
  SOME("zum Teil"),
  NONE("Nein");
  private final String name;

  SupplyImportState(String name) {
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
