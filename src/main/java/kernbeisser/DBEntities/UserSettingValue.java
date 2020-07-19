package kernbeisser.DBEntities;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import javax.persistence.*;
import kernbeisser.DBConnection.DBConnection;
import kernbeisser.Enums.PermissionKey;
import kernbeisser.Enums.UserSetting;
import kernbeisser.Security.Key;
import kernbeisser.Useful.Tools;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table
public class UserSettingValue {
  private static User loaded;
  private static HashMap<UserSetting, String> values;

  @Id
  @GeneratedValue
  @Getter(onMethod_ = {@Key(PermissionKey.USER_SETTING_VALUE_ID_READ)})
  @Setter(onMethod_ = {@Key(PermissionKey.USER_SETTING_VALUE_ID_WRITE)})
  private int id;

  @JoinColumn
  @ManyToOne
  @Getter(onMethod_ = {@Key(PermissionKey.USER_SETTING_VALUE_ID_READ)})
  @Setter(onMethod_ = {@Key(PermissionKey.USER_SETTING_VALUE_ID_WRITE)})
  private User user;

  @Column
  @Enumerated(EnumType.STRING)
  @Getter(onMethod_ = {@Key(PermissionKey.USER_SETTING_VALUE_ID_READ)})
  @Setter(onMethod_ = {@Key(PermissionKey.USER_SETTING_VALUE_ID_WRITE)})
  private UserSetting userSetting;

  @Column
  @Getter(onMethod_ = {@Key(PermissionKey.USER_SETTING_VALUE_ID_READ)})
  @Setter(onMethod_ = {@Key(PermissionKey.USER_SETTING_VALUE_ID_WRITE)})
  private String value;

  public static List<UserSettingValue> getAll(String condition) {
    return Tools.getAll(UserSettingValue.class, condition);
  }

  private static String loadOrCreateSettingValue(UserSetting setting, User user) {
    EntityManager em = DBConnection.getEntityManager();
    try {
      return em.createQuery(
              "select s from UserSettingValue s where userSetting = :sn and user.id = :uid",
              UserSettingValue.class)
          .setParameter("sn", setting)
          .setParameter("uid", user.getId())
          .getSingleResult()
          .value;
    } catch (NoResultException e) {
      EntityTransaction et = em.getTransaction();
      et.begin();
      UserSettingValue value = new UserSettingValue();
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
    EntityManager em = DBConnection.getEntityManager();
    List<UserSettingValue> out =
        em.createQuery("select u from UserSettingValue u where u.id = :uid", UserSettingValue.class)
            .setParameter("uid", user.getId())
            .getResultList();
    em.close();
    return out;
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

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    UserSettingValue that = (UserSettingValue) o;
    return id == that.id
        && user.equals(that.user)
        && userSetting == that.userSetting
        && value.equals(that.value);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, user, userSetting, value);
  }
}
