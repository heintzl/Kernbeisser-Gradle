package kernbeisser.Enums;

import kernbeisser.Useful.Named;

public enum MetricUnits implements Named {
    GRAM("Gramm", "g") {},
    KILOGRAM("Kilogramm", "kg"){
        @Override
        public double getBaseFactor() {
            return 1;
        }
    },
    LITER("Liter", "l"){
        @Override
        public double getBaseFactor() {
            return 1;
        }
    },
    MILLILITER("Mililiter", "ml"),
    PIECE("Stück", "stk"){
        @Override
        public double getBaseFactor() {
            return 1;
        }
    },
    NONE("Undefinierte-Einheit", "?"){
        @Override
        public double getBaseFactor() {
            return 1;
        }
    };

    private final String shortName;

    private final String name;

    MetricUnits(String s, String shortName) {
        this.name = s;
        this.shortName = shortName;
    }

    public double getBaseFactor(){
        return 0.001;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }

    public String getShortName() {
        return shortName;
    }
}

