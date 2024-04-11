package kernbeisser.DBEntities;

import com.google.common.collect.ImmutableMap;
import jakarta.persistence.*;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import kernbeisser.DBConnection.DBConnection;
import kernbeisser.DBConnection.QueryBuilder;
import kernbeisser.DBEntities.TypeFields.SystemSettingField;
import kernbeisser.VersionIntegrationTools.Version;
import lombok.Cleanup;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import rs.groump.Access;
import rs.groump.Key;
import rs.groump.PermissionKey;

@Entity
@Table
@EqualsAndHashCode(doNotUseGetters = true)
public class SystemSetting {

  @Getter(lazy = true)
  private static final Map<String, String> valueHashMap = load();

  private static Map<String, String> load() {
    return Access.runUnchecked(
        () ->
            QueryBuilder.selectAll(SystemSetting.class).getResultList().stream()
                .collect(Collectors.toMap(SystemSetting::getSetting, SystemSetting::getValue)));
  }

  @Id
  @GeneratedValue
  @Getter(onMethod_ = {@Key(PermissionKey.SETTING_VALUE_ID_READ)})
  @Setter(onMethod_ = {@Key(PermissionKey.SETTING_VALUE_ID_WRITE)})
  private int id;

  @Column
  @Getter(onMethod_ = {@Key(PermissionKey.SETTING_VALUE_SETTING_READ)})
  @Setter(onMethod_ = {@Key(PermissionKey.SETTING_VALUE_SETTING_WRITE)})
  private String setting;

  @Column
  @Getter(onMethod_ = {@Key(PermissionKey.SETTING_VALUE_VALUE_READ)})
  @Setter(onMethod_ = {@Key(PermissionKey.SETTING_VALUE_VALUE_WRITE)})
  private String value;

  public static final String DB_VERSION = "DB_VERSION";
  private static final Map<String, String> defaultValue =
      ImmutableMap.<String, String>builder()
          .put("DB_VERSION", Version.newestVersion().name())
          .build();

  public static String getValue(String key) {
    String out = getValueHashMap().get(key);
    if (out == null) {
      out =
          Objects.requireNonNull(
              defaultValue.get(key), "Missing default value for system setting <" + key + ">");
      setValue(key, out);
    }
    return out;
  }

  public static void setValue(String key, String value) {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    @Cleanup(value = "commit")
    EntityTransaction et = em.getTransaction();
    et.begin();
    try {
      SystemSetting settingValue =
          QueryBuilder.selectAll(SystemSetting.class)
              .where(SystemSettingField.setting.eq(key))
              .getSingleResult(em);
      settingValue.value = value;
      em.persist(settingValue);
    } catch (NoResultException noResultException) {
      SystemSetting settingValue = new SystemSetting();
      settingValue.setting = key;
      settingValue.value = value;
      em.persist(settingValue);
    }
    getValueHashMap().replace(key, value);
  }

  @Override
  public String toString() {
    return "SettingValue{" + " key=" + setting + ", value='" + value + '\'' + '}';
  }
}
