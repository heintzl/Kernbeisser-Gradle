package kernbeisser.Reports;

import static org.mockito.Mockito.*;

import java.time.Instant;
import java.util.Collections;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import kernbeisser.DBConnection.DBConnection;
import kernbeisser.DBEntities.ShoppingItem;
import net.sf.jasperreports.engine.JasperPrint;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

class TillrollReportTest {

  @Test
  void createOutFileName() {
    // arrange
    Instant start = Instant.parse("2001-01-01T10:15:30.00Z");
    Instant end = Instant.parse("2525-12-31T10:15:30.00Z");
    Report report = new TillrollReport(start, end);

    // act
    String result = report.createOutFileName();

    // assert
    String expected =
        "KernbeisserBonrolle_" + "2001-01-01 11:15:30.0" + "_" + "2525-12-31 11:15:30.0";
    Assertions.assertEquals(expected, result);
  }

  @Test
  void lazyGetJspPrint() {

    try (MockedStatic<DBConnection> dbConnectionMock = mockStatic(DBConnection.class)) {
      // arrange
      EntityManager entityManagerMock = EntityManagerMockHelper.mockEntityManager(dbConnectionMock);
      EntityManagerMockHelper.mockTupleQuery(entityManagerMock);
      EntityManagerMockHelper.mockSettingValueQuery(entityManagerMock);
      mockQuery(entityManagerMock);

      Instant end = Instant.parse("2525-12-31T10:15:30.00Z");
      Instant start = Instant.parse("2001-01-01T10:15:30.00Z");

      // act
      Report report = new TillrollReport(start, end);
      JasperPrint jspPrint = report.lazyGetJspPrint();

      // assert
      Assertions.assertNotNull(jspPrint);
      Assertions.assertEquals(
          ReportFileNames.TILLROLL_REPORT_FILENAME, jspPrint.getName() + ".jrxml");
    }
  }

  static void mockQuery(EntityManager entityManagerMock) {
    TypedQuery typedQueryTuple = mock(TypedQuery.class);
    when(entityManagerMock.createQuery(anyString(), eq(ShoppingItem.class)))
        .thenReturn(typedQueryTuple);
    when(typedQueryTuple.setParameter(anyString(), any())).thenReturn(typedQueryTuple);
    when(typedQueryTuple.getResultList()).thenReturn(Collections.emptyList());
  }
}
