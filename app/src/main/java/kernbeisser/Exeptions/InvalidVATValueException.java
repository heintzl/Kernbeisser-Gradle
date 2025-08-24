package kernbeisser.Exeptions;

import kernbeisser.Enums.VAT;

public class InvalidVATValueException extends Exception {
  public InvalidVATValueException(VAT vat, double value) {
    super("%f.2 is no valid VAT rate for %s".formatted(value * 100, vat.getName()));
  }
}
