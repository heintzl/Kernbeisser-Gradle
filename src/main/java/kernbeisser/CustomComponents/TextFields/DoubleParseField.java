package kernbeisser.CustomComponents.TextFields;

import kernbeisser.Exeptions.IncorrectInput;

public class DoubleParseField extends FilterField<Double> {
  DoubleParseField(double min, double max) {
    super(
        e -> {
          try {
            if (e.equals("") || e.equals("-")) {
              return 0.;
            }
            double v = Double.parseDouble(e.replace(",", "."));
            if (v < min || v > max) {
              throw new IncorrectInput(
                  "double " + v + " is not between min " + min + " and max" + max);
            }
            return v;
          } catch (NumberFormatException ex) {
            throw new IncorrectInput("cannot extract double from: '" + e + "'");
          }
        });
  }

  public DoubleParseField() {
    this(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
  }
}
