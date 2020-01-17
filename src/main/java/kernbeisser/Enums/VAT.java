package kernbeisser.Enums;

import kernbeisser.Useful.Named;

public enum VAT implements Named {
    LOW("Niedrig(7%)",7),
    HIGH("Hoch(19%)",19);

    private final String name;
    private final int value;

    VAT(String name,int value){
        this.name=name;
        this.value=value;
    }
    public int getValue(){
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