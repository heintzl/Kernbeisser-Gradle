package kernbeisser.Useful;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Locale;

public class Date {
    public static DateTimeFormatter INSTANT_FORMAT =
            DateTimeFormatter.ofLocalizedDateTime( FormatStyle.SHORT )
            .withLocale( Locale.GERMANY ).withZone(ZoneId.systemDefault());
}
