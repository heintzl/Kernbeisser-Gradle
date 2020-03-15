package kernbeisser.Enums;

import kernbeisser.Useful.Named;

public enum VAT implements Named {
    LOW("Niedrig(7%)", 0.07),
    HIGH("Hoch(19%)", 0.19);

    private final String name;
    private final double value;

    VAT(String name, double value) {
        this.name = name;
        this.value = value;
    }

    public double getValue() {
        return value;
    }

    @Override
    public String toString() {
        return getName();
    }

    @Override
    public String getName() {
        return name;
    }
}