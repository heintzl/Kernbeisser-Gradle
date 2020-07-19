package kernbeisser.Enums;

import kernbeisser.Useful.Named;

public enum Repeat implements Named {
    EVERY_YEAR("Jedes Jahr"),
    NONE("Einmalig");
    private final String name;

    Repeat(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public String getName() {
        return name;
    }
}
