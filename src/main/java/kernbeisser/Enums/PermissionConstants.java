package kernbeisser.Enums;

import com.google.common.collect.ImmutableMap;
import java.util.Set;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.NoResultException;
import kernbeisser.DBConnection.DBConnection;
import kernbeisser.DBEntities.Permission;
import kernbeisser.DBEntities.User;
import kernbeisser.Security.Access.Access;
import kernbeisser.Security.Access.AccessManager;
import kernbeisser.Security.PermissionSet;
import lombok.Cleanup;

// Permissions which become automatically generated when the application
// requires them to prevent the functionality from the application
public enum PermissionConstants {
  // the permission with all rights reserved
  ADMIN(allPermissions()),
  FULL_MEMBER(PermissionKey.ACTION_LOGIN),
  // the permission which is given to all imported users from the old application
  IMPORT,
  // the permission which is given to all users which has a key in the old application
  KEY_PERMISSION(PermissionKey.ACTION_LOGIN, PermissionKey.GO_UNDER_MIN),
  APPLICATION(PermissionKey.values()),
  IN_RELATION_TO_OWN_USER(PermissionKey.find(User.class).toArray(new PermissionKey[0])),
  CASHIER(PermissionKey.ACTION_OPEN_CASHIER_SHOPPING_MASK),
  // the default permission for all new users
  BASIC_ACCESS(PermissionKey.ACTION_LOGIN),
  TRIAL_MEMBER(PermissionKey.ACTION_LOGIN);

  private final Permission bounded;

  final PermissionSet defaultPermissionKeys;

  PermissionConstants(PermissionKey... keys) {
    this.defaultPermissionKeys = PermissionSet.asPermissionSet(keys);
    this.bounded = loadOrCreate(this);
  }

  PermissionConstants(PermissionSet keys) {
    this.defaultPermissionKeys = keys;
    this.bounded = loadOrCreate(this);
  }

  public Permission getPermission() {
    return bounded;
  }

  public static String getTranslation(String permissionName) {
    ImmutableMap<String, String> nameTranslations =
        ImmutableMap.<String, String>builder()
            .put("@KEY_PERMISSION", "<Schlüssel Inhaber>")
            .put("@IMPORT", "<Aus alter Version übernommen>")
            .put("@FULL_MEMBER", "<Vollmitglied>")
            .put("@APPLICATION", "<Applikation>")
            .put("@ADMIN", "<Administration>")
            .put("@IN_RELATION_TO_OWN_USER", "<eigene Daten>")
            .put("@CASHIER", "<Ladendienst>")
            .put("@BASIC_ACCESS", "<Basis-Anwender>")
            .put("@TRIAL_MEMBER", "<Probemitglied>")
            .build();
    return nameTranslations.getOrDefault(permissionName, permissionName);
  }

  private static Permission loadOrCreate(PermissionConstants constants) {
    return Access.runWithAccessManager(AccessManager.NO_ACCESS_CHECKING, () -> {
              @Cleanup EntityManager em = DBConnection.getEntityManager();
              try {
                @Cleanup(value = "commit")
                EntityTransaction et = em.getTransaction();
                et.begin();
                return em.createQuery("select p from Permission p where name like :pcn", Permission.class)
                        .setParameter("pcn", "@" + constants.name())
                        .getSingleResult();
              } catch (NoResultException e) {
                          EntityTransaction et = em.getTransaction();
                          et.begin();
                          Permission permission = new Permission();
                          permission.getKeySet().addAll(constants.defaultPermissionKeys);
                          permission.setName("@" + constants.name());
                          em.persist(permission);
                          em.flush();
                          et.commit();
                return loadOrCreate(constants);
              }
            }
    );
  }

  public static PermissionSet allPermissions() {
    PermissionSet p = new PermissionSet();
    p.setAllBits(true);
    return p;
  }

  public static void cleanAdminPermission(User currentUser) {
    Access.putException(currentUser, AccessManager.NO_ACCESS_CHECKING);
    Permission adminPermission = ADMIN.getPermission();
    Access.putException(adminPermission, AccessManager.NO_ACCESS_CHECKING);
    adminPermission.setKeySet(allPermissions().minus(PermissionKey.getNonAdminPermissions()));
    Set<Permission> adminPermissionSet = new java.util.HashSet<>();
    adminPermissionSet.add(adminPermission);
    currentUser.setPermissions(adminPermissionSet);
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    @Cleanup(value = "commit")
    EntityTransaction et = em.getTransaction();
    et.begin();
    em.merge(adminPermission);
    em.merge(currentUser);
    em.flush();
    Access.removeException(currentUser);
  }
}
