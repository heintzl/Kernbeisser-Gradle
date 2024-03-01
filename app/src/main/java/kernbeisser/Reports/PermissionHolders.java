package kernbeisser.Reports;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;
import kernbeisser.DBConnection.DBConnection;
import kernbeisser.DBEntities.Permission;

public class PermissionHolders extends Report {
  private final Collection<Permission> permissions;

  public PermissionHolders(boolean withKeys) {
    super(ReportFileNames.PERMISSION_HOLDERS_REPORT_FILENAME);
    permissions =
        DBConnection.getEntityManager()
            .createQuery(
                "Select p from Permission p where not p.name in ('@IMPORT', '@APPLICATION', '@IN_RELATION_TO_OWN_USER'"
                    + (withKeys ? "" : ", '@Key_Permission', '@FULL_MEMBER' ,'@BASIC_ACCESS'")
                    + ")",
                Permission.class)
            .getResultList();
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
