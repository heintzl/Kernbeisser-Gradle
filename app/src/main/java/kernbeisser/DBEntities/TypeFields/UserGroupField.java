package kernbeisser.DBEntities.TypeFields;

import kernbeisser.DBConnection.FieldIdentifier;

public class UserGroupField {
  public static FieldIdentifier<kernbeisser.DBEntities.UserGroup, Integer> id =
      new FieldIdentifier<>(kernbeisser.DBEntities.UserGroup.class, Integer.class, "id");
  public static FieldIdentifier<kernbeisser.DBEntities.UserGroup, Double> value =
      new FieldIdentifier<>(kernbeisser.DBEntities.UserGroup.class, Double.class, "value");
  public static FieldIdentifier<kernbeisser.DBEntities.UserGroup, Double> interestThisYear =
      new FieldIdentifier<>(
          kernbeisser.DBEntities.UserGroup.class, Double.class, "interestThisYear");
  public static FieldIdentifier<kernbeisser.DBEntities.UserGroup, java.time.Instant> updateDate =
      new FieldIdentifier<>(
          kernbeisser.DBEntities.UserGroup.class, java.time.Instant.class, "updateDate");
  public static FieldIdentifier<kernbeisser.DBEntities.UserGroup, kernbeisser.DBEntities.User>
      updateBy =
          new FieldIdentifier<>(
              kernbeisser.DBEntities.UserGroup.class,
              kernbeisser.DBEntities.User.class,
              "updateBy");
  public static FieldIdentifier<kernbeisser.DBEntities.UserGroup, java.lang.String>
      membersAsString =
          new FieldIdentifier<>(
              kernbeisser.DBEntities.UserGroup.class, java.lang.String.class, "membersAsString");
  public static FieldIdentifier<kernbeisser.DBEntities.UserGroup, java.lang.Double> transactionSum =
      new FieldIdentifier<>(
          kernbeisser.DBEntities.UserGroup.class, java.lang.Double.class, "transactionSum");
  public static FieldIdentifier<kernbeisser.DBEntities.UserGroup, Double> solidaritySurcharge =
      new FieldIdentifier<>(
          kernbeisser.DBEntities.UserGroup.class, Double.class, "solidaritySurcharge");
  public static FieldIdentifier<kernbeisser.DBEntities.UserGroup, java.lang.Double> oldSolidarity =
      new FieldIdentifier<>(
          kernbeisser.DBEntities.UserGroup.class, java.lang.Double.class, "oldSolidarity");
}
