package kernbeisser.Enums;

import kernbeisser.DBEntities.User;
import kernbeisser.DBEntities.UserSettingValue;

public enum UserSetting {
  THEME("APPLICATION") {
    @Override
    public <T extends Enum<T>> T getEnumValue(Class<T> c, User user) {
      if (!c.equals(Theme.class)) {
        return super.getEnumValue(c, user);
      }
      if (UserSettingValue.getValueFor(user, this).equals("APPLICATION")) {
        return Setting.DEFAULT_THEME.getEnumValue(c);
      } else {
        return Enum.valueOf(c, UserSettingValue.getValueFor(user, this));
      }
    }
  },
  FONT_SCALE_FACTOR("1");
  private final String defaultValue;

  UserSetting(String defaultValue) {
    this.defaultValue = defaultValue;
  }

  public String getDefaultValue() {
    return defaultValue;
  }

  public String getValue(User user) {
    return UserSettingValue.getValueFor(user, this);
  }

  public String getStringValue(User user) {
    return UserSettingValue.getValueFor(user, this);
  }

  public double getDoubleValue(User user) {
    return Double.parseDouble(UserSettingValue.getValueFor(user, this));
  }

  public int getIntValue(User user) {
    return Integer.parseInt(UserSettingValue.getValueFor(user, this));
  }

  public long getLongValue(User user) {
    return Long.parseLong(UserSettingValue.getValueFor(user, this));
  }

  public float getFloatValue(User user) {
    return Float.parseFloat(UserSettingValue.getValueFor(user, this));
  }

  public void setValue(User user,String value){
    UserSettingValue.setValue(user,this,value);
  }

  public void setValue(User user,Object value){
    UserSettingValue.setValue(user,this,String.valueOf(value));
  }


  public <T extends Enum<T>> T getEnumValue(Class<T> c, User user) {
    return Enum.valueOf(c, UserSettingValue.getValueFor(user, this));
  }
}
