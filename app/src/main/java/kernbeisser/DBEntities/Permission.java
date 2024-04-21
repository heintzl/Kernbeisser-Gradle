package kernbeisser.DBEntities;

import com.google.common.collect.Maps;
import static kernbeisser.DBConnection.PredicateFactory.isMember;
import static kernbeisser.DBConnection.PredicateFactory.like;

import jakarta.persistence.*;
import java.util.*;
import java.util.stream.Collectors;
import javax.persistence.*;
import kernbeisser.DBConnection.DBConnection;
import kernbeisser.DBConnection.ExpressionFactory;
import kernbeisser.DBConnection.QueryBuilder;
import kernbeisser.DBEntities.Permission_;
import kernbeisser.DBEntities.User_;
import kernbeisser.Enums.PermissionConstants;
import kernbeisser.Useful.Tools;
import lombok.*;
import rs.groump.Key;
import rs.groump.PermissionKey;
import rs.groump.PermissionSet;

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

  public boolean contains(PermissionKey key) {
    return getKeySetAsAvailable().contains(key);
  }

  public static Collection<Permission> defaultSearch(String s, int max) {
    return QueryBuilder.selectAll(Permission.class)
        .where(like(Permission_.name, s + "%"))
        .limit(max)
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
    return Tools.runIfPossible(this::getName).orElse("Permission[" + id + "]");
  }

  public Collection<User> getAllUsers() {
    return QueryBuilder.selectAll(User.class)
        .where(isMember(ExpressionFactory.asExpression(this), User_.permissions))
        .getResultList();
  }

  public PermissionSet toPermissionSet() {
    PermissionSet permissionKeys = new PermissionSet();
    permissionKeys.addAll(keySet);
    return permissionKeys;
  }

  //TODO Review query
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
