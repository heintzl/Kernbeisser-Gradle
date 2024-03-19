package kernbeisser.DBEntities.Types;

import java.time.Instant;
import kernbeisser.DBConnection.FieldIdentifier;
import kernbeisser.DBEntities.*;

public class UserGroupField {
  public static FieldIdentifier<UserGroup, Integer> id =
      new FieldIdentifier<>(UserGroup.class, "id");
  public static FieldIdentifier<UserGroup, Double> value =
      new FieldIdentifier<>(UserGroup.class, "value");
  public static FieldIdentifier<UserGroup, Double> interestThisYear =
      new FieldIdentifier<>(UserGroup.class, "interestThisYear");
  public static FieldIdentifier<UserGroup, Instant> updateDate =
      new FieldIdentifier<>(UserGroup.class, "updateDate");
  public static FieldIdentifier<UserGroup, User> updateBy =
      new FieldIdentifier<>(UserGroup.class, "updateBy");
  public static FieldIdentifier<UserGroup, String> membersAsString =
      new FieldIdentifier<>(UserGroup.class, "membersAsString");
  public static FieldIdentifier<UserGroup, Double> transactionSum =
      new FieldIdentifier<>(UserGroup.class, "transactionSum");
  public static FieldIdentifier<UserGroup, Double> solidaritySurcharge =
      new FieldIdentifier<>(UserGroup.class, "solidaritySurcharge");
  public static FieldIdentifier<UserGroup, Double> oldSolidarity =
      new FieldIdentifier<>(UserGroup.class, "oldSolidarity");
}
