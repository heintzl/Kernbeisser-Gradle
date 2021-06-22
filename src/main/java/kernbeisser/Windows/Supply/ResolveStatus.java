package kernbeisser.Windows.Supply;

public enum ResolveStatus {
  PRODUCE("Obst & Gemüse(Wird auch ignoriert!)"),
  IGNORE("Konnten nicht geliefert werden"),
  ADDED("Artikel die neu aufgenommen werden"),
  OK("Atikel die bereits in Ladenbestand sind");
  final String name;

  ResolveStatus(String name) {
    this.name = name;
  }

  @Override
  public String toString() {
    return name;
  }
}
