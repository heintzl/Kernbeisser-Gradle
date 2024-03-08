package kernbeisser.Useful;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class KornKraft {
  public static int MAX_LOOK_UP_COUNT = 100;

  public static Optional<String> findLastValidURL() {
    for (int i = 0; i < MAX_LOOK_UP_COUNT; i++) {
      String url = generateURL(LocalDate.now().minusDays(i));
      try {
        if (getStatusCode(url) != 404) {
          log.info("took look back of " + i + " days to find newest kk bnn file url");
          return Optional.of(url);
        }
      } catch (IOException ignored) {
      }
    }
    return Optional.empty();
  }

  public static int getStatusCode(String urlString) throws IOException {
    URL url = new URL(urlString);
    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    connection.setRequestMethod("GET");
    connection.connect();

    return connection.getResponseCode();
  }

  public static String generateURL(LocalDate date) {
    String dateString = date.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
    String monthString =
        (date.getDayOfMonth() > 16 ? date.plusMonths(1) : date)
            .format(DateTimeFormatter.ofPattern("yyMM"));
    return String.format(
        "https://shop.kornkraft.com/files/cms_download/PL_%s_%s.BNN", monthString, dateString);
  }
}
