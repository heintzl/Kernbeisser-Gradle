package kernbeisser.Windows.PermissionManagement;

import static kernbeisser.DBConnection.ExpressionFactory.asExpression;
import static kernbeisser.Windows.PermissionManagement.AccessLevel.*;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import java.util.*;
import java.util.stream.Collectors;
import kernbeisser.DBConnection.DBConnection;
import kernbeisser.DBConnection.PredicateFactory;
import kernbeisser.DBConnection.QueryBuilder;
import kernbeisser.DBEntities.Permission;
import kernbeisser.DBEntities.Permission_;
import kernbeisser.DBEntities.User;
import kernbeisser.DBEntities.User_;
import kernbeisser.Enums.PermissionConstants;
import kernbeisser.Security.PermissionKeyGroups;
import kernbeisser.Security.PermissionKeys;
import kernbeisser.Useful.Tools;
import kernbeisser.Windows.MVC.IModel;
import lombok.Cleanup;
import lombok.Getter;
import rs.groump.PermissionKey;

@Getter
public class PermissionModel implements IModel<PermissionController> {

  private PermissionKeyGroups selectedGroup;
  private Collection<PermissionKey> readPermission;
  private Collection<PermissionKey> writePermission;
  private final Map<Permission, Map<PermissionKey, AccessLevel>> permissionKeyLevels =
      new HashMap<>();
  private final Set<PermissionKeyGroups> loadedGroups = new HashSet<>();

  private AccessLevel readPermissionLevel(PermissionKey permissionKey, Permission permission) {
    if (PermissionKeyGroups.isInGroup(permissionKey, PermissionKeyGroups.ACTIONS)) {
      return permission.contains(permissionKey) ? ACTION : NO_ACTION;
    }
    boolean read;
    boolean write;
    if (permissionKey == PermissionKey.CHANGE_ALL) {
      read = permission.getKeySet().containsAll(readPermission);
      write = permission.getKeySet().containsAll(writePermission);
    } else {
      read = permission.contains(permissionKey);
      write = permission.contains(PermissionKeys.getWriteKey(permissionKey));
    }
    return (read ? write ? READ_WRITE : READ : NONE);
  }

  public void setPermissionLevel(
      PermissionKey permissionKey, Permission permission, AccessLevel level) {
    Map<PermissionKey, AccessLevel> permissionKeyMap =
        permissionKeyLevels.computeIfAbsent(
            permission, p -> new HashMap<PermissionKey, AccessLevel>());
    permissionKeyMap.put(permissionKey, level);
  }

  public AccessLevel getPermissionLevel(PermissionKey permissionKey, Permission permission) {
    Map<PermissionKey, AccessLevel> permissionKeyMap = permissionKeyLevels.get(permission);
    if (permissionKeyMap == null) return null;
    else return permissionKeyMap.get(permissionKey);
  }

  private AccessLevel cycleLevel(AccessLevel level) {
    switch (level) {
      case NONE -> {
        return READ;
      }
      case READ -> {
        return READ_WRITE;
      }
      case READ_WRITE -> {
        return NONE;
      }
      case ACTION -> {
        return NO_ACTION;
      }
      case NO_ACTION -> {
        return ACTION;
      }
    }
    return null;
  }

  public PermissionKey cycleAccess(PermissionKey permissionKey, Permission permission) {
    if (permissionKey == PermissionKey.CHANGE_ALL) {
      AccessLevel level = (getPermissionLevel(permissionKey, permission));
      AccessLevel newLevel = level == NONE ? READ : level == READ ? READ_WRITE : NONE;
      permissionKeyLevels.get(permission).keySet().stream()
          .filter(
              k ->
                  PermissionKeyGroups.isInGroup(k, selectedGroup)
                      && readPermission.contains(k)
                      && (newLevel == NONE
                          || getPermissionLevel(k, permission).ordinal() < newLevel.ordinal()))
          .forEach(k -> setPermissionLevel(k, permission, newLevel));
    }
    setPermissionLevel(
        permissionKey, permission, cycleLevel(getPermissionLevel(permissionKey, permission)));
    return permissionKey;
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

  public List<Permission> readAllPermissions() {
    List<Permission> result =
        DBConnection.getConditioned(
            Permission.class, Permission_.name.eq(PermissionConstants.ADMIN.nameId()).not());
    Collections.reverse(result);
    return result;
  }

  public List<PermissionKey> createAllKeysFor(PermissionKeyGroups group) {
    List<PermissionKey> keys =
        Arrays.stream(PermissionKey.values())
            .filter(k -> !k.name().endsWith("_WRITE") && PermissionKeyGroups.isInGroup(k, group))
            .collect(Collectors.toList());
    if (!group.equals(PermissionKeyGroups.ACTIONS)) {
      keys.addFirst(PermissionKey.CHANGE_ALL);
    }
    boolean groupSelectedForFirstTime = !loadedGroups.contains(group);
    if (groupSelectedForFirstTime) {
      loadedGroups.add(group);
    }
    for (Permission p : DBConnection.getAll(Permission.class)) {
      if (!p.getName().equals("@ADMIN")) {
        if (groupSelectedForFirstTime) {
          for (PermissionKey key : keys) {
            if (key == PermissionKey.CHANGE_ALL) {
              AccessLevel minimumLevel =
                  keys.stream()
                      .map(k -> readPermissionLevel(k, p))
                      .min(Comparator.comparingInt(AccessLevel::ordinal))
                      .orElse(NONE);
              setPermissionLevel(key, p, minimumLevel);
            } else {
              setPermissionLevel(key, p, readPermissionLevel(key, p));
            }
          }
        }
      }
    }
    return keys;
  }

  public List<PermissionKey> selectGroup(PermissionKeyGroups group) {
    selectedGroup = group;
    readPermission = PermissionKeys.find(group, true, false);
    writePermission = PermissionKeys.find(group, false, true);
    return createAllKeysFor(group);
  }

  public Map<Permission, Map<PermissionKey, AccessLevel>> dirtyPermissionKeys() {
    Map<Permission, Map<PermissionKey, AccessLevel>> dirty = new HashMap<>();
    for (Permission p : permissionKeyLevels.keySet()) {
      for (PermissionKey k : permissionKeyLevels.get(p).keySet()) {
        if (!k.equals(PermissionKey.CHANGE_ALL)) {
          AccessLevel newLevel = getPermissionLevel(k, p);
          if (readPermissionLevel(k, p) != newLevel) {
            Map<PermissionKey, AccessLevel> keyLevels =
                dirty.computeIfAbsent(p, newP -> new HashMap<>());
            keyLevels.put(k, newLevel);
          }
        }
      }
    }
    return dirty;
  }
}
