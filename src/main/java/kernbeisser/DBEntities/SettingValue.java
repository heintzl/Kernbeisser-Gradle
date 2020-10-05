package kernbeisser.DBEntities;

import java.util.HashMap;
import java.util.List;
import javax.persistence.*;
import kernbeisser.DBConnection.DBConnection;
import kernbeisser.Enums.PermissionKey;
import kernbeisser.Enums.Setting;
import kernbeisser.Security.Key;
import kernbeisser.Useful.Tools;
import lombok.Cleanup;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table
@EqualsAndHashCode(doNotUseGetters = true)
public class SettingValue {

  @Getter(lazy = true)
  private static final HashMap<Setting, String> settingValueHashMap = load();

  private static HashMap<Setting, String> load() {
    HashMap<Setting, String> out = new HashMap<>();
    getAll(null).forEach(e -> out.put(e.setting, e.value));
    return out;
  }

  @Id
  @GeneratedValue
  @Getter(onMethod_ = {@Key(PermissionKey.SETTING_VALUE_ID_READ)})
  @Setter(onMethod_ = {@Key(PermissionKey.SETTING_VALUE_ID_WRITE)})
  private int id;

  @Column
  @Enumerated(value = EnumType.STRING)
  @Getter(onMethod_ = {@Key(PermissionKey.SETTING_VALUE_SETTING_READ)})
  @Setter(onMethod_ = {@Key(PermissionKey.SETTING_VALUE_SETTING_WRITE)})
  private Setting setting;

  @Column
  @Getter(onMethod_ = {@Key(PermissionKey.SETTING_VALUE_VALUE_READ)})
  @Setter(onMethod_ = {@Key(PermissionKey.SETTING_VALUE_VALUE_WRITE)})
  private String value;

  public static String getValue(Setting s) {
    String out = getSettingValueHashMap().get(s);
    return out == null ? s.getDefaultValue() : out;
  }

  public static void setValue(Setting setting, String value) {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    EntityTransaction et = em.getTransaction();
    et.begin();
    try {
      SettingValue settingValue =
          em.createQuery(
                  "select s from SettingValue s where s.setting = :setting", SettingValue.class)
              .setParameter("setting", setting)
              .getSingleResult();
      settingValue.setValue(value);
      em.persist(settingValue);
    } catch (NoResultException noResultException) {
      SettingValue settingValue = new SettingValue();
      settingValue.setting = setting;
      settingValue.value = value;
      em.persist(settingValue);
    }
    et.commit();
    em.close();
    getSettingValueHashMap().replace(setting, value);
  }

  public static List<SettingValue> getAll(String condition) {
    return Tools.getAll(SettingValue.class, condition);
  }

  @Override
  public String toString() {
    return "SettingValue{" + "id=" + id + ", setting=" + setting + ", value='" + value + '\'' + '}';
  }
}
