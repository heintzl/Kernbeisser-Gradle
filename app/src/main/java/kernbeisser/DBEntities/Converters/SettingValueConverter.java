package kernbeisser.DBEntities.Converters;

import jakarta.persistence.AttributeConverter;
import kernbeisser.Enums.Setting;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class SettingValueConverter implements AttributeConverter<Setting, String> {

  @Override
  public String convertToDatabaseColumn(Setting setting) {
    return setting.name();
  }

  @Override
  public Setting convertToEntityAttribute(String s) {
    try {
      return Setting.valueOf(s);
    } catch (IllegalArgumentException enumVariantNotFoundException) {
      log.warn(
          "Could not find enum variant of Setting for: "
              + s
              + " returning Setting::"
              + Setting.OUTDATED_SETTING.name());
      return Setting.OUTDATED_SETTING;
    }
  }
}
