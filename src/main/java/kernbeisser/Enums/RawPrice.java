package kernbeisser.Enums;

import kernbeisser.Useful.Named;

public enum RawPrice implements Named {
    ORGANIC("Obst und Gemüse"),
    BACKER("Backware"),
    DEPOSIT("Pfand");

    private final String name;

    RawPrice(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return this.getName();
    }

    @Override
    public String getName() {
        return name;
    }
}
