package kernbeisser.Enums;

import java.awt.event.KeyEvent;
import javax.swing.*;
import kernbeisser.DBEntities.SettingValue;
import kernbeisser.Main;
import kernbeisser.Useful.Tools;
import org.jetbrains.annotations.NotNull;

public enum Setting {
  DB_VERSION("0.2.4"),
  DB_INITIALIZED("false"),
  VAT_LOW("0.07") {
    @Override
    public void changeValue(Object s) {
      super.changeValue(s);
      JOptionPane.showMessageDialog(
          null, "Bitte starten sie das Programm neu um mwSt. zu aktualiesieren");
    }
  },
  VAT_HIGH("0.19") {
    @Override
    public void changeValue(Object s) {
      super.changeValue(s);
      JOptionPane.showMessageDialog(
          null, "Bitte starten sie das Programm neu um mwSt. zu aktualiesieren");
    }
  },
  DEFAULT_MAX_SEARCH("500"),
  CONTAINER_SURCHARGE_REDUCTION("0.5"),
  SURCHARGE_DEFAULT("0.25"),
  SURCHARGE_BAKERY("0.17"),
  SURCHARGE_PRODUCE("0.22"),
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
  CATALOG_RUN_GC_UNDER("20"),
  LABEL_SCALE_FACTOR("1.5"),
  WARN_OVER_TRANSACTION_VALUE("1000."),
  OPEN_MULTIPLE_SHOPPING_MASK("true"),
  PRINTER("OS_default");

  // defines the type to like in java style
  // Value: Type:
  // 0.0    double
  // 0.0f   float
  // 0      int
  // 0L     long
  // any    String
  private String value;
  private final String defaultValue;

  Setting(String defaultValue) {
    this.defaultValue = defaultValue;
  }

  Setting(@NotNull Enum<?> e) {
    this(e.name());
  }

  public String getStringValue() {
    return SettingValue.getValue(this);
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
      return Long.parseLong((getValue()));
    } catch (NumberFormatException e) {
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
              + "' which cant be interpreted as an integer");
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
      return Boolean.parseBoolean(SettingValue.getValue(this));
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

  public void setValue(Object s) {
    value = String.valueOf(s);
  }

  public static Class<?> getExpectedType(@NotNull Setting setting) {
    if (setting.getDefaultValue().matches("\\d*")) {
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
    return String.class;
  }

  public String getValue() {
    return (this.value = this.value == null ? SettingValue.getValue(this) : value);
  }
}
