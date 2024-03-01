package kernbeisser.DBEntities;

import jakarta.persistence.*;
import java.util.HashMap;
import java.util.List;
import kernbeisser.DBConnection.DBConnection;
import kernbeisser.DBEntities.Converters.SettingValueConverter;
import kernbeisser.Enums.Setting;
import kernbeisser.Useful.Tools;
import lombok.Cleanup;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import rs.groump.Key;
import rs.groump.PermissionKey;

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
  @Convert(converter = SettingValueConverter.class)
  @Getter(onMethod_ = {@Key(PermissionKey.SETTING_VALUE_SETTING_READ)})
  @Setter(onMethod_ = {@Key(PermissionKey.SETTING_VALUE_SETTING_WRITE)})
  private Setting setting;

  @Column
  @Getter(onMethod_ = {@Key(PermissionKey.SETTING_VALUE_VALUE_READ)})
  @Setter(onMethod_ = {@Key(PermissionKey.SETTING_VALUE_VALUE_WRITE)})
  private String value;

  public static String getValue(Setting s, boolean requiresPersistedDefaultValue) {
    String out = getSettingValueHashMap().get(s);
    if (requiresPersistedDefaultValue && out == null) {
      setValue(s, s.getDefaultValue());
      out = s.getDefaultValue();
    }
    return out == null ? s.getDefaultValue() : out;
  }

  public static void setValue(Setting setting, String value) {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    @Cleanup(value = "commit")
    EntityTransaction et = em.getTransaction();
    et.begin();
    try {
      SettingValue settingValue =
          em.createQuery(
                  "select s from SettingValue s where s.setting = :setting", SettingValue.class)
              .setParameter("setting", setting)
              .getSingleResult();
      settingValue.value = value;
      em.persist(settingValue);
    } catch (NoResultException noResultException) {
      SettingValue settingValue = new SettingValue();
      settingValue.setting = setting;
      settingValue.value = value;
      em.persist(settingValue);
    }
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
