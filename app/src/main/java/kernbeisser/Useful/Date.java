package kernbeisser.Useful;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Locale;

public class Date {

  public static final ZoneId CURRENT_ZONE = ZoneId.systemDefault();
  public static final DateTimeFormatter INSTANT_DATE_TIME =
      DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT)
          .withLocale(Locale.getDefault())
          .withZone(CURRENT_ZONE);
  public static final DateTimeFormatter INSTANT_DATE_TIME_SEC =
      DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT, FormatStyle.MEDIUM)
          .withLocale(Locale.getDefault())
          .withZone(CURRENT_ZONE);
  public static final DateTimeFormatter INSTANT_TIME =
      DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT)
          .withLocale(Locale.getDefault())
          .withZone(CURRENT_ZONE);
  public static final DateTimeFormatter INSTANT_DATE =
      DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)
          .withLocale(Locale.getDefault())
          .withZone(CURRENT_ZONE);
  public static final DateTimeFormatter INSTANT_MONTH_YEAR =
      DateTimeFormatter.ofPattern("MMMuu").withLocale(Locale.getDefault()).withZone(CURRENT_ZONE);
  public static final DateTimeFormatter INSTANT_CATALOG_DATE =
      DateTimeFormatter.ofPattern("uuuuMMdd")
          .withLocale(Locale.getDefault())
          .withZone(CURRENT_ZONE);
  public static final DateTimeFormatter INSTANT_CATALOG_TIME =
      DateTimeFormatter.ofPattern("HHmm").withLocale(Locale.getDefault()).withZone(CURRENT_ZONE);

  public static String safeDateFormat(Instant instant, DateTimeFormatter formatter) {
    if (instant == null) {
      return "";
    }
    return formatter.format(instant);
  }

  public static String zonedDateFormat(Instant instant, DateTimeFormatter formatter) {
    if (instant == null) {
      return "";
    }
    return formatter.format(instant.atZone(CURRENT_ZONE));
  }

  public static Instant atStartOrEndOfDay(LocalDate localDate, boolean atStart) {
    return (atStart
            ? localDate.atStartOfDay(CURRENT_ZONE)
            : localDate.plusDays(1).atStartOfDay(CURRENT_ZONE).minusNanos(1))
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
        .atDate(date.atZone(CURRENT_ZONE).toLocalDate())
        .atZone(CURRENT_ZONE)
        .toInstant();
  }

  public static Instant shiftInstantToUTC(Instant instant) {
    // Criteria Query of getCustomerValueMapAt applies time zone conversion to Instant, which is
    // not safely applied to Transaction.getDate, so this conversion gets applied explicitly here
    return instant.minusSeconds(Date.CURRENT_ZONE.getRules().getOffset(instant).getTotalSeconds());
  }
}
