package kernbeisser.Reports;

import java.time.LocalDate;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class InventoryCountingListsTest {

  @Test
  void createOutFileName() {
    // arrange
    LocalDate now = LocalDate.now();
    Report report = new InventoryCountingLists(null, now);

    // act
    String result = report.createOutFileName();

    // assert
    Assertions.assertEquals("Zähllisten_" + now, result);
  }
}
