package kernbeisser.Enums;

import kernbeisser.Useful.Named;

public enum Unit implements Named {
    GRAM("Gramm","g"),
    KILOGRAM("Kilogramm","kg"),
    LITER("Liter","l"),
    MILLILITER("Mililiter","ml"),
    STACK("Stück","stück"),
    NONE("Undefinierte-Einheit","?");

    private final String shortName;

    private final String name;

    Unit(String s,String shortName){
        this.name=s;
        this.shortName=shortName;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public String toString() {
        return name;
    }

    public String getShortName() {
        return shortName;
    }
}

