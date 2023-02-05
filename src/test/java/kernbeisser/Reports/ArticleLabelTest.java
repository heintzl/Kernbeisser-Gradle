package kernbeisser.Reports;

import kernbeisser.Useful.Date;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Instant;

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