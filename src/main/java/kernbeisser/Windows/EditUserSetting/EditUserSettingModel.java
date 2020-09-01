package kernbeisser.Windows.EditUserSetting;

import kernbeisser.DBEntities.User;
import kernbeisser.Enums.Theme;
import kernbeisser.Enums.UserSetting;
import kernbeisser.Windows.MVC.IModel;

public class EditUserSettingModel implements IModel<EditUserSettingController> {
  private final User user;

  public EditUserSettingModel(User user) {
    this.user = user;
  }

  public User getUser() {
    return user;
  }

  public void setFontScale(float f) {
    UserSetting.FONT_SCALE_FACTOR.setValue(user, f);
  }

  public void setTheme(Theme t) {
    UserSetting.THEME.setValue(user, t);
  }
}
