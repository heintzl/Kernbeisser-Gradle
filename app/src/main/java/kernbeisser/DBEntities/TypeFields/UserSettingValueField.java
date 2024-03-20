package kernbeisser.DBEntities.TypeFields;

import kernbeisser.DBConnection.FieldIdentifier;

public class UserSettingValueField {
public static FieldIdentifier<kernbeisser.DBEntities.UserSettingValue,Integer> id = new FieldIdentifier<>(kernbeisser.DBEntities.UserSettingValue.class, Integer.class, "id");
public static FieldIdentifier<kernbeisser.DBEntities.UserSettingValue,kernbeisser.DBEntities.User> user = new FieldIdentifier<>(kernbeisser.DBEntities.UserSettingValue.class, kernbeisser.DBEntities.User.class, "user");
public static FieldIdentifier<kernbeisser.DBEntities.UserSettingValue,kernbeisser.Enums.UserSetting> userSetting = new FieldIdentifier<>(kernbeisser.DBEntities.UserSettingValue.class, kernbeisser.Enums.UserSetting.class, "userSetting");
public static FieldIdentifier<kernbeisser.DBEntities.UserSettingValue,java.lang.String> value = new FieldIdentifier<>(kernbeisser.DBEntities.UserSettingValue.class, java.lang.String.class, "value");

}