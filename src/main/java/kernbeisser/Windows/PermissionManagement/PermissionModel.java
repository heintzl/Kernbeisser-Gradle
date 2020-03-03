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
    void addKey(Permission permission,Key key){
        EntityManager em = DBConnection.getEntityManager();
        EntityTransaction et = em.getTransaction();
        et.begin();
        Permission db = em.find(Permission.class,permission.getId());
        db.getKeySet().add(key);
        em.persist(db);
        em.flush();
        et.commit();
        em.close();
    }
    void removeKey(Permission permission, Key key){
        EntityManager em = DBConnection.getEntityManager();
        EntityTransaction et = em.getTransaction();
        et.begin();
        Permission db = em.find(Permission.class,permission.getId());
        db.getKeySet().remove(key);
        em.persist(db);
        em.flush();
        et.commit();
        em.close();
    }

    Collection<Permission> getAllPermissions(){
        return Permission.getAll(null);
    }

    Security[] getAllSecurities() {
        return Security.values();
    }

    KeyCategory[] getAllKeyCategories() {
        return KeyCategory.values();
    }

    void deletePermission(Permission selectedObject) {
        Tools.delete(selectedObject,selectedObject.getId());
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
}
