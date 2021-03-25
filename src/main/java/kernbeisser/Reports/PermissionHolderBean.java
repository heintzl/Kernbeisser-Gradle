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
    String name = permission.getName();
    switch (name) {
      case "@KEY_PERMISSION":
        name = "SchlüsselinhaberIn (Selbsteinkauf)";
        break;
      case "@BEGINNER":
        name = "Probemitglied";
        break;
      case "@ADMIN":
        name = "SystemadministratorIn";
        break;
      case "@IMPORT":
        name = "Übernommen aus Version 1";
        break;
      case "@APPLICATION":
        name = "Anwendungs-Dienst";
        break;
      case "@ON_OWN_USER":
        name = "Zugriffsrolle für eigene Daten";
        break;
      default:
    }
    for (User u : permission.getAllUsers()) {
      if (!u.toString().equals("Admin") && !u.toString().equals("kernbeisser"))
        permissionHolders.add(new PermissionHolderBean(name, u));
    }
    return permissionHolders;
  }
}
