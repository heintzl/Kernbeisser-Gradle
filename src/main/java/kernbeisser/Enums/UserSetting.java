package kernbeisser.Enums;

import kernbeisser.DBEntities.User;
import kernbeisser.DBEntities.UserSettingValue;

public enum UserSetting {
    THEME("STANDARD")
    ;
    private final String defaultValue;
    UserSetting(String defaultValue){
        this.defaultValue = defaultValue;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public String getValue(User user){
        return UserSettingValue.getValueFor(user,this);
    }
}
