package kernbeisser.DBEntities;

import com.google.common.collect.Maps;
import java.util.*;
import java.util.stream.Collectors;
import javax.persistence.*;
import kernbeisser.DBConnection.DBConnection;
import kernbeisser.Enums.PermissionConstants;
import kernbeisser.Enums.PermissionKey;
import kernbeisser.Security.Key;
import kernbeisser.Useful.Tools;
import lombok.Cleanup;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table
@EqualsAndHashCode(doNotUseGetters = true, exclude = "keySet")
public class Permission {
  @Id
  @GeneratedValue
  @Getter(onMethod_ = {@Key(PermissionKey.PERMISSION_ID_READ)})
  @Setter(onMethod_ = {@Key(PermissionKey.PERMISSION_ID_WRITE)})
  private int id;

  @Column(unique = true, nullable = false)
  @Getter(onMethod_ = {@Key(PermissionKey.PERMISSION_NAME_READ)})
  @Setter(onMethod_ = {@Key(PermissionKey.PERMISSION_NAME_WRITE)})
  private String name;

  @JoinColumn
  @Enumerated(EnumType.STRING)
  @ElementCollection(fetch = FetchType.EAGER)
  @Setter(onMethod_ = {@Key({PermissionKey.PERMISSION_KEY_SET_WRITE})})
  @Getter(
      onMethod_ = {
        @Key({PermissionKey.PERMISSION_KEY_SET_READ, PermissionKey.PERMISSION_KEY_SET_WRITE})
      })
  private Set<PermissionKey> keySet = new HashSet<>();

  @Key(PermissionKey.PERMISSION_KEY_SET_READ)
  public Set<PermissionKey> getKeySetAsAvailable() {
    return Tools.or(this::getKeySet, Collections.unmodifiableSet(keySet));
  }

  public static List<Permission> getAll(String condition) {
    return Tools.getAll(Permission.class, condition);
  }

  public boolean contains(PermissionKey key) {
    return getKeySetAsAvailable().contains(key);
  }

  public static Collection<Permission> defaultSearch(String s, int max) {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    @Cleanup(value = "commit")
    EntityTransaction et = em.getTransaction();
    et.begin();
    return em.createQuery("select p from Permission p where p.name like :s", Permission.class)
        .setParameter("s", s + "%")
        .setMaxResults(max)
        .getResultList();
  }

  public String getNeatName() {
    if (!name.startsWith("@")) {
      return name;
    } else {
      return PermissionConstants.getTranslation(name);
    }
  }

  @Override
  public String toString() {
    return Tools.optional(this::getName).orElse("Permission[" + id + "]");
  }

  public Collection<User> getAllUsers() {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    @Cleanup(value = "commit")
    EntityTransaction et = em.getTransaction();
    et.begin();
    return DBConnection.getEntityManager()
        .createQuery("select u from User u where :p in(elements(u.permissions))", User.class)
        .setParameter("p", this)
        .getResultList();
  }

  public List<Permission> getGranters() {
    return DBConnection.getAll(PermissionGrant.class).stream()
        .filter(g -> g.getOn().equals(this))
        .map(PermissionGrant::getBy)
        .collect(Collectors.toList());
  }

  public List<Permission> getGrantees() {
    return DBConnection.getAll(PermissionGrant.class).stream()
        .filter(g -> g.getBy().equals(this))
        .map(PermissionGrant::getOn)
        .collect(Collectors.toList());
  }

  public void addGranters(Permission... permission) {

    @Cleanup EntityManager em = DBConnection.getEntityManager();
    @Cleanup(value = "commit")
    EntityTransaction et = em.getTransaction();
    et.begin();
    for (Permission p : permission) {
      em.merge(new PermissionGrant(this, p));
    }
  }

  public void removeGranters(Permission... permissions) {

    @Cleanup EntityManager em = DBConnection.getEntityManager();
    @Cleanup(value = "commit")
    EntityTransaction et = em.getTransaction();
    et.begin();
    Map<String, PermissionGrant> indexedGrants =
        Maps.uniqueIndex(
            DBConnection.getAll(PermissionGrant.class), (g -> g.getOn().id + "#" + g.getBy().id));
    for (Permission p : permissions) {
      PermissionGrant grantToDelete = indexedGrants.get(this.id + "#" + p.id);
      if (grantToDelete != null) {
        em.remove(em.find(PermissionGrant.class, grantToDelete.getId()));
      }
    }
  }
}
