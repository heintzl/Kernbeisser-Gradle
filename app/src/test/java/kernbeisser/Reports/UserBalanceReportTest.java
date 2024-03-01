package kernbeisser.Reports;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import kernbeisser.DBConnection.DBConnection;
import kernbeisser.DBEntities.UserGroup;
import net.sf.jasperreports.engine.JasperPrint;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

class UserBalanceReportTest {

  @Test
  void createOutFileName_default() {
    UserGroup userGroup = mock(UserGroup.class);
    when(userGroup.getMembers()).thenReturn(Collections.emptyList());
    try (MockedStatic<DBConnection> dbConnectionMock = mockStatic(DBConnection.class)) {
      // arrange
      EntityManager entityManagerMock = EntityManagerMockHelper.mockEntityManager(dbConnectionMock);
      mockUserGroupQuery(entityManagerMock, userGroup);

      Report report = new UserBalanceReport(-1L, true);

      // act
      String result = report.createOutFileName();

      // assert
      String expected =
          "KernbeisserGuthabenstände_"
              + Timestamp.from(Instant.now().truncatedTo(ChronoUnit.MINUTES));
      Assertions.assertEquals(expected, result);
    }
  }

  @Test
  void createOutFileName_reportNo() {
    UserGroup userGroup = mock(UserGroup.class);
    when(userGroup.getMembers()).thenReturn(Collections.emptyList());

    try (MockedStatic<DBConnection> dbConnectionMock = mockStatic(DBConnection.class)) {
      // arrange
      EntityManager entityManagerMock = EntityManagerMockHelper.mockEntityManager(dbConnectionMock);
      mockUserGroupQuery(entityManagerMock, userGroup);
      mockLongQuery(entityManagerMock);
      EntityManagerMockHelper.mockTupleQuery(entityManagerMock);

      Report report = new UserBalanceReport(42L, true);

      // act
      String result = report.createOutFileName();

      // assert
      String expected = "KernbeisserGuthabenstände_42";
      Assertions.assertEquals(expected, result);
    }
  }

  @Test
  void lazyGetJspPrint() {

    UserGroup userGroup = mock(UserGroup.class);
    when(userGroup.getMembers()).thenReturn(Collections.emptyList());
    try (MockedStatic<DBConnection> dbConnectionMock = mockStatic(DBConnection.class)) {
      // arrange
      EntityManager entityManagerMock = EntityManagerMockHelper.mockEntityManager(dbConnectionMock);
      mockUserGroupQuery(entityManagerMock, userGroup);
      mockLongQuery(entityManagerMock);
      EntityManagerMockHelper.mockTupleQuery(entityManagerMock);
      EntityManagerMockHelper.mockSettingValueQuery(entityManagerMock);

      Report report = new UserBalanceReport(42L, true);

      // act
      JasperPrint jspPrint = report.lazyGetJspPrint();

      // assert
      Assertions.assertNotNull(jspPrint);
      // FIXME
      //  Assertions.assertEquals(ReportFileNames.USER_BALANCE_REPORT_FILENAME, jspPrint.getName() +
      // ".jrxml");
    }
  }

  private static void mockUserGroupQuery(
      EntityManager entityManagerMock, @NotNull UserGroup userGroup) {
    TypedQuery<UserGroup> typedQueryUserGroup = Mockito.mock(TypedQuery.class);
    when(entityManagerMock.createQuery(anyString(), eq(UserGroup.class)))
        .thenReturn(typedQueryUserGroup);
    when(typedQueryUserGroup.getResultList()).thenReturn(Collections.singletonList(userGroup));
  }

  private static void mockLongQuery(EntityManager entityManagerMock) {
    TypedQuery typedQuery = mock(TypedQuery.class);
    when(entityManagerMock.createQuery(anyString(), eq(Long.class))).thenReturn(typedQuery);
    when(typedQuery.setParameter(anyString(), any())).thenReturn(typedQuery);
    when(typedQuery.getSingleResult()).thenReturn(42L);
  }
}
