package kernbeisser.DBEntities.TypeFields;

import kernbeisser.DBConnection.FieldIdentifier;

public class SettingValueField {
public static FieldIdentifier<kernbeisser.DBEntities.SettingValue,Integer> id = new FieldIdentifier<>(kernbeisser.DBEntities.SettingValue.class, Integer.class, "id");
public static FieldIdentifier<kernbeisser.DBEntities.SettingValue,kernbeisser.Enums.Setting> setting = new FieldIdentifier<>(kernbeisser.DBEntities.SettingValue.class, kernbeisser.Enums.Setting.class, "setting");
public static FieldIdentifier<kernbeisser.DBEntities.SettingValue,java.lang.String> value = new FieldIdentifier<>(kernbeisser.DBEntities.SettingValue.class, java.lang.String.class, "value");

}