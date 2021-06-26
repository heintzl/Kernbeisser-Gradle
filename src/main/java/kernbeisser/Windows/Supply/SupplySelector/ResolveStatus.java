package kernbeisser.Windows.Supply.SupplySelector;

public enum ResolveStatus {
  PRODUCE("Obst & Gemüse"),
  IGNORE("Konnten nicht geliefert werden"),
  ADDED("Artikel die neu aufgenommen werden"),
  OK("Artikel die bereits in Ladenbestand sind");
  final String name;

  ResolveStatus(String name) {
    this.name = name;
  }

  @Override
  public String toString() {
    return name;
  }
}
