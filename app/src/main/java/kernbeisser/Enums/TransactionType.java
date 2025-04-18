package kernbeisser.Enums;

public enum TransactionType {
  PURCHASE("Einkauf"),
  USER_GENERATED("interne Überweisung"),
  INITIALIZE("Übertrag aus Vorversion"),
  PAYIN("Einzahlung"),
  GROUP_MERGE("Kontoübertrag bei Gruppenwechsel"),
  INFO("Information"),
  SHARED_CONTAINER("Teilgebinde");

  private final String name;

  TransactionType(String name) {
    this.name = name;
  }

  @Override
  public String toString() {
    return name;
  }
}
