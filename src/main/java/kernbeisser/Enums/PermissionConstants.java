package kernbeisser.Enums;

import java.util.Arrays;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.NoResultException;
import kernbeisser.DBConnection.DBConnection;
import kernbeisser.DBEntities.Permission;
import kernbeisser.DBEntities.User;
import lombok.Cleanup;

// Permissions which become automatically generated when the application
// requires them to prevent the functionality from the application
public enum PermissionConstants {
  // the permission with all rights reserved
  ADMIN(PermissionKey.values()),
  // the default permission for all new users
  BEGINNER(PermissionKey.ACTION_LOGIN),
  // the permission which is given to all imported users from the old application
  IMPORT,
  // the permission which is given to all users which has a key in the old application
  KEY_PERMISSION(PermissionKey.ACTION_LOGIN, PermissionKey.GO_UNDER_MIN),
  APPLICATION(PermissionKey.values()),
  ON_OWN_USER(PermissionKey.find(User.class).toArray(new PermissionKey[0]));

  private final Permission bounded;

  final PermissionKey[] defaultPermissionKeys;

  PermissionConstants(PermissionKey... keys) {
    this.defaultPermissionKeys = keys;
    this.bounded = loadOrCreate(this);
  }

  public Permission getPermission() {
    return bounded;
  }

  private static Permission loadOrCreate(PermissionConstants constants) {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    try {
      return em.createQuery("select p from Permission p where name like :pcn", Permission.class)
          .setParameter("pcn", "@" + constants.name())
          .getSingleResult();
    } catch (NoResultException e) {
      EntityTransaction et = em.getTransaction();
      et.begin();
      Permission permission = new Permission();
      permission.getKeySet().addAll(Arrays.asList(constants.defaultPermissionKeys));
      permission.setName("@" + constants.name());
      em.persist(permission);
      em.flush();
      et.commit();
      return loadOrCreate(constants);
    } finally {
      em.close();
    }
  }
}
