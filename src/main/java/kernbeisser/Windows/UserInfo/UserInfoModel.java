package kernbeisser.Windows.UserInfo;

import kernbeisser.DBEntities.User;
import kernbeisser.Windows.MVC.IModel;

public class UserInfoModel implements IModel<UserInfoController> {

  private final User user;

  public UserInfoModel(User user) {
    this.user = user;
  }

  public User getUser() {
    return user;
  }
}
