package kernbeisser.DBEntities.Types;

import kernbeisser.DBConnection.FieldIdentifier;
import kernbeisser.DBEntities.*;
import kernbeisser.Enums.UserSetting;

public class UserSettingValueField {
  public static FieldIdentifier<UserSettingValue, Integer> id =
      new FieldIdentifier<>(UserSettingValue.class, "id");
  public static FieldIdentifier<UserSettingValue, User> user =
      new FieldIdentifier<>(UserSettingValue.class, "user");
  public static FieldIdentifier<UserSettingValue, UserSetting> userSetting =
      new FieldIdentifier<>(UserSettingValue.class, "userSetting");
  public static FieldIdentifier<UserSettingValue, String> value =
      new FieldIdentifier<>(UserSettingValue.class, "value");
}
