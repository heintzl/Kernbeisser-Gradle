package kernbeisser.Tasks.Catalog;

import lombok.Getter;

public class CatalogImportError {
  @Getter final String lineNumber;
  @Getter final Exception e;

  public CatalogImportError(String lineNumber, Exception e) {
    this.lineNumber = lineNumber;
    this.e = e;
  }
}
