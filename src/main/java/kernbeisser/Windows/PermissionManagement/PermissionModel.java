package kernbeisser.Windows.PermissionManagement;

import java.util.Collection;
import java.util.HashSet;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import kernbeisser.DBConnection.DBConnection;
import kernbeisser.DBEntities.Permission;
import kernbeisser.DBEntities.User;
import kernbeisser.Enums.PermissionKey;
import kernbeisser.Useful.Tools;
import kernbeisser.Windows.MVC.IModel;
import lombok.Cleanup;

public class PermissionModel implements IModel<PermissionController> {
  void addKey(Permission permission, PermissionKey key) {
    Tools.addToCollection(Permission.class, permission.getId(), Permission::getKeySet, key);
  }

  void removeKey(Permission permission, PermissionKey key) {
    Tools.removeFromCollection(Permission.class, permission.getId(), Permission::getKeySet, key);
  }

  Collection<Permission> getAllPermissions() {
    return Permission.getAll(null);
  }

  Class<?>[] getAllKeyCategories() {
    HashSet<Class<?>> classes = new HashSet<>();
    for (PermissionKey value : PermissionKey.values()) {
      if (value.getClazz() != null) {
        classes.add(value.getClazz());
      }
    }
    return classes.toArray(new Class[0]);
  }

  void deletePermission(Permission selectedObject) {
    Tools.delete(Permission.class, selectedObject.getId());
  }

  void addPermission(String permissionName) {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    EntityTransaction et = em.getTransaction();
    et.begin();
    Permission pm = new Permission();
    pm.setName(permissionName);
    em.persist(pm);
    em.flush();
    et.commit();
    em.close();
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
    em.createQuery("select u from User u", User.class)
        .getResultList()
        .forEach(
            e -> {
              e.getPermissions().remove(permission);
              em.persist(e);
            });
    em.flush();
    em.remove(permission);
    et.commit();
  }
}
