package kernbeisser.Enums;

import kernbeisser.Useful.Named;

public enum Unit implements Named {
    GRAM("Gramm", "g") {},
    KILOGRAM("Kilogramm", "kg") {
        @Override
        public int toUnit(double v) {
            return (int) (v * 1000);
        }

        @Override
        public double fromUnit(int amount) {
            return amount / 1000f;
        }
    },
    LITER("Liter", "l") {
        @Override
        public int toUnit(double v) {
            return (int) (v * 1000);
        }

        @Override
        public double fromUnit(int amount) {
            return amount / 1000f;
        }
    },
    MILLILITER("Mililiter", "ml"),
    STACK("Stück", "stk"),
    NONE("Undefinierte-Einheit", "?");

    private final String shortName;

    private final String name;

    Unit(String s, String shortName) {
        this.name = s;
        this.shortName = shortName;
    }

    public int toUnit(double v) {
        return (int) (v + 0.5);
    }

    public double fromUnit(int amount) {
        return amount;
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

