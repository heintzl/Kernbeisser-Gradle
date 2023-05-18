package kernbeisser.Enums;

import java.awt.event.KeyEvent;
import java.lang.reflect.Field;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Map;
import javax.swing.*;
import kernbeisser.DBEntities.SettingValue;
import kernbeisser.Main;
import kernbeisser.Useful.DelegatingMap;
import kernbeisser.Useful.Tools;
import org.apache.commons.lang3.Range;
import org.jetbrains.annotations.NotNull;

public enum Setting {
  // removed some duplicated enum constrains Permission
  DB_INITIALIZED("false"),
  VAT_LOW("0.07") {
    @Override
    public void changeValue(Object s) {
      super.changeValue(s);
      JOptionPane.showMessageDialog(null, "Programm bitte neu starten, um MwSt. zu aktualisieren");
    }
  },
  VAT_HIGH("0.19") {
    @Override
    public void changeValue(Object s) {
      super.changeValue(s);
      JOptionPane.showMessageDialog(null, "Programm bitte neu starten, um MwSt. zu aktualisieren");
    }
  },
  DEFAULT_MAX_SEARCH("500"),
  CONTAINER_SURCHARGE_REDUCTION("0.5"),
  SURCHARGE_DEFAULT("0.18"),
  SURCHARGE_BAKERY("0.13"),
  SURCHARGE_PRODUCE("0.23"),
  DEFAULT_THEME(Theme.LIGHT),
  INFO_LINE_LAST_CATALOG("notDefined"),
  UPDATE_CATALOG_FROM_INTERNET("false"),
  MIN_PASSWORD_LENGTH("5"),
  MIN_REQUIRED_PASSWORD_STRENGTH("3"),
  HASH_COSTS("12"),
  FORCE_PASSWORD_CHANGE_AFTER("365"),
  DEFAULT_MIN_VALUE("0."),
  CREDIT_WARNING_THRESHOLD("20"),
  SCANNER_PREFIX_KEY("VK_F12"),
  SCANNER_SUFFIX_KEY("VK_END"),
  SCANNER_TIMEOUT("50"),
  APP_DEFAULT_WIDTH("1600"),
  APP_DEFAULT_HEIGHT("1000"),
  SUBWINDOW_SIZE_FACTOR("0.85"),
  CATALOG_RUN_GC_UNDER("20"),
  LABEL_SCALE_FACTOR("1.5"),
  WARN_OVER_TRANSACTION_VALUE("1000."),
  SHOPPING_PRICE_WARNING_THRESHOLD("200."),
  SHOPPING_AMOUNT_WARNING_THRESHOLD("30"),
  OPEN_MULTIPLE_SHOPPING_MASK("true"),
  DAYS_BEFORE_INACTIVITY("180"),
  PRINTER("PDF-Export"),
  ALTERNATIVE_A5_PRINTER(""),
  DUPLEX_PRINTER_SUFFIX(""),
  LAST_EXPORTED_PREORDER_NR("0"),
  USER_BALANCE_REPORT_INTERVAL("28"),
  LAST_USER_BALANCE_REPORT("2010-01-01T00:00:00.000Z"),
  LABELS_PER_PAGE("40"),
  STORE_NAME("Kernbeißer"),
  REPORT_FOOTLINE(
      "Kernbeißer Verbraucher-Erzeuger-Genossenschaft eG, Bültenweg 71, 38106 Braunschweig, Steuer-Nr.:  14/201/08813"),
  OFFER_PREFIX("*AK* "),
  PREORDER_RETARD_THRESHOLD("6"),
  WARN_ARTICLE_DIFFERENCE("10"),
  VALIDATE_EMAIL_SYNTAX("false"),
  IS_DEFAULT_SURCHARGES("true"),
  PASSWORD_TOKEN_GENERATION_LENGTH("8"),
  // Ununsed
  GENERATE_PASSWORD_RELATED_TO_USERNAME("false"),
  KK_SUPPLIER_FILE_REGEX_MATCH("BR[0-9]+\\.[0-9]{3}"),
  KK_SUPPLY_MAX_FILE_TRANSFER_DURATION("3600L"),
  KK_SUPPLY_FROM_TIME("14"),
  KK_SUPPLY_TO_TIME("24"),
  KK_SUPPLY_DAY_OF_WEEK(DayOfWeek.TUESDAY),
  KK_SUPPLY_PRODUCE_SUPPLIER_ITEM_NUMBER_RANGE("700000/799999"),
  // not in use
  KK_SUPPLY_DIR("deprecated"),
  COMBO_BOX_SEARCH_TIMEOUT("500"),
  INVENTORY_COUNTING_EXPIRE_HOURS("48"),
  INVENTORY_INACTIVE_ARTICLE("365"),
  INVENTORY_LOOK_FOR_ARTICLE_USAGE("true"),

  INVENTORY_SCHEDULED_DATE("2022-12-30"),

  @Deprecated
  SUPPLY_DAY_DIFFERENCE_THRESHOLD("1");

  // defines the type like in java style
  // Value: Type:
  // 0.0    double
  // 0.0f   float
  // 0      int
  // 0L     long
  // any    String
  private String value;
  private final String defaultValue;
  private final boolean requiresPersistedDefaultValue;

  static {
    ignoreDeprecatedDBSettings();
  }

  public static final void ignoreDeprecatedDBSettings() {
    try {
      Field field = Class.class.getDeclaredField("enumConstantDirectory");
      field.setAccessible(true);
      Setting.valueOf(Setting.DB_INITIALIZED.name());
      Map<String, Setting> map = (Map<String, Setting>) field.get(Setting.class);
      Map<String, Setting> delegatingMap =
          new DelegatingMap<String, Setting>(map) {
            @Override
            public Setting get(Object key) {
              Setting result = super.get(key);
              if (result == null) {
                Main.logger.info("found outdated Setting name: " + key);
                return Tools.createWithoutConstructor(Setting.class);
              }
              return result;
            }
          };
      field.set(Setting.class, delegatingMap);
    } catch (NoSuchFieldException | IllegalAccessException e) {
      Tools.showUnexpectedErrorWarning(e);
    }
  }

  Setting(String defaultValue) {
    this(defaultValue, false);
  }

  Setting(@NotNull Enum<?> e) {
    this(e.name());
  }

  Setting(String defaultValue, boolean requiresPersistedDefaultValue) {
    this.requiresPersistedDefaultValue = requiresPersistedDefaultValue;
    this.defaultValue = defaultValue;
  }

  public String getStringValue() {
    return getValue();
  }

  public double getDoubleValue() {
    try {
      return Double.parseDouble(getValue());
    } catch (NumberFormatException e) {
      Tools.showUnexpectedErrorWarning(e);
      StackTraceElement element = Tools.getCallerStackTraceElement(1);
      Main.logger.error(
          element.getClassName()
              + "::"
              + element.getMethodName()
              + " requires double value Setting["
              + toString()
              + "] has the value '"
              + getStringValue()
              + "' which cant be interpreted as an integer");
      throw new NumberFormatException();
    }
  }

  public int getIntValue() {
    try {
      return Integer.parseInt(getValue());
    } catch (NumberFormatException e) {
      if (getValue().endsWith("L")) return (int) getLongValue();
      Tools.showUnexpectedErrorWarning(e);
      StackTraceElement element = Tools.getCallerStackTraceElement(1);
      Main.logger.error(
          element.getClassName()
              + "::"
              + element.getMethodName()
              + " requires integer value Setting["
              + toString()
              + "] has the value '"
              + getStringValue()
              + "' which cant be interpreted as an integer");
      throw new NumberFormatException();
    }
  }

  public long getLongValue() {
    try {
      return Long.parseLong((getValue().replace("l", "L").replace("L", "")));
    } catch (NumberFormatException e) {
      e.printStackTrace();
      Tools.showUnexpectedErrorWarning(e);
      StackTraceElement element = Tools.getCallerStackTraceElement(1);
      Main.logger.error(
          element.getClassName()
              + "::"
              + element.getMethodName()
              + " requires long value Setting["
              + toString()
              + "] has the value '"
              + getStringValue()
              + "' which cant be interpreted as an long");
      throw new NumberFormatException();
    }
  }

  public float getFloatValue() {
    try {
      return Float.parseFloat(getValue());
    } catch (NumberFormatException e) {
      Tools.showUnexpectedErrorWarning(e);
      StackTraceElement element = Tools.getCallerStackTraceElement(1);
      Main.logger.error(
          element.getClassName()
              + "::"
              + element.getMethodName()
              + " requires float value Setting["
              + toString()
              + "] has the value '"
              + getStringValue()
              + "' which cant be interpreted as an integer");
      throw new NumberFormatException();
    }
  }

  public LocalDate getDateValue() {
    return LocalDate.parse(getValue());
  }

  public int getKeyEventValue() {
    int vKey = 0;
    try {
      vKey = KeyEvent.class.getDeclaredField(getValue()).getInt(null);
    } catch (IllegalAccessException | NoSuchFieldException e) {
      e.printStackTrace();
    }
    return vKey;
  }

  public <T extends Enum<T>> T getEnumValue(Class<T> c) {
    return Enum.valueOf(c, getValue());
  }

  public String getDefaultValue() {
    return defaultValue;
  }

  public boolean getBooleanValue() {
    try {
      return Boolean.parseBoolean(getValue());
    } catch (NumberFormatException e) {
      Tools.showUnexpectedErrorWarning(e);
      StackTraceElement element = Tools.getCallerStackTraceElement(1);
      Main.logger.error(
          element.getClassName()
              + "::"
              + element.getMethodName()
              + " requires boolean value Setting["
              + toString()
              + "] has the value '"
              + getStringValue()
              + "' which cant be interpreted as an integer");
      throw new NumberFormatException();
    }
  }

  public void changeValue(Object s) {
    SettingValue.setValue(this, String.valueOf(s));
    value = String.valueOf(s);
  }

  public static Class<?> getExpectedType(@NotNull Setting setting) {
    if (setting.getDefaultValue().matches("\\d+")) {
      return Integer.class;
    }
    if (setting.getDefaultValue().matches("\\d+[.]\\d*")) {
      return Double.class;
    }
    if (setting.getDefaultValue().matches("\\d+[Ll]")) {
      return Long.class;
    }
    if (setting.getDefaultValue().matches("\\d*[.]\\d*[Ff]")) {
      return Float.class;
    }
    if (setting.getDefaultValue().equals("false") || setting.getDefaultValue().equals("true")) {
      return Boolean.class;
    }
    if (setting.getDefaultValue().matches("\\d{4}-\\d{2}-\\d{2}")) {
      return LocalDate.class;
    }
    return String.class;
  }

  public String getValue() {
    return (this.value =
        this.value == null ? SettingValue.getValue(this, requiresPersistedDefaultValue) : value);
  }

  public Range<Integer> getIntRange() {
    try {
      String[] parts = getValue().split("/");
      return Range.between(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]));
    } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
      Tools.showUnexpectedErrorWarning(
          new RuntimeException(
              "Setting: "
                  + this
                  + " has a value "
                  + getValue()
                  + " the requested type was a range example -100/100"));
      throw e;
    }
  }
}
