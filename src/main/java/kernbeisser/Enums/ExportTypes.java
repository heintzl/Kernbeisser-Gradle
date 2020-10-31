package kernbeisser.Enums;

import kernbeisser.Useful.Named;

public enum ExportTypes implements Named {
  PRINT("Ausdruck"),
  PDF("PDF-Datei"),
  JSON("JSON-Datei"),
  CSV("CSV-Datei");

  private final String name;

  ExportTypes(String name) {
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
