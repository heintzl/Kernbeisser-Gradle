package kernbeisser.DBEntities.TypeFields;

import kernbeisser.DBConnection.FieldIdentifier;

public class SurchargeGroupField {
  public static FieldIdentifier<kernbeisser.DBEntities.SurchargeGroup, Integer> id =
      new FieldIdentifier<>(kernbeisser.DBEntities.SurchargeGroup.class, Integer.class, "id");
  public static FieldIdentifier<kernbeisser.DBEntities.SurchargeGroup, java.lang.Double> surcharge =
      new FieldIdentifier<>(
          kernbeisser.DBEntities.SurchargeGroup.class, java.lang.Double.class, "surcharge");
  public static FieldIdentifier<kernbeisser.DBEntities.SurchargeGroup, java.lang.String> name =
      new FieldIdentifier<>(
          kernbeisser.DBEntities.SurchargeGroup.class, java.lang.String.class, "name");
  public static FieldIdentifier<
          kernbeisser.DBEntities.SurchargeGroup, kernbeisser.DBEntities.Supplier>
      supplier =
          new FieldIdentifier<>(
              kernbeisser.DBEntities.SurchargeGroup.class,
              kernbeisser.DBEntities.Supplier.class,
              "supplier");
  public static FieldIdentifier<
          kernbeisser.DBEntities.SurchargeGroup, kernbeisser.DBEntities.SurchargeGroup>
      parent =
          new FieldIdentifier<>(
              kernbeisser.DBEntities.SurchargeGroup.class,
              kernbeisser.DBEntities.SurchargeGroup.class,
              "parent");
}
