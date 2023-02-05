package kernbeisser.Reports;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.Instant;
import java.util.Collections;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import kernbeisser.DBConnection.DBConnection;
import kernbeisser.DBEntities.Transaction;
import kernbeisser.DBEntities.User;
import kernbeisser.Exeptions.MissingFullMemberException;
import net.sf.jasperreports.engine.JasperPrint;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

class AccountingTransactionsReportTest {

  @Test
  void createOutFileName() {
    // arrange
    AccountingTransactionsReport report =
        new AccountingTransactionsReport(1337L, null, UserNameObfuscation.NONE, true);

    // act
    String result = report.createOutFileName();

    // assert
    Assertions.assertEquals("KernbeisserBuchhaltungEinSonderzahlungen_1337", result);
  }

  @Test
  void lazyGetJspPrint() throws MissingFullMemberException {
    try (MockedStatic<DBConnection> dbConnectionMock = mockStatic(DBConnection.class)) {
      // arrange
      EntityManager entityManagerMock = EntityManagerMockHelper.mockEntityManager(dbConnectionMock);
      EntityManagerMockHelper.mockTupleQuery(entityManagerMock);
      EntityManagerMockHelper.mockSettingValueQuery(entityManagerMock);
      mockQuery(entityManagerMock);

      Transaction transaction = createTransaction();

      // act
      Report report =
          new AccountingTransactionsReport(
              1337L, Collections.singletonList(transaction), UserNameObfuscation.NONE, true);
      JasperPrint jspPrint = report.lazyGetJspPrint();

      // assert
      Assertions.assertNotNull(jspPrint);

      // FIXME
      // Assertions.assertEquals(ReportFileNames.ACCOUNTING_TRANSACTION_REPORT_FILENAME,
      // jspPrint.getName() + ".jrxml");
    }
  }

  @NotNull
  private static Transaction createTransaction() {
    Transaction transaction = mock(Transaction.class);
    when(transaction.getDate()).thenReturn(Instant.now());
    when(transaction.withUserIdentifications(any())).thenReturn(mock(Transaction.class));
    return transaction;
  }

  private static void mockQuery(EntityManager entityManagerMock) {
    TypedQuery typedQueryUser = mock(TypedQuery.class);
    when(entityManagerMock.createQuery(anyString(), eq(User.class))).thenReturn(typedQueryUser);
    when(typedQueryUser.setMaxResults(anyInt())).thenReturn(typedQueryUser);
    when(typedQueryUser.getSingleResult()).thenReturn(new User());
  }
}
