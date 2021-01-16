package kernbeisser.Reports;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;
import kernbeisser.DBEntities.Permission;

public class PermissionHolders extends Report {
  private final Collection<Permission> permissions;

  public PermissionHolders(Collection<Permission> permissions) {
    super("permissionHolders", "RollenInhaber" + LocalDate.now().toString());
    this.permissions = permissions;
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
