package kernbeisser.Reports;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import kernbeisser.DBEntities.User;

public class LoginInfo extends Report {

  private final User user;
  private final String newPassword;

  public LoginInfo(User user, String newPassword) {
    super("loginInfo", "loginInfo_" + user.getUsername());
    this.user = user;
    this.newPassword = newPassword;
  }

  @Override
  Map<String, Object> getReportParams() {
    Map<String, Object> params = new HashMap<>();
    params.put("password", newPassword);
    return params;
  }

  @Override
  Collection<?> getDetailCollection() {
    return Collections.singletonList(user);
  }
}
