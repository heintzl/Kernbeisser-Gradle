package kernbeisser.Windows.UserInfo;

import kernbeisser.DBEntities.User;
import kernbeisser.Windows.Model;

public class UserInfoModel implements Model<UserInfoController> {

    private final User user;

    public UserInfoModel(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }

}
