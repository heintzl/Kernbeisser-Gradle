package kernbeisser.DBEntities.Types;

import kernbeisser.DBConnection.FieldIdentifier;
import kernbeisser.DBEntities.*;
import kernbeisser.Enums.Setting;

public class SettingValueField {
  public static FieldIdentifier<SettingValue, Integer> id =
      new FieldIdentifier<>(SettingValue.class, "id");
  public static FieldIdentifier<SettingValue, Setting> setting =
      new FieldIdentifier<>(SettingValue.class, "setting");
  public static FieldIdentifier<SettingValue, String> value =
      new FieldIdentifier<>(SettingValue.class, "value");
}
