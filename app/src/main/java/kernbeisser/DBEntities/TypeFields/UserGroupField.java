package kernbeisser.DBEntities.TypeFields;

import kernbeisser.DBConnection.FieldIdentifier;
import kernbeisser.DBEntities.*;

import java.time.Instant;

public class UserGroupField {
public static FieldIdentifier<UserGroup,Integer> id = new FieldIdentifier<>(UserGroup.class, Integer.class, "id");
public static FieldIdentifier<UserGroup,Double> value = new FieldIdentifier<>(UserGroup.class, Double.class, "value");
public static FieldIdentifier<UserGroup,Double> interestThisYear = new FieldIdentifier<>(UserGroup.class, Double.class, "interestThisYear");
public static FieldIdentifier<UserGroup,Instant> updateDate = new FieldIdentifier<>(UserGroup.class, Instant.class, "updateDate");
public static FieldIdentifier<UserGroup,User> updateBy = new FieldIdentifier<>(UserGroup.class, User.class, "updateBy");
public static FieldIdentifier<UserGroup,String> membersAsString = new FieldIdentifier<>(UserGroup.class, String.class, "membersAsString");
public static FieldIdentifier<UserGroup,Double> transactionSum = new FieldIdentifier<>(UserGroup.class, Double.class, "transactionSum");
public static FieldIdentifier<UserGroup,Double> solidaritySurcharge = new FieldIdentifier<>(UserGroup.class, Double.class, "solidaritySurcharge");
public static FieldIdentifier<UserGroup,Double> oldSolidarity = new FieldIdentifier<>(UserGroup.class, Double.class, "oldSolidarity");

}