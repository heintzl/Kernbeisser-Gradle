package kernbeisser.DBEntities.TypeFields;

import kernbeisser.DBConnection.FieldIdentifier;
import kernbeisser.DBEntities.*;

import java.time.Instant;
import java.util.Set;

public class UserField {
public static FieldIdentifier<User,Integer> id = new FieldIdentifier<>(User.class, Integer.class, "id");
public static FieldIdentifier<User,Set> permissions = new FieldIdentifier<>(User.class, Set.class, "permissions");
public static FieldIdentifier<User,Integer> shares = new FieldIdentifier<>(User.class, Integer.class, "shares");
public static FieldIdentifier<User,Set> jobs = new FieldIdentifier<>(User.class, Set.class, "jobs");
public static FieldIdentifier<User,Integer> kernbeisserKey = new FieldIdentifier<>(User.class, Integer.class, "kernbeisserKey");
public static FieldIdentifier<User,Boolean> employee = new FieldIdentifier<>(User.class, Boolean.class, "employee");
public static FieldIdentifier<User,String> username = new FieldIdentifier<>(User.class, String.class, "username");
public static FieldIdentifier<User,String> password = new FieldIdentifier<>(User.class, String.class, "password");
public static FieldIdentifier<User,String> firstName = new FieldIdentifier<>(User.class, String.class, "firstName");
public static FieldIdentifier<User,String> surname = new FieldIdentifier<>(User.class, String.class, "surname");
public static FieldIdentifier<User,String> phoneNumber1 = new FieldIdentifier<>(User.class, String.class, "phoneNumber1");
public static FieldIdentifier<User,String> phoneNumber2 = new FieldIdentifier<>(User.class, String.class, "phoneNumber2");
public static FieldIdentifier<User,String> street = new FieldIdentifier<>(User.class, String.class, "street");
public static FieldIdentifier<User,String> town = new FieldIdentifier<>(User.class, String.class, "town");
public static FieldIdentifier<User,String> townCode = new FieldIdentifier<>(User.class, String.class, "townCode");
public static FieldIdentifier<User,String> email = new FieldIdentifier<>(User.class, String.class, "email");
public static FieldIdentifier<User,Instant> createDate = new FieldIdentifier<>(User.class, Instant.class, "createDate");
public static FieldIdentifier<User,Instant> updateDate = new FieldIdentifier<>(User.class, Instant.class, "updateDate");
public static FieldIdentifier<User,User> updateBy = new FieldIdentifier<>(User.class, User.class, "updateBy");
public static FieldIdentifier<User,UserGroup> userGroup = new FieldIdentifier<>(User.class, UserGroup.class, "userGroup");
public static FieldIdentifier<User,Boolean> unreadable = new FieldIdentifier<>(User.class, Boolean.class, "unreadable");
public static FieldIdentifier<User,Instant> lastPasswordChange = new FieldIdentifier<>(User.class, Instant.class, "lastPasswordChange");
public static FieldIdentifier<User,Boolean> forcePasswordChange = new FieldIdentifier<>(User.class, Boolean.class, "forcePasswordChange");
public static FieldIdentifier<User,Boolean> active = new FieldIdentifier<>(User.class, Boolean.class, "active");
public static FieldIdentifier<User,Boolean> testOnly = new FieldIdentifier<>(User.class, Boolean.class, "testOnly");
public static FieldIdentifier<User,Boolean> primary = new FieldIdentifier<>(User.class, Boolean.class, "primary");
public static FieldIdentifier<User,Boolean> setUpdatedBy = new FieldIdentifier<>(User.class, Boolean.class, "setUpdatedBy");

}