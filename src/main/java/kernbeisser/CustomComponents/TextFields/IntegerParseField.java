package kernbeisser.CustomComponents.TextFields;

import java.util.function.Function;

public class IntegerParseField extends FilterField{
    IntegerParseField(int max,int min) {
        super(e -> {
            try{
                int v = Integer.parseInt(e);
                return v >= min && v <= max;
            }catch (NumberFormatException ex){
                return false;
            }
        });
    }
    IntegerParseField(){
        super(e -> {
            try{
                Integer.parseInt(e);
                return true;
            }catch (NumberFormatException ex){
                return false;
            }
        });
    }
}
