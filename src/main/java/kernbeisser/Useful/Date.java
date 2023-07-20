package kernbeisser.Useful;

import java.time.*;
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
  public static DateTimeFormatter INSTANT_CATALOG_DATE =
      DateTimeFormatter.ofPattern("uuuuMMdd")
          .withLocale(Locale.GERMANY)
          .withZone(ZoneId.systemDefault());
  public static DateTimeFormatter INSTANT_CATALOG_TIME =
      DateTimeFormatter.ofPattern("HHmm")
          .withLocale(Locale.GERMANY)
          .withZone(ZoneId.systemDefault());

  public static String safeDateFormat(Instant instant, DateTimeFormatter formatter) {
    if (instant == null) {
      return "";
    }
    return formatter.format(instant);
  }

  public static Instant atStartOrEndOfDay(LocalDate localDate, boolean atStart) {
    return (atStart
            ? localDate.atStartOfDay(ZoneId.systemDefault())
            : localDate.plusDays(1).atStartOfDay(ZoneId.systemDefault()).minusNanos(1))
        .toInstant();
  }

  public static Instant parseInstantDate(String s, DateTimeFormatter format) {
    if (s.replace(" ", "").isEmpty()) {
      return null;
    }
    return LocalDate.parse(s, format).atStartOfDay().toInstant(ZoneOffset.UTC);
  }

  public static Instant parseInstantTime(String s, DateTimeFormatter format) {
    if (s.replace(" ", "").isEmpty()) {
      return null;
    }
    return LocalTime.parse(s, format).atDate(LocalDate.MIN).toInstant(ZoneOffset.UTC);
  }
}
