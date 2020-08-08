package kernbeisser.Windows.Setting;

import kernbeisser.DBEntities.SettingValue;
import kernbeisser.Windows.MVC.Model;

public class SettingModel implements Model<SettingController> {
  private SettingValue settingValue;

  public void edit(String value) {
    settingValue.getSetting().setValue(value);
  }

  public void setSelectedValue(SettingValue settingValue) {
    this.settingValue = settingValue;
  }

  public SettingValue getSelectedSettingValue() {
    return settingValue;
  }
}
