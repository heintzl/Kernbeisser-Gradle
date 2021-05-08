package kernbeisser.CustomComponents.TextFields;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import kernbeisser.Exeptions.IncorrectInput;
import kernbeisser.Useful.Date;
import lombok.var;

public class DateParseField extends FilterField<Instant> {

  public DateParseField(boolean atStartOfDay) {
    super((s) -> fromString(s, atStartOfDay), true);
  }

  public void setValue(Instant date) {
    setText(Date.INSTANT_DATE.format(date));
  }

  private static Instant fromString(String s, boolean atStartOfDay) throws IncorrectInput {
    var localDate = LocalDate.from(Date.INSTANT_DATE.parse(s));
    var dateTime =
        atStartOfDay
            ? localDate.atStartOfDay(ZoneId.systemDefault())
            : localDate.plusDays(1).atStartOfDay(ZoneId.systemDefault()).minusNanos(1);
    return dateTime.toInstant();
  }
}
