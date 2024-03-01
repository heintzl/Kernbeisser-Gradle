package kernbeisser.Reports;

import java.util.ArrayList;
import java.util.List;
import kernbeisser.DBEntities.Permission;
import kernbeisser.DBEntities.User;
import lombok.Getter;

public class PermissionHolderBean {

  @Getter private final String permission;
  @Getter private final User user;

  public PermissionHolderBean(String permission, User user) {
    this.permission = permission;
    this.user = user;
  }

  public static List<PermissionHolderBean> createBeans(Permission permission) {
    List<PermissionHolderBean> permissionHolders = new ArrayList<>();
    for (User u : permission.getAllUsers()) {
      if (!u.isSysAdmin() && !u.isKernbeisser())
        permissionHolders.add(new PermissionHolderBean(permission.getNeatName(), u));
    }
    return permissionHolders;
  }
}
