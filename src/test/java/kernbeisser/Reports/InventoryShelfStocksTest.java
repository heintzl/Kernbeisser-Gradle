package kernbeisser.Reports;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

class InventoryShelfStocksTest {

    @Test
    void createOutFileName() {
        // arrange
        LocalDate now = LocalDate.now();
        Report report = new InventoryShelfStocks(null, now);

        // act
        String result = report.createOutFileName();

        // assert
        Assertions.assertEquals("InventurRegalBestände_" + now, result);

    }
}