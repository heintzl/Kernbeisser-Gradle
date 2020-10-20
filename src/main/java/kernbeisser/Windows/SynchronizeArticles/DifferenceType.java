package kernbeisser.Windows.SynchronizeArticles;

public enum DifferenceType {
  PRICE("Preis"),
  CONTAINER_SIZE("Gebinde-Größe"),
  DEPOSIT("Pfand"),
  CONTAINER_DEPOSIT("Kister-Pfand"),
  AMOUNT("Menge");
  private final String name;

  DifferenceType(String displayName) {
    this.name = displayName;
  }

  @Override
  public String toString() {
    return name;
  }
}
