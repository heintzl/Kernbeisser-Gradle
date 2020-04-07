package kernbeisser.Windows.PermissionManagement;

import kernbeisser.DBConnection.DBConnection;
import kernbeisser.DBEntities.Permission;
import kernbeisser.DBEntities.Transaction;
import kernbeisser.Enums.Key;
import kernbeisser.Enums.KeyCategory;
import kernbeisser.Enums.Security;
import kernbeisser.Useful.Tools;
import kernbeisser.Windows.Model;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import java.util.Collection;

class PermissionModel implements Model {
    void addKey(Permission permission, Key key) {
        Tools.addToCollection(Permission.class,permission.getId(),Permission::getKeySet,key);
    }

    void removeKey(Permission permission, Key key) {
        Tools.removeFromCollection(Permission.class,permission.getId(),Permission::getKeySet,key);
    }

    Collection<Permission> getAllPermissions() {
        return Permission.getAll(null);
    }

    KeyCategory[] getAllKeyCategories() {
        return KeyCategory.values();
    }

    void deletePermission(Permission selectedObject) {
        Tools.delete(Permission.class, selectedObject.getId());
    }

    void addPermission(String permissionName) {
        EntityManager em = DBConnection.getEntityManager();
        EntityTransaction et = em.getTransaction();
        et.begin();
        Permission pm = new Permission();
        pm.setName(permissionName);
        em.persist(pm);
        em.flush();
        et.commit();
        em.close();
    }

    void removeKeys(Permission permission,Collection<Key> keys) {
        Tools.removeMultipleFromCollection(Permission.class,permission.getId(),Permission::getKeySet,keys);
    }

    void addKeys(Permission permission,Collection<Key> keys){
        Tools.addMultipleToCollection(Permission.class,permission.getId(),Permission::getKeySet,keys);
    }
}
