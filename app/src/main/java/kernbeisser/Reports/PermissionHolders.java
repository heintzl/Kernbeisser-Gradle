package kernbeisser.Reports;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import kernbeisser.DBConnection.DBConnection;
import kernbeisser.DBEntities.Permission;
import kernbeisser.DBEntities.Permission_;

public class PermissionHolders extends Report {
  private final Collection<Permission> permissions;

  public PermissionHolders(boolean withKeys) {
    super(ReportFileNames.PERMISSION_HOLDERS_REPORT_FILENAME);
    List<String> excludedPermissionNames =
        new ArrayList<>(Arrays.asList("@IMPORT", "@APPLICATION", "@IN_RELATION_TO_OWN_USER"));
    if (!withKeys) {
      excludedPermissionNames.addAll(
          Arrays.asList("@Key_Permission", "@FULL_MEMBER", "@BASIC_ACCESS"));
    }
    permissions =
        DBConnection.getConditioned(
            Permission.class, Permission_.name.in(excludedPermissionNames).not());
  }

  @Override
  String createOutFileName() {
    return "RollenInhaber" + LocalDate.now();
  }

  @Override
  Map<String, Object> getReportParams() {
    return null;
  }

  @Override
  Collection<?> getDetailCollection() {
    return permissions.stream()
        .flatMap(p -> PermissionHolderBean.createBeans(p).stream())
        .collect(Collectors.toList());
  }
}
