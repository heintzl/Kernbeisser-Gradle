package kernbeisser.Enums;

import kernbeisser.DBConnection.DBConnection;
import kernbeisser.DBEntities.VATConstant;
import kernbeisser.Useful.Named;

import javax.persistence.EntityManager;

public enum VAT implements Named {
    LOW("Niedrig(7%)"){
        @Override
        public double getValue() {
            return VATConstant.getLow().getValue();
        }
    },
    HIGH("Hoch(19%)"){
        @Override
        public double getValue() {
            return VATConstant.getHigh().getValue();
        }
    };

    private final String name;

    VAT(String name) {
        this.name = name;
    }

    public double getValue(){
        return -1;
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