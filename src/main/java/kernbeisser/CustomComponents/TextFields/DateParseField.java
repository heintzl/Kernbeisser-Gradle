package kernbeisser.CustomComponents.TextFields;

import java.time.Instant;
import kernbeisser.Exeptions.IncorrectInput;
import kernbeisser.Useful.Date;

public class DateParseField extends FilterField<Instant> {
  public DateParseField() {
    super(DateParseField::fromString, true);
  }

  public void setValue(Instant date) {
    setText(Date.INSTANT_DATE.format(date));
  }

  private static Instant fromString(String s) throws IncorrectInput {
    return Instant.from(Date.INSTANT_DATE.parse(s));
  }
}
