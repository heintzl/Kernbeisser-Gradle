package kernbeisser.Reports;

import java.util.Collections;
import kernbeisser.DBEntities.PriceList;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PriceListReportTest {

  @Test
  void createOutFileName() {
    // arrange
    Report report = new PriceListReport(Collections.emptyList(), "testName");

    // act
    String result = report.createOutFileName();

    // assert
    Assertions.assertEquals("Preisliste testName", result);
  }

  @Test
  void createOutFileNamePriceList() {
    // arrange
    PriceList priceList = Mockito.mock(PriceList.class);
    Mockito.when(priceList.getAllArticles()).thenReturn(Collections.emptyList());
    Mockito.when(priceList.getName()).thenReturn("testName");

    Report report = new PriceListReport(priceList);

    // act
    String result = report.createOutFileName();

    // assert
    Assertions.assertEquals("Preisliste testName", result);
  }
}
