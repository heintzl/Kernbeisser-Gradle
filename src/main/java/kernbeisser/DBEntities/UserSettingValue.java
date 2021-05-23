package kernbeisser.DBEntities;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import javax.persistence.*;
import kernbeisser.DBConnection.DBConnection;
import kernbeisser.Enums.PermissionKey;
import kernbeisser.Enums.UserSetting;
import kernbeisser.Security.Key;
import kernbeisser.Security.Relations.UserRelated;
import kernbeisser.Useful.Tools;
import lombok.Cleanup;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

@Entity
@Table
@EqualsAndHashCode(doNotUseGetters = true)
public class UserSettingValue implements UserRelated {
  private static User loaded;
  private static HashMap<UserSetting, String> values;

  protected UserSettingValue() {}

  // for the is relation to own user check
  public UserSettingValue(User user) {
    this.user = user;
  }

  @Id
  @GeneratedValue
  @Getter(onMethod_ = {@Key(PermissionKey.USER_SETTING_VALUE_ID_READ)})
  @Setter(onMethod_ = {@Key(PermissionKey.USER_SETTING_VALUE_ID_WRITE)})
  private int id;

  @JoinColumn
  @ManyToOne
  @Getter(onMethod_ = {@Key(PermissionKey.USER_SETTING_VALUE_USER_READ)})
  @Setter(onMethod_ = {@Key(PermissionKey.USER_SETTING_VALUE_USER_WRITE)})
  private User user;

  @Column
  @Enumerated(EnumType.STRING)
  @Getter(onMethod_ = {@Key(PermissionKey.USER_SETTING_VALUE_USER_SETTING_READ)})
  @Setter(onMethod_ = {@Key(PermissionKey.USER_SETTING_VALUE_USER_SETTING_WRITE)})
  private UserSetting userSetting;

  @Column
  @Getter(onMethod_ = {@Key(PermissionKey.USER_SETTING_VALUE_VALUE_READ)})
  @Setter(onMethod_ = {@Key(PermissionKey.USER_SETTING_VALUE_VALUE_WRITE)})
  private String value;

  public static List<UserSettingValue> getAll(String condition) {
    return Tools.getAll(UserSettingValue.class, condition);
  }

  private static String loadOrCreateSettingValue(UserSetting setting, User user) {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    try {
      @Cleanup(value = "commit")
      EntityTransaction et = em.getTransaction();
      et.begin();
      return em.createQuery(
              "select s from UserSettingValue s where userSetting = :sn and user.id = :id",
              UserSettingValue.class)
          .setParameter("sn", setting)
          .setParameter("id", user.getId())
          .getSingleResult()
          .value;
    } catch (NoResultException e) {
      EntityTransaction et = em.getTransaction();
      et.begin();
      UserSettingValue value = new UserSettingValue(user);
      value.value = setting.getDefaultValue();
      value.setUser(user);
      value.setUserSetting(setting);
      em.persist(value);
      em.flush();
      et.commit();
      return loadOrCreateSettingValue(setting, user);
    } finally {
      em.close();
    }
  }

  private static Collection<UserSettingValue> getAllForUser(User user) {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    @Cleanup(value = "commit")
    EntityTransaction et = em.getTransaction();
    et.begin();
    return em.createQuery(
            "select u from UserSettingValue u where u.id = :id", UserSettingValue.class)
        .setParameter("id", user.getId())
        .getResultList();
  }

  public static String getValueFor(User user, UserSetting setting) {
    if (loaded == null || loaded.getId() != user.getId()) {
      loadUser(user);
    }
    String out = values.get(setting);
    if (out == null) {
      out = loadOrCreateSettingValue(setting, user);
      values.put(setting, out);
    }
    return out;
  }

  private static void loadUser(User user) {
    loaded = user;
    values = new HashMap<>(UserSetting.values().length);
    getAllForUser(user).forEach(e -> values.put(e.userSetting, e.value));
  }

  @Override
  public String toString() {
    return "UserSettingValue{"
        + "id="
        + id
        + ", user="
        + user
        + ", userSetting="
        + userSetting
        + ", value='"
        + value
        + '\''
        + '}';
  }

  public static void setValue(User user, UserSetting setting, String value) {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    @Cleanup(value = "commit")
    EntityTransaction et = em.getTransaction();
    et.begin();
    em.createQuery(
            "update UserSettingValue setting set setting.value = :v where user.id = :id and setting.userSetting = :us")
        .setParameter("v", value)
        .setParameter("id", user.getId())
        .setParameter("us", setting)
        .executeUpdate();
    em.flush();
    loadUser(user);
  }

  @Override
  public boolean isInRelation(@NotNull User user) {
    return user.equals(this.user);
  }
}
