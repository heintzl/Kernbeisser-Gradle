package kernbeisser.Enums;

public enum Permission {
    STANDARD("Standart"),
    ADMIN("Admin"),
    MANAGER("Ladendienst"),
    MONEY_MANAGER("Geldbeauftagte*r"),
    BEGINNER("Neuling");

    private final String name;

    Permission(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
