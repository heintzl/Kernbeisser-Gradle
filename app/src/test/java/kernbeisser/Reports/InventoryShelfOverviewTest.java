package kernbeisser.Reports;

import java.time.LocalDate;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class InventoryShelfOverviewTest {

  @Test
  void createOutFileName() {
    // arrange
    LocalDate now = LocalDate.now();
    Report report = new InventoryShelfOverview(null, now);

    // act
    String result = report.createOutFileName();

    // assert
    Assertions.assertEquals("Regalübersicht_" + now, result);
  }
}
