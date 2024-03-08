package kernbeisser.Reports;

import java.time.LocalDate;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class InventoryShelfDetailsTest {

  @Test
  void createOutFileName() {
    // arrange
    LocalDate now = LocalDate.now();
    Report report = new InventoryShelfDetails(null, now);

    // act
    String result = report.createOutFileName();

    // assert
    Assertions.assertEquals("Regaldetails_" + now, result);
  }
}
