package kernbeisser.DBEntities.TypeFields;

import kernbeisser.DBConnection.FieldIdentifier;

public class UserField {
public static FieldIdentifier<kernbeisser.DBEntities.User,Integer> id = new FieldIdentifier<>(kernbeisser.DBEntities.User.class, Integer.class, "id");
public static FieldIdentifier<kernbeisser.DBEntities.User,java.util.Set> permissions = new FieldIdentifier<>(kernbeisser.DBEntities.User.class, java.util.Set.class, "permissions");
public static FieldIdentifier<kernbeisser.DBEntities.User,Integer> shares = new FieldIdentifier<>(kernbeisser.DBEntities.User.class, Integer.class, "shares");
public static FieldIdentifier<kernbeisser.DBEntities.User,java.util.Set> jobs = new FieldIdentifier<>(kernbeisser.DBEntities.User.class, java.util.Set.class, "jobs");
public static FieldIdentifier<kernbeisser.DBEntities.User,Integer> kernbeisserKey = new FieldIdentifier<>(kernbeisser.DBEntities.User.class, Integer.class, "kernbeisserKey");
public static FieldIdentifier<kernbeisser.DBEntities.User,Boolean> employee = new FieldIdentifier<>(kernbeisser.DBEntities.User.class, Boolean.class, "employee");
public static FieldIdentifier<kernbeisser.DBEntities.User,java.lang.String> username = new FieldIdentifier<>(kernbeisser.DBEntities.User.class, java.lang.String.class, "username");
public static FieldIdentifier<kernbeisser.DBEntities.User,java.lang.String> password = new FieldIdentifier<>(kernbeisser.DBEntities.User.class, java.lang.String.class, "password");
public static FieldIdentifier<kernbeisser.DBEntities.User,java.lang.String> firstName = new FieldIdentifier<>(kernbeisser.DBEntities.User.class, java.lang.String.class, "firstName");
public static FieldIdentifier<kernbeisser.DBEntities.User,java.lang.String> surname = new FieldIdentifier<>(kernbeisser.DBEntities.User.class, java.lang.String.class, "surname");
public static FieldIdentifier<kernbeisser.DBEntities.User,java.lang.String> phoneNumber1 = new FieldIdentifier<>(kernbeisser.DBEntities.User.class, java.lang.String.class, "phoneNumber1");
public static FieldIdentifier<kernbeisser.DBEntities.User,java.lang.String> phoneNumber2 = new FieldIdentifier<>(kernbeisser.DBEntities.User.class, java.lang.String.class, "phoneNumber2");
public static FieldIdentifier<kernbeisser.DBEntities.User,java.lang.String> street = new FieldIdentifier<>(kernbeisser.DBEntities.User.class, java.lang.String.class, "street");
public static FieldIdentifier<kernbeisser.DBEntities.User,java.lang.String> town = new FieldIdentifier<>(kernbeisser.DBEntities.User.class, java.lang.String.class, "town");
public static FieldIdentifier<kernbeisser.DBEntities.User,java.lang.String> townCode = new FieldIdentifier<>(kernbeisser.DBEntities.User.class, java.lang.String.class, "townCode");
public static FieldIdentifier<kernbeisser.DBEntities.User,java.lang.String> email = new FieldIdentifier<>(kernbeisser.DBEntities.User.class, java.lang.String.class, "email");
public static FieldIdentifier<kernbeisser.DBEntities.User,java.time.Instant> createDate = new FieldIdentifier<>(kernbeisser.DBEntities.User.class, java.time.Instant.class, "createDate");
public static FieldIdentifier<kernbeisser.DBEntities.User,java.time.Instant> updateDate = new FieldIdentifier<>(kernbeisser.DBEntities.User.class, java.time.Instant.class, "updateDate");
public static FieldIdentifier<kernbeisser.DBEntities.User,kernbeisser.DBEntities.User> updateBy = new FieldIdentifier<>(kernbeisser.DBEntities.User.class, kernbeisser.DBEntities.User.class, "updateBy");
public static FieldIdentifier<kernbeisser.DBEntities.User,kernbeisser.DBEntities.UserGroup> userGroup = new FieldIdentifier<>(kernbeisser.DBEntities.User.class, kernbeisser.DBEntities.UserGroup.class, "userGroup");
public static FieldIdentifier<kernbeisser.DBEntities.User,Boolean> unreadable = new FieldIdentifier<>(kernbeisser.DBEntities.User.class, Boolean.class, "unreadable");
public static FieldIdentifier<kernbeisser.DBEntities.User,java.time.Instant> lastPasswordChange = new FieldIdentifier<>(kernbeisser.DBEntities.User.class, java.time.Instant.class, "lastPasswordChange");
public static FieldIdentifier<kernbeisser.DBEntities.User,Boolean> forcePasswordChange = new FieldIdentifier<>(kernbeisser.DBEntities.User.class, Boolean.class, "forcePasswordChange");
public static FieldIdentifier<kernbeisser.DBEntities.User,Boolean> active = new FieldIdentifier<>(kernbeisser.DBEntities.User.class, Boolean.class, "active");
public static FieldIdentifier<kernbeisser.DBEntities.User,Boolean> testOnly = new FieldIdentifier<>(kernbeisser.DBEntities.User.class, Boolean.class, "testOnly");
public static FieldIdentifier<kernbeisser.DBEntities.User,Boolean> primary = new FieldIdentifier<>(kernbeisser.DBEntities.User.class, Boolean.class, "primary");
public static FieldIdentifier<kernbeisser.DBEntities.User,Boolean> setUpdatedBy = new FieldIdentifier<>(kernbeisser.DBEntities.User.class, Boolean.class, "setUpdatedBy");
public static FieldIdentifier<kernbeisser.DBEntities.User,java.util.concurrent.atomic.AtomicReference> ignoredDialogs = new FieldIdentifier<>(kernbeisser.DBEntities.User.class, java.util.concurrent.atomic.AtomicReference.class, "ignoredDialogs");

}