package kernbeisser.Enums;

import kernbeisser.DBEntities.SettingValue;

public enum Setting {
    VAT_LOW("0.07"),
    VAT_HIGH("0.19")
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

    public String getDefaultValue() {
        return defaultValue;
    }
}
