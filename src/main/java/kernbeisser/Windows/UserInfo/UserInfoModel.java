package kernbeisser.Windows.UserInfo;

import kernbeisser.DBEntities.User;
import kernbeisser.Windows.Controller;
import kernbeisser.Windows.Model;

public class UserInfoModel implements Model {

    private final User user;

    public UserInfoModel(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }
}
