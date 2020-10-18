package kernbeisser.Enums;

import kernbeisser.Useful.Named;

public enum ExportTypes implements Named {
  CSV("CSV-Datei"),
  JSON("JSON-Datei"),
  PDF("PDF-Datei"),
  PRINT("Ausdruck");

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
