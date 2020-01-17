package kernbeisser.CustomComponents.TextFields;

public class LongParseField extends FilterField{
    LongParseField(long min,long max){
        super(e -> {
            try{
                long v = Long.parseLong(e);
                return v >= min && v <= max;
            }catch (NumberFormatException ex){
                return false;
            }
        });
    }
    public LongParseField(){
        super(e -> {
            try{
                Long.parseLong(e);
                return true;
            }catch (NumberFormatException ex){
                return false;
            }
        });
    }
    public long getValue(){
        return Long.parseLong(getText());
    }
}
