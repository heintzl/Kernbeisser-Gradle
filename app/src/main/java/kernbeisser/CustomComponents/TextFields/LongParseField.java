package kernbeisser.CustomComponents.TextFields;

import kernbeisser.Exeptions.IncorrectInput;

public class LongParseField extends FilterField<Long> {
  LongParseField(long min, long max) {
    super(
        e -> {
          try {
            if (e.equals("") || e.equals("-")) {
              return 0L;
            }
            long v = Long.parseLong(e);
            if (v <= min || v >= max) {
              throw new IncorrectInput(
                  "long " + v + " is not between min: " + min + " and max:" + max);
            }
            return v;
          } catch (NumberFormatException ex) {
            throw new IncorrectInput("cannot parse " + e + " to long");
          }
        });
  }

  public LongParseField() {
    this(Long.MIN_VALUE, Long.MAX_VALUE);
  }
}
