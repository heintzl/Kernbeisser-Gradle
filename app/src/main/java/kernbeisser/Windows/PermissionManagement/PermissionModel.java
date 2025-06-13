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
  private final Map<Permission, Map<PermissionKey, AccessLevel>> originalPermissionKeyLevels =
      new HashMap<>();
  private final Set<PermissionKeyGroups> loadedGroups = new HashSet<>();

  private AccessLevel readPermissionLevel(PermissionKey permissionKey, Permission permission) {
    if (PermissionKeyGroups.isInGroup(permissionKey, PermissionKeyGroups.ACTIONS)
        || !(permissionKey == PermissionKey.CHANGE_ALL
            || PermissionKeys.getWriteKey(permissionKey) != permissionKey)) {
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

  private static void setPermissionLevelTo(
      Map<Permission, Map<PermissionKey, AccessLevel>> permissionKeyLevels,
      PermissionKey permissionKey,
      Permission permission,
      AccessLevel level) {
    Map<PermissionKey, AccessLevel> permissionKeyMap =
        permissionKeyLevels.computeIfAbsent(
            permission, p -> new HashMap<PermissionKey, AccessLevel>());
    permissionKeyMap.put(permissionKey, level);
  }

  public void setPermissionLevel(
      PermissionKey permissionKey, Permission permission, AccessLevel level) {
    setPermissionLevelTo(permissionKeyLevels, permissionKey, permission, level);
  }

  private void setOriginalPermissionKeyLevels(
      PermissionKey permissionKey, Permission permission, AccessLevel level) {
    setPermissionLevelTo(originalPermissionKeyLevels, permissionKey, permission, level);
  }

  private AccessLevel getPermissionLevelFrom(
      Map<Permission, Map<PermissionKey, AccessLevel>> permissionKeyLevels,
      PermissionKey permissionKey,
      Permission permission) {
    Map<PermissionKey, AccessLevel> permissionKeyMap = permissionKeyLevels.get(permission);
    if (permissionKeyMap == null) return null;
    else return permissionKeyMap.get(permissionKey);
  }

  public AccessLevel getPermissionLevel(PermissionKey permissionKey, Permission permission) {
    return getPermissionLevelFrom(permissionKeyLevels, permissionKey, permission);
  }

  private AccessLevel getOriginalPermissionLevel(
      PermissionKey permissionKey, Permission permission) {
    return getPermissionLevelFrom(originalPermissionKeyLevels, permissionKey, permission);
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

  public void addPermission(String permissionName) {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    @Cleanup(value = "commit")
    EntityTransaction et = em.getTransaction();
    et.begin();
    Permission permission = new Permission();
    permission.setName(permissionName);
    em.persist(permission);
    Map<PermissionKey, AccessLevel> permissionKeyMap = new HashMap<>();
    Map<PermissionKey, AccessLevel> originalPermissionKeyMap = new HashMap<>();
    for (PermissionKey key :
        permissionKeyLevels.get(PermissionConstants.BASIC_ACCESS.getPermission()).keySet()) {
      AccessLevel level =
          (key == PermissionKey.CHANGE_ALL || PermissionKeys.getWriteKey(key) != key)
              ? NONE
              : NO_ACTION;
      permissionKeyMap.put(key, level);
      originalPermissionKeyMap.put(key, level);
    }
    permissionKeyLevels.put(permission, permissionKeyMap);
    originalPermissionKeyLevels.put(permission, originalPermissionKeyMap);
  }

  void deletePermission(Permission permission) {
    Tools.delete(Permission.class, permission.getId());
    permissionKeyLevels.remove(permission);
    originalPermissionKeyLevels.remove(permission);
  }

  public void removeUserFromPermission(Permission permission) {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    @Cleanup(value = "commit")
    EntityTransaction et = em.getTransaction();
    et.begin();
    List<User> resultList =
        QueryBuilder.selectAll(User.class)
            .where(PredicateFactory.isMember(asExpression(permission), User_.permissions))
            .getResultList(em);
    for (User e : resultList) {
      e.getPermissions().remove(permission);
      em.merge(e);
    }
    em.flush();
    deletePermission(permission);
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
              setOriginalPermissionKeyLevels(key, p, minimumLevel);
            } else {
              setPermissionLevel(key, p, readPermissionLevel(key, p));
              setOriginalPermissionKeyLevels(key, p, readPermissionLevel(key, p));
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

  public boolean isDirty(Permission permission, PermissionKey key) {
    return getPermissionLevel(key, permission) != getOriginalPermissionLevel(key, permission);
  }

  public Map<Permission, Map<PermissionKey, AccessLevel>> dirtyPermissionKeys() {
    Map<Permission, Map<PermissionKey, AccessLevel>> dirty = new HashMap<>();
    for (Permission p : permissionKeyLevels.keySet()) {
      for (PermissionKey k : permissionKeyLevels.get(p).keySet()) {
        if (!k.equals(PermissionKey.CHANGE_ALL)) {
          AccessLevel newLevel = getPermissionLevel(k, p);
          if (isDirty(p, k)) {
            Map<PermissionKey, AccessLevel> keyLevels =
                dirty.computeIfAbsent(p, newP -> new HashMap<>());
            keyLevels.put(k, newLevel);
          }
        }
      }
    }
    return dirty;
  }

  public void persistChanges(Map<Permission, Map<PermissionKey, AccessLevel>> changedLevels) {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    @Cleanup(value = "commit")
    EntityTransaction et = em.getTransaction();
    et.begin();
    for (Permission p : changedLevels.keySet()) {
      Map<PermissionKey, AccessLevel> changedPermissionKeys = changedLevels.get(p);
      Permission persistedPermission = em.find(Permission.class, p.getId());
      Set<PermissionKey> permissionKeySet = persistedPermission.getKeySet();
      for (PermissionKey k : changedPermissionKeys.keySet()) {
        PermissionKey writeKey = PermissionKeys.getWriteKey(k);
        AccessLevel level = changedPermissionKeys.get(k);
        switch (level) {
          case NONE -> {
            permissionKeySet.remove(k);
            permissionKeySet.remove(writeKey);
          }
          case READ -> {
            permissionKeySet.add(k);
            permissionKeySet.remove(writeKey);
          }
          case READ_WRITE -> {
            permissionKeySet.add(k);
            permissionKeySet.add(writeKey);
          }
          case NO_ACTION -> permissionKeySet.remove(k);
          case ACTION -> permissionKeySet.add(k);
        }
        setOriginalPermissionKeyLevels(k, p, level);
      }
      persistedPermission.setKeySet(permissionKeySet);
      em.merge(persistedPermission);
    }
  }
}
