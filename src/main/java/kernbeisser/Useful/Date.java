package kernbeisser.Useful;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Locale;

public class Date {
  public static DateTimeFormatter INSTANT_DATE_TIME =
      DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT)
          .withLocale(Locale.GERMANY)
          .withZone(ZoneId.systemDefault());
  public static DateTimeFormatter INSTANT_DATE =
      DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)
          .withLocale(Locale.GERMANY)
          .withZone(ZoneId.systemDefault());
  public static DateTimeFormatter INSTANT_MONTH_YEAR =
      DateTimeFormatter.ofPattern("MMMuu")
          .withLocale(Locale.GERMANY)
          .withZone(ZoneId.systemDefault());
}
