package kernbeisser.CustomComponents.TextFields;

import javax.swing.*;
import javax.swing.text.*;

public class DoubleParseField extends FilterField {
    private double value = 0;
    DoubleParseField(double min,double max){
        super(e -> {
            try{
                double v = Double.parseDouble(e);
                return v >= min && v <= max;
            }catch (NumberFormatException ex){
                return false;
            }
        });
    }

    public DoubleParseField(){
        super(e -> {
            try{
                Double.parseDouble(e);
                return true;
            }catch (NumberFormatException ex){
                return false;
            }
        });
    }

    public double getValue(){
        return Double.parseDouble(getText());
    }
}
