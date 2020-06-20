package kernbeisser.Enums;

import kernbeisser.DBEntities.SettingValue;

public enum Setting {
    DB_VERSION("0.1.0j"),
    DB_INITIALIZED("false"),
    VAT_LOW("0.07"),
    VAT_HIGH("0.19"),
    DEFAULT_MAX_SEARCH("500"),
    CONTAINER_SURCHARGE_REDUCTION ("0.5"),
    DEFAULT_THEME(Theme.LIGHT.name()),
    INFO_LINE_LAST_CATALOG("notDefined"),
    UPDATE_CATALOG_FROM_INTERNET("false"),
    MIN_PASSWORD_LENGTH("5"),
    MIN_REQUIRED_PASSWORD_STRENGTH("3"),
    HASH_COSTS("12"),
    FORCE_PASSWORD_CHANGE_AFTER("365"),
    DEFAULT_MIN_VALUE("0.")
    ;

    private final String defaultValue;

    Setting(String defaultValue){
        this.defaultValue = defaultValue;
    }

    public String getStringValue(){
        return SettingValue.getValue(this);
    }
    public double getDoubleValue(){
        return Double.parseDouble(SettingValue.getValue(this));
    }
    public int getIntValue(){
        return Integer.parseInt(SettingValue.getValue(this));
    }
    public long getLongValue(){
        return Long.parseLong(SettingValue.getValue(this));
    }
    public float getFloatValue(){
        return Float.parseFloat(SettingValue.getValue(this));
    }
    public <T extends Enum<T>> T getEnumValue(Class<T> c){return Enum.valueOf(c,SettingValue.getValue(this));}

    public String getDefaultValue() {
        return defaultValue;
    }

    public boolean getBooleanValue() {
        return Boolean.parseBoolean(SettingValue.getValue(this));
    }

    public void setValue(Object s) {
        SettingValue.setValue(this,String.valueOf(s));
    }
}
