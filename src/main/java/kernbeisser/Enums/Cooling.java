package kernbeisser.Enums;

import kernbeisser.Useful.Named;

public enum Cooling implements Named {
  COLD("Kühlschrank"),
  EXTRA_COLD("Gefrierer"),
  NONE("Keine Kühlung");

  private final String name;

  Cooling(String name) {
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
