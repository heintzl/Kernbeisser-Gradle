package kernbeisser.Windows.Setting;

import kernbeisser.Enums.Setting;
import kernbeisser.Windows.MVC.IModel;

public class SettingModel implements IModel<SettingController> {
  private Setting setting;

  public void edit(String value) {
    setting.changeValue(value);
  }

  public void setSelectedValue(Setting settingValue) {
    this.setting = settingValue;
  }

  public Setting getSelectedSettingValue() {
    return setting;
  }
}
