package kernbeisser.CustomComponents.TextFields;

public class DoubleParseField extends FilterField {
    private double value = 0;

    DoubleParseField(double min, double max) {
        super(e -> {
            try {
                if (e.equals("")) {
                    return true;
                }
                double v = Double.parseDouble(e);
                return v >= min && v <= max;
            } catch (NumberFormatException ex) {
                return false;
            }
        });
    }

    public DoubleParseField() {
        super(e -> {
            try {
                if (e.equals("")) {
                    return true;
                }
                Double.parseDouble(e);
                return true;
            } catch (NumberFormatException ex) {
                return false;
            }
        });
    }

    public double getValue() {
        return getText().equals("") ? 0 : Double.parseDouble(getText());
    }
}
