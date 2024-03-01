package kernbeisser.Useful;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Locale;

public class Date {

  private static final ZoneId currentZone = ZoneId.systemDefault();
  public static DateTimeFormatter INSTANT_DATE_TIME =
      DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT)
          .withLocale(Locale.getDefault())
          .withZone(currentZone);
  public static DateTimeFormatter INSTANT_TIME =
      DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT)
          .withLocale(Locale.getDefault())
          .withZone(currentZone);
  public static DateTimeFormatter INSTANT_DATE =
      DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)
          .withLocale(Locale.getDefault())
          .withZone(currentZone);
  public static DateTimeFormatter INSTANT_MONTH_YEAR =
      DateTimeFormatter.ofPattern("MMMuu").withLocale(Locale.getDefault()).withZone(currentZone);
  public static DateTimeFormatter INSTANT_CATALOG_DATE =
      DateTimeFormatter.ofPattern("uuuuMMdd").withLocale(Locale.getDefault()).withZone(currentZone);
  public static DateTimeFormatter INSTANT_CATALOG_TIME =
      DateTimeFormatter.ofPattern("HHmm").withLocale(Locale.getDefault()).withZone(currentZone);

  public static String safeDateFormat(Instant instant, DateTimeFormatter formatter) {
    if (instant == null) {
      return "";
    }
    return formatter.format(instant);
  }

  public static Instant atStartOrEndOfDay(LocalDate localDate, boolean atStart) {
    return (atStart
            ? localDate.atStartOfDay(currentZone)
            : localDate.plusDays(1).atStartOfDay(currentZone).minusNanos(1))
        .toInstant();
  }

  public static Instant parseInstantDate(String s, DateTimeFormatter format) {
    if (s.replace(" ", "").isEmpty()) {
      return null;
    }
    return atStartOrEndOfDay(LocalDate.parse(s, format), true);
  }

  public static Instant parseInstantTime(String s, Instant date, DateTimeFormatter format) {
    if (s.replace(" ", "").isEmpty()) {
      return null;
    }
    return LocalTime.parse(s, format)
        .atDate(date.atZone(currentZone).toLocalDate())
        .atZone(currentZone)
        .toInstant();
  }
}
