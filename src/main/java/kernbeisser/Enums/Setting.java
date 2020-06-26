package kernbeisser.Enums;

import kernbeisser.DBEntities.SettingValue;
import kernbeisser.Main;
import kernbeisser.Useful.Tools;

public enum Setting {
    DB_VERSION("0.1.0"),
    DB_INITIALIZED("false"),
    VAT_LOW("0.07"),
    VAT_HIGH("0.19"),
    DEFAULT_MAX_SEARCH("500"),
    CONTAINER_SURCHARGE_REDUCTION ("0.5"),
    DEFAULT_THEME(Theme.LIGHT),
    INFO_LINE_LAST_CATALOG("notDefined"),
    UPDATE_CATALOG_FROM_INTERNET("false"),
    MIN_PASSWORD_LENGTH("5"),
    MIN_REQUIRED_PASSWORD_STRENGTH("3"),
    HASH_COSTS("12"),
    FORCE_PASSWORD_CHANGE_AFTER("365"),
    DEFAULT_MIN_VALUE("0.")
    ;


    //defines the type to like in java style
    //Value: Type:
    //0.0    double
    //0.0f   float
    //0      int
    //0L     long
    //any    String
    private final String defaultValue;

    Setting(String defaultValue){
        this.defaultValue = defaultValue;
    }

    Setting(Enum<?> e){
        this.defaultValue = e.name();
    }

    public String getStringValue(){
        return SettingValue.getValue(this);
    }
    public double getDoubleValue(){
        try{
            return Double.parseDouble(SettingValue.getValue(this));
        }catch (NumberFormatException e){
            Tools.showUnexpectedErrorWarning(e);
            StackTraceElement element = Tools.getCallerStackTraceElement(1);
            Main.logger.error(element.getClassName()+"::"+element.getMethodName()+" requires double value Setting["+toString()+"] has the value '"+getStringValue()+"' which cant be interpreted as an integer");
            throw new NumberFormatException();
        }
    }

    public int getIntValue(){
        try{
            return Integer.parseInt(SettingValue.getValue(this));
        }catch (NumberFormatException e){
            Tools.showUnexpectedErrorWarning(e);
            StackTraceElement element = Tools.getCallerStackTraceElement(1);
            Main.logger.error(element.getClassName()+"::"+element.getMethodName()+" requires integer value Setting["+toString()+"] has the value '"+getStringValue()+"' which cant be interpreted as an integer");
            throw new NumberFormatException();
        }
    }
    public long getLongValue(){
        try{
            return Long.parseLong(SettingValue.getValue(this));
        }catch (NumberFormatException e){
            Tools.showUnexpectedErrorWarning(e);
            StackTraceElement element = Tools.getCallerStackTraceElement(1);
            Main.logger.error(element.getClassName()+"::"+element.getMethodName()+" requires long value Setting["+toString()+"] has the value '"+getStringValue()+"' which cant be interpreted as an integer");
            throw new NumberFormatException();
        }
    }
    public float getFloatValue(){
        try{
            return Float.parseFloat(SettingValue.getValue(this));
        }catch (NumberFormatException e){
            Tools.showUnexpectedErrorWarning(e);
            StackTraceElement element = Tools.getCallerStackTraceElement(1);
            Main.logger.error(element.getClassName()+"::"+element.getMethodName()+" requires float value Setting["+toString()+"] has the value '"+getStringValue()+"' which cant be interpreted as an integer");
            throw new NumberFormatException();
        }
    }
    public <T extends Enum<T>> T getEnumValue(Class<T> c){return Enum.valueOf(c,SettingValue.getValue(this));}
    public String getDefaultValue() {
        return defaultValue;
    }
    public boolean getBooleanValue() {
        try{
            return Boolean.parseBoolean(SettingValue.getValue(this));
        }catch (NumberFormatException e){
            Tools.showUnexpectedErrorWarning(e);
            StackTraceElement element = Tools.getCallerStackTraceElement(1);
            Main.logger.error(element.getClassName()+"::"+element.getMethodName()+" requires boolean value Setting["+toString()+"] has the value '"+getStringValue()+"' which cant be interpreted as an integer");
            throw new NumberFormatException();
        }
    }
    public void setValue(Object s) {
        SettingValue.setValue(this,String.valueOf(s));
    }

    public static Class<?> getExpectedType(Setting setting){
        if (setting.getDefaultValue().matches("\\d*")) return Integer.class;
        if (setting.getDefaultValue().matches("\\d+[.]\\d*")) return Double.class;
        if (setting.getDefaultValue().matches("\\d+[Ll]")) return Long.class;
        if (setting.getDefaultValue().matches("\\d*[.]\\d*[Ff]")) return Float.class;
        if(setting.getDefaultValue().equals("false") || setting.getDefaultValue().equals("true"))return Boolean.class;
        return String.class;
    }
}
