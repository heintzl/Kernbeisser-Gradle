package kernbeisser.DBEntities.TypeFields;

import kernbeisser.DBConnection.FieldIdentifier;
import kernbeisser.DBEntities.*;

public class SurchargeGroupField {
public static FieldIdentifier<SurchargeGroup,Integer> id = new FieldIdentifier<>(SurchargeGroup.class, Integer.class, "id");
public static FieldIdentifier<SurchargeGroup,Double> surcharge = new FieldIdentifier<>(SurchargeGroup.class, Double.class, "surcharge");
public static FieldIdentifier<SurchargeGroup,String> name = new FieldIdentifier<>(SurchargeGroup.class, String.class, "name");
public static FieldIdentifier<SurchargeGroup,Supplier> supplier = new FieldIdentifier<>(SurchargeGroup.class, Supplier.class, "supplier");
public static FieldIdentifier<SurchargeGroup,SurchargeGroup> parent = new FieldIdentifier<>(SurchargeGroup.class, SurchargeGroup.class, "parent");

}