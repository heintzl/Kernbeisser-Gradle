package kernbeisser.DBEntities.TypeFields;

import kernbeisser.DBConnection.FieldIdentifier;

public class SystemSettingField {
  public static FieldIdentifier<kernbeisser.DBEntities.SystemSetting, Integer> id =
      new FieldIdentifier<>(kernbeisser.DBEntities.SystemSetting.class, Integer.class, "id");
  public static FieldIdentifier<kernbeisser.DBEntities.SystemSetting, java.lang.String> setting =
      new FieldIdentifier<>(
          kernbeisser.DBEntities.SystemSetting.class, java.lang.String.class, "setting");
  public static FieldIdentifier<kernbeisser.DBEntities.SystemSetting, java.lang.String> value =
      new FieldIdentifier<>(
          kernbeisser.DBEntities.SystemSetting.class, java.lang.String.class, "value");
}
