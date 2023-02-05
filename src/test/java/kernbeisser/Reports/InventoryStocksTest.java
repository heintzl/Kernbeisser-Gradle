package kernbeisser.Reports;

import java.time.LocalDate;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class InventoryStocksTest {

  @Test
  void createOutFileName() {
    // arrange
    LocalDate now = LocalDate.now();
    Report report = new InventoryStocks(now);

    // act
    String result = report.createOutFileName();

    // assert
    Assertions.assertEquals("InventurBestände_" + now, result);
  }
}
