package kernbeisser.Exeptions;

public class InvalidVATValueException extends Exception {
  public InvalidVATValueException(double vat) {
    super(vat * 100 + "% is no valid VAT rate");
  }
}
