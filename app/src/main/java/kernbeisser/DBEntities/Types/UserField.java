package kernbeisser.DBEntities.Types;

import java.time.Instant;
import java.util.Set;
import kernbeisser.DBConnection.FieldIdentifier;
import kernbeisser.DBEntities.*;

public class UserField {
  public static FieldIdentifier<User, Integer> id = new FieldIdentifier<>(User.class, "id");
  public static FieldIdentifier<User, Set<Permission>> permissions =
      new FieldIdentifier<>(User.class, "permissions");
  public static FieldIdentifier<User, Integer> shares = new FieldIdentifier<>(User.class, "shares");
  public static FieldIdentifier<User, Set<Job>> jobs = new FieldIdentifier<>(User.class, "jobs");
  public static FieldIdentifier<User, Integer> kernbeisserKey =
      new FieldIdentifier<>(User.class, "kernbeisserKey");
  public static FieldIdentifier<User, Boolean> employee =
      new FieldIdentifier<>(User.class, "employee");
  public static FieldIdentifier<User, String> username =
      new FieldIdentifier<>(User.class, "username");
  public static FieldIdentifier<User, String> password =
      new FieldIdentifier<>(User.class, "password");
  public static FieldIdentifier<User, String> firstName =
      new FieldIdentifier<>(User.class, "firstName");
  public static FieldIdentifier<User, String> surname =
      new FieldIdentifier<>(User.class, "surname");
  public static FieldIdentifier<User, String> phoneNumber1 =
      new FieldIdentifier<>(User.class, "phoneNumber1");
  public static FieldIdentifier<User, String> phoneNumber2 =
      new FieldIdentifier<>(User.class, "phoneNumber2");
  public static FieldIdentifier<User, String> street = new FieldIdentifier<>(User.class, "street");
  public static FieldIdentifier<User, String> town = new FieldIdentifier<>(User.class, "town");
  public static FieldIdentifier<User, String> townCode =
      new FieldIdentifier<>(User.class, "townCode");
  public static FieldIdentifier<User, String> email = new FieldIdentifier<>(User.class, "email");
  public static FieldIdentifier<User, Instant> createDate =
      new FieldIdentifier<>(User.class, "createDate");
  public static FieldIdentifier<User, Instant> updateDate =
      new FieldIdentifier<>(User.class, "updateDate");
  public static FieldIdentifier<User, User> updateBy =
      new FieldIdentifier<>(User.class, "updateBy");
  public static FieldIdentifier<User, UserGroup> userGroup =
      new FieldIdentifier<>(User.class, "userGroup");
  public static FieldIdentifier<User, Boolean> unreadable =
      new FieldIdentifier<>(User.class, "unreadable");
  public static FieldIdentifier<User, Instant> lastPasswordChange =
      new FieldIdentifier<>(User.class, "lastPasswordChange");
  public static FieldIdentifier<User, Boolean> forcePasswordChange =
      new FieldIdentifier<>(User.class, "forcePasswordChange");
  public static FieldIdentifier<User, Boolean> active = new FieldIdentifier<>(User.class, "active");
  public static FieldIdentifier<User, Boolean> testOnly =
      new FieldIdentifier<>(User.class, "testOnly");
  public static FieldIdentifier<User, Boolean> primary =
      new FieldIdentifier<>(User.class, "primary");
  public static FieldIdentifier<User, Boolean> setUpdatedBy =
      new FieldIdentifier<>(User.class, "setUpdatedBy");
}
