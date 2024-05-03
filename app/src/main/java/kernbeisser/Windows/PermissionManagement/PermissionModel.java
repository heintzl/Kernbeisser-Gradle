package kernbeisser.Windows.PermissionManagement;

import static kernbeisser.DBConnection.ExpressionFactory.asExpression;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import java.util.*;
import kernbeisser.DBConnection.DBConnection;
import kernbeisser.DBConnection.PredicateFactory;
import kernbeisser.DBConnection.QueryBuilder;
import kernbeisser.DBEntities.Permission;
import kernbeisser.DBEntities.User;
import kernbeisser.DBEntities.User_;
import kernbeisser.Security.PermissionKeyGroups;
import kernbeisser.Useful.Tools;
import kernbeisser.Windows.MVC.IModel;
import lombok.Cleanup;
import rs.groump.PermissionKey;

public class PermissionModel implements IModel<PermissionController> {
  void addKey(Permission permission, PermissionKey key) {
    Tools.addToCollection(Permission.class, permission.getId(), Permission::getKeySet, key);
  }

  void removeKey(Permission permission, PermissionKey key) {
    Tools.removeFromCollection(Permission.class, permission.getId(), Permission::getKeySet, key);
  }

  Collection<Permission> getAllPermissions() {
    return Tools.getAll(Permission.class);
  }

  List<PermissionKeyGroups> getAllKeyCategories() {
    return Arrays.asList(PermissionKeyGroups.values());
  }

  void deletePermission(Permission selectedObject) {
    Tools.delete(Permission.class, selectedObject.getId());
  }

  void addPermission(String permissionName) {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    @Cleanup(value = "commit")
    EntityTransaction et = em.getTransaction();
    et.begin();
    Permission pm = new Permission();
    pm.setName(permissionName);
    em.persist(pm);
    em.flush();
  }

  void removeKeys(Permission permission, Collection<PermissionKey> keys) {
    Tools.removeMultipleFromCollection(
        Permission.class, permission.getId(), Permission::getKeySet, keys);
  }

  void addKeys(Permission permission, Collection<PermissionKey> keys) {
    Tools.addMultipleToCollection(
        Permission.class, permission.getId(), Permission::getKeySet, keys);
  }

  public void removeUserFromPermission(Permission permission) {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    EntityTransaction et = em.getTransaction();
    et.begin();
    // TODO: fix that inefficient query
    List<User> resultList =
        QueryBuilder.selectAll(User.class)
            .where(PredicateFactory.isMember(asExpression(permission), User_.permissions))
            .getResultList(em);
    for (User e : resultList) {
      e.getPermissions().remove(permission);
      em.persist(e);
    }
    em.flush();
    em.remove(permission);
    et.commit();
  }
}
