package kernbeisser.DBEntities;

import jakarta.persistence.*;
import java.util.Collection;
import java.util.HashMap;
import kernbeisser.DBConnection.DBConnection;
import kernbeisser.DBConnection.QueryBuilder;
import kernbeisser.Enums.UserSetting;
import kernbeisser.Security.Access.UserRelated;
import lombok.Cleanup;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import rs.groump.Key;
import rs.groump.PermissionKey;

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

  private static Collection<UserSettingValue> getAllForUser(User user) {
    return QueryBuilder.selectAll(UserSettingValue.class)
        .where(UserSettingValue_.user.eq(user))
        .getResultList();
  }

  public static String getValueFor(User user, UserSetting setting) {
    if (loaded == null || !loaded.equals(user)) {
      loadUser(user);
    }
    String out = values.get(setting);
    return out != null ? out : setting.getDefaultValue();
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
    UserSettingValue usv =
        QueryBuilder.selectAll(UserSettingValue.class)
            .where(UserSettingValue_.user.eq(user), UserSettingValue_.userSetting.eq(setting))
            .getSingleResultOptional(em)
            .orElseGet(
                () -> {
                  UserSettingValue newUsv = new UserSettingValue();
                  newUsv.user = user;
                  newUsv.userSetting = setting;
                  return newUsv;
                });
    usv.setValue(value);
    em.persist(usv);
    if (loaded != null && loaded.getId() == user.getId()) values.put(setting, value);
  }

  @Override
  public boolean isInRelation(@NotNull User user) {
    return user.getId() == this.user.getId();
  }
}
