package kernbeisser.Reports;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.stream.Stream;
import kernbeisser.DBConnection.DBConnection;
import kernbeisser.DBEntities.User;
import net.sf.jasperreports.engine.JasperPrint;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

class KeyUserListTest {

  @Test
  void createOutFileName() {
    // arrange
    Instant now = Instant.now().truncatedTo(ChronoUnit.MINUTES);
    Report report = new KeyUserList(null);

    // act
    String result = report.createOutFileName();

    // assert
    Assertions.assertEquals("Ladenbenutzerschlüssel_" + Timestamp.from(now), result);
  }

  @Test
  void lazyGetJspPrint() {
    try (MockedStatic<DBConnection> dbConnectionMock = mockStatic(DBConnection.class)) {
      // arrange
      EntityManager entityManagerMock = EntityManagerMockHelper.mockEntityManager(dbConnectionMock);
      EntityManagerMockHelper.mockTupleQuery(entityManagerMock);
      EntityManagerMockHelper.mockSettingValueQuery(entityManagerMock);
      mockQuery(entityManagerMock);

      // act
      Report report = new KeyUserList(null);
      JasperPrint jspPrint = report.lazyGetJspPrint();

      // assert
      Assertions.assertNotNull(jspPrint);
      // FIXME
      // Assertions.assertEquals(ReportFileNames.KEY_USER_LIST_REPORT_FILENAME, jspPrint.getName() +
      // ".jrxml");
    }
  }

  private void mockQuery(EntityManager entityManagerMock) {
    TypedQuery typedQueryTuple = mock(TypedQuery.class);
    when(entityManagerMock.createQuery(anyString(), eq(User.class))).thenReturn(typedQueryTuple);
    when(typedQueryTuple.setParameter(anyString(), any())).thenReturn(typedQueryTuple);
    when(typedQueryTuple.getResultStream()).thenReturn(Stream.empty());
  }
}
