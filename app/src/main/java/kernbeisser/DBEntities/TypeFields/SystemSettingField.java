package kernbeisser.DBEntities.TypeFields;

import kernbeisser.DBConnection.FieldIdentifier;
import kernbeisser.DBEntities.*;

public class SystemSettingField {
public static FieldIdentifier<SystemSetting,Integer> id = new FieldIdentifier<>(SystemSetting.class, Integer.class, "id");
public static FieldIdentifier<SystemSetting,String> setting = new FieldIdentifier<>(SystemSetting.class, String.class, "setting");
public static FieldIdentifier<SystemSetting,String> value = new FieldIdentifier<>(SystemSetting.class, String.class, "value");

}