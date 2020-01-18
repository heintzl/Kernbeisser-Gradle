package kernbeisser.Enums;

import kernbeisser.Useful.Named;

public enum Unit implements Named {
    GRAM("Gramm"),
    KILOGRAM("Kilogramm"),
    LITER("Liter"),
    MILLILITER("Mililiter"),
    STACK("Stück"),
    NONE("Undefinierte-Einheit");

    private final String name;

    Unit(String s){
        this.name=s;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public String toString() {
        return name;
    }
}

