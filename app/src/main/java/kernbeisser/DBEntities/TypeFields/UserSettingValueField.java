package kernbeisser.DBEntities.TypeFields;

import kernbeisser.DBConnection.FieldIdentifier;
import kernbeisser.DBEntities.*;
import kernbeisser.Enums.UserSetting;

public class UserSettingValueField {
public static FieldIdentifier<UserSettingValue,Integer> id = new FieldIdentifier<>(UserSettingValue.class, Integer.class, "id");
public static FieldIdentifier<UserSettingValue,User> user = new FieldIdentifier<>(UserSettingValue.class, User.class, "user");
public static FieldIdentifier<UserSettingValue, UserSetting> userSetting = new FieldIdentifier<>(UserSettingValue.class, UserSetting.class, "userSetting");
public static FieldIdentifier<UserSettingValue,String> value = new FieldIdentifier<>(UserSettingValue.class, String.class, "value");

}