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
import kernbeisser.DBEntities.UserGroup;
import kernbeisser.Enums.StatementType;
import net.sf.jasperreports.engine.JasperPrint;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

class TransactionStatementTest {

  @Test
  void createOutFileName() {
    // arrange
    User user = Mockito.mock(User.class);
    UserGroup userGroup = Mockito.mock(UserGroup.class);
    Mockito.when(user.getUserGroup()).thenReturn(userGroup);
    Mockito.when(userGroup.getId()).thenReturn(42);
    Mockito.when(user.toString()).thenReturn("testuser");

    try (MockedStatic<DBConnection> dbConnectionMock = mockStatic(DBConnection.class)) {
      // arrange
      EntityManager entityManagerMock = EntityManagerMockHelper.mockEntityManager(dbConnectionMock);
      mockQuery(entityManagerMock, new Transaction());

      Report report = new TransactionStatement(user, StatementType.FULL, true);

      // act
      String result = report.createOutFileName();

      // assert
      String expected = "Kontoauszug_" + "testuser";
      Assertions.assertEquals(expected, result);
    }
  }

  @Test
  void lazyGetJspPrint() {
    User user = Mockito.mock(User.class);
    UserGroup userGroup = Mockito.mock(UserGroup.class);
    Mockito.when(user.getUserGroup()).thenReturn(userGroup);
    Mockito.when(userGroup.getId()).thenReturn(42);
    Mockito.when(user.toString()).thenReturn("testuser");

    Transaction transaction = mock(Transaction.class);
    when(transaction.getDate()).thenReturn(Instant.now());
    when(transaction.getFromUserGroup()).thenReturn(userGroup);

    when(userGroup.getMembers()).thenReturn(Collections.emptyList());
    try (MockedStatic<DBConnection> dbConnectionMock = mockStatic(DBConnection.class)) {
      // arrange
      EntityManager entityManagerMock = EntityManagerMockHelper.mockEntityManager(dbConnectionMock);
      EntityManagerMockHelper.mockTupleQuery(entityManagerMock);
      EntityManagerMockHelper.mockSettingValueQuery(entityManagerMock);
      mockQuery(entityManagerMock, transaction);

      Report report = new TransactionStatement(user, StatementType.FULL, true);

      // act
      JasperPrint jspPrint = report.lazyGetJspPrint();

      // assert
      Assertions.assertNotNull(jspPrint);
      Assertions.assertEquals(
          ReportFileNames.TRANSACTION_STATEMENT_REPORT_FILENAME, jspPrint.getName() + ".jrxml");
    }
  }

  private static void mockQuery(EntityManager entityManagerMock, @NotNull Transaction transaction) {
    TypedQuery<Transaction> typedQueryPurchase = Mockito.mock(TypedQuery.class);
    when(entityManagerMock.createQuery(anyString(), eq(Transaction.class)))
        .thenReturn(typedQueryPurchase);
    when(typedQueryPurchase.setParameter(anyString(), any())).thenReturn(typedQueryPurchase);
    when(typedQueryPurchase.getResultList()).thenReturn(Collections.singletonList(transaction));
  }
}
