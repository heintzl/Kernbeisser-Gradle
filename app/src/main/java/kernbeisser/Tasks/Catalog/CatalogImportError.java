package kernbeisser.Tasks.Catalog;

import lombok.Getter;

public class CatalogImportError {
  @Getter final int lineNumber;
  @Getter final Exception e;

  public CatalogImportError(int lineNumber, Exception e) {
    this.lineNumber = lineNumber;
    this.e = e;
  }
}
