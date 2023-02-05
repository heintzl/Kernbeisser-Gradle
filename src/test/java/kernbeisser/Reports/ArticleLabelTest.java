package kernbeisser.Reports;

import java.time.Instant;
import kernbeisser.Useful.Date;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ArticleLabelTest {

  @Test
  void createOutFileName() {
    // arrange
    Report report = new ArticleLabel(null);
    String now = Date.INSTANT_DATE_TIME.format(Instant.now());

    // act
    String result = report.createOutFileName();

    // assert
    Assertions.assertEquals("Etiketten_" + now, result);
  }
}
