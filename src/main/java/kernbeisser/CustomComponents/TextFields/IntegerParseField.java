package kernbeisser.CustomComponents.TextFields;

import java.util.function.Function;

public class IntegerParseField extends FilterField {
    IntegerParseField(int max, int min) {
        super(e -> {
            try {
                if (e.equals("")) {
                    return true;
                }
                int v = Integer.parseInt(e);
                return v >= min && v <= max;
            } catch (NumberFormatException ex) {
                return false;
            }
        });
    }

    public IntegerParseField() {
        super(e -> {
            try {
                if (e.equals("")) {
                    return true;
                }
                Integer.parseInt(e);
                return true;
            } catch (NumberFormatException ex) {
                return false;
            }
        });
    }

    public int getValue() {
        return getText().equals("") ? 0 : Integer.parseInt(getText());
    }
}
