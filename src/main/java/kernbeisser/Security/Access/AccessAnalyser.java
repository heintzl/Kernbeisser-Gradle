package kernbeisser.Security.Access;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import kernbeisser.DBConnection.DBConnection;
import kernbeisser.DBEntities.Permission;
import kernbeisser.Enums.PermissionKey;
import kernbeisser.Security.PermissionSet;
import lombok.Cleanup;

public class AccessAnalyser implements AccessManager, PermissionKeyBasedAccessManager {

  private final String permissionName;
  private final PermissionSet keySet = new PermissionSet();

  public AccessAnalyser(String permissionName) {
    this.permissionName = permissionName;
  }

  @Override
  public boolean hasAccess(
      Object object, String methodName, String signature, PermissionKey[] keys) {
    for (PermissionKey key : keys) {
      keySet.addPermission(key);
    }
    return true;
  }

  @Override
  public boolean hasPermission(PermissionSet keys) {
    return true;
  }

  public void dumpInDB() {
    Permission permission = new Permission();
    permission.getKeySet().addAll(keySet.asPermissionSet());
    permission.setName(permissionName);
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    @Cleanup("commit")
    EntityTransaction et = em.getTransaction();
    et.begin();
    em.persist(permission);
    em.flush();
  }
}
