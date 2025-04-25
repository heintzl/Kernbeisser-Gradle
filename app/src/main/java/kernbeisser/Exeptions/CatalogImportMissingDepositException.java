package kernbeisser.Exeptions;

import lombok.Getter;

public class CatalogImportMissingDepositException extends CatalogImportCriticalErrorException {

  @Getter private final String depositArtNo;

  public CatalogImportMissingDepositException(String message, String depositArtNo) {
    super(message);
    this.depositArtNo = depositArtNo;
  }
}
