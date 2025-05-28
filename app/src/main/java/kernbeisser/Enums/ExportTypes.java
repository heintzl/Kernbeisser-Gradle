package kernbeisser.Enums;

import kernbeisser.Useful.Named;
import lombok.Getter;

public enum ExportTypes implements Named {
  PRINT("Ausdruck", null),
  PDF("PDF-Datei", "pdf"),
  CLOUD("PDF in Cloud-Verzeichnis", "pdf"),
  JSON("JSON-Datei", "json"),
  CSV("CSV-Datei", "csv");

  @Getter private final String name;
  @Getter private final String fileNameExtension;

  ExportTypes(String name, String fileNameExtension) {
    this.name = name;
    this.fileNameExtension = fileNameExtension;
  }

  @Override
  public String toString() {
    return this.getName();
  }
}
