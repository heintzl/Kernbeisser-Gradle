package kernbeisser.CustomComponents.TextFields;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class DateParseField extends FilterField {
    public DateParseField(){
        super(e -> fromString(e) != null);
    }

    public LocalDate getValue(){
        return fromString(getText());
    }

    public void setValue(LocalDate date){
        setText(date.getDayOfMonth()+"."+date.getMonth().getValue()+"."+date.getYear());
    }

    private static LocalDate fromString(String s){
        String[] parts = s.split("[/_,*+|\\- .]");
        if(parts.length!=3)return null;
        try {
            return LocalDate.of(Integer.parseInt(parts[2]),Integer.parseInt(parts[1]),Integer.parseInt(parts[0]));
        }catch (NumberFormatException | DateTimeException e){
            return null;
        }
    }
}
