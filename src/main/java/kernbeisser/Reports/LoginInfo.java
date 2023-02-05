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
    super(ReportFileNames.LOGININFO_REPORT_FILENAME);
    this.user = user;
    this.newPassword = newPassword;
  }

  @Override
  String createOutFileName() {
    return "loginInfo_" + user.getUsername();
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
