package kernbeisser.DBEntities;

import com.google.common.collect.ImmutableMap;
import java.util.*;
import javax.persistence.*;
import kernbeisser.DBConnection.DBConnection;
import kernbeisser.Enums.PermissionKey;
import kernbeisser.Security.IterableProtection.ProxyIterable;
import kernbeisser.Security.Key;
import kernbeisser.Useful.Tools;
import lombok.Cleanup;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table
@EqualsAndHashCode(doNotUseGetters = true)
public class Permission {
  @Id
  @GeneratedValue
  @Getter(onMethod_ = {@Key(PermissionKey.PERMISSION_ID_READ)})
  @Setter(onMethod_ = {@Key(PermissionKey.PERMISSION_ID_WRITE)})
  private int id;

  @Column(unique = true)
  @Getter(onMethod_ = {@Key(PermissionKey.PERMISSION_NAME_READ)})
  @Setter(onMethod_ = {@Key(PermissionKey.PERMISSION_NAME_WRITE)})
  private String name;

  @JoinColumn
  @Enumerated(EnumType.STRING)
  @ElementCollection(fetch = FetchType.EAGER)
  @Getter(
      onMethod_ = {
        @ProxyIterable(
            read = {PermissionKey.PERMISSION_NAME_READ},
            modify = {PermissionKey.PERMISSION_NAME_WRITE})
      })
  @Setter(onMethod_ = {@Key({PermissionKey.PERMISSION_NAME_WRITE})})
  private Set<PermissionKey> keySet = new HashSet<>();

  public static List<Permission> getAll(String condition) {
    return Tools.getAll(Permission.class, condition);
  }

  public boolean contains(PermissionKey key) {
    return keySet.contains(key);
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
    ImmutableMap<String, String> nameTranslations =
        ImmutableMap.<String, String>builder()
            .put("@KEY_PERMISSION", "<Schlüssel Inhaber>")
            .put("@IMPORT", "<Aus alter Version übernommen>")
            .put("@FULL_MEMBER", "<Vollmitglied>")
            .put("@APPLICATION", "<Applikation>")
            .put("@ADMIN", "<Administration>")
            .put("@IN_RELATION_TO_OWN_USER", "<eigene Daten>")
            .build();
    if (!name.startsWith("@")) {
      return name;
    } else {
      return nameTranslations.getOrDefault(name, name);
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
}
