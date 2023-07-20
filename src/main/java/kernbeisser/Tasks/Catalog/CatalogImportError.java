package kernbeisser.Tasks.Catalog;

import lombok.Getter;

public class CatalogImportError {
  @Getter int lineNumber;
  @Getter String field;
  @Getter Exception e;

  public CatalogImportError(int lineNumber, String field, Exception e) {
    this.lineNumber = lineNumber;
    this.field = field;
    this.e = e;
  }
}
