package kernbeisser.Enums;

import kernbeisser.Exeptions.CannotParseException;
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


    public static MetricUnits fromString(String source) {
        if (source.toUpperCase().contains("L")) {
            return MetricUnits.LITER;
        } else if (source.toUpperCase().contains("ML")) {
            return MetricUnits.MILLILITER;
        } else if (source.toUpperCase().contains("KG")) {
            return MetricUnits.KILOGRAM;
        } else if (source.toUpperCase().contains("G")) {
            return MetricUnits.GRAM;
        }
        if(source.toUpperCase().contains("ST"))return MetricUnits.PIECE;
        for (MetricUnits value : MetricUnits.values()) {
            if(value.getShortName().equals(source)|| value.getName().equals(source))return value;
        }
        return MetricUnits.NONE;
    }
}

