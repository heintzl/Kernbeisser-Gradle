package kernbeisser.Reports;

import static org.mockito.Mockito.*;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Tuple;
import jakarta.persistence.TypedQuery;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import kernbeisser.DBConnection.DBConnection;
import kernbeisser.DBEntities.*;
import kernbeisser.Enums.TransactionType;
import kernbeisser.Exeptions.InvalidReportNoException;
import kernbeisser.Exeptions.NoTransactionsFoundException;
import net.sf.jasperreports.engine.JasperPrint;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AccountingReportTest {
  @Test
  void lazyGetJspPrint() {

    try (MockedStatic<DBConnection> dbConnectionMock = mockStatic(DBConnection.class)) {
      // arrange
      EntityManager entityManagerMock = EntityManagerMockHelper.mockEntityManager(dbConnectionMock);

      Purchase purchase = createPurchase();
      mockQuerys(entityManagerMock, purchase);
      AccountingReport accountingReport = new AccountingReport(1L, false);

      // act
      JasperPrint jspPrint = accountingReport.lazyGetJspPrint();

      // assert
      Assertions.assertNotNull(jspPrint);
      Assertions.assertEquals(
          ReportFileNames.ACCOUNTING_REPORT_FILENAME, jspPrint.getName() + ".jrxml");
    } catch (NoTransactionsFoundException | InvalidReportNoException e) {
      Assertions.fail("unsuitable reportNo");
    }
  }

  @Test
  void createOutFileName() {
    try (MockedStatic<DBConnection> dbConnectionMock = mockStatic(DBConnection.class)) {
      // arrange
      EntityManager entityManagerMock = EntityManagerMockHelper.mockEntityManager(dbConnectionMock);
      mockPurchaseQuery(entityManagerMock, new Purchase());

      AccountingReport accountingReport = new AccountingReport(1337L, false);

      // act
      String result = accountingReport.createOutFileName();

      // assert
      Assertions.assertEquals("KernbeisserBuchhaltungBonUebersicht_1337", result);
    } catch (NoTransactionsFoundException | InvalidReportNoException e) {
      Assertions.fail("unsuitable reportNo");
    }
  }

  private static void mockQuerys(EntityManager entityManagerMock, @NotNull Purchase purchase) {
    TypedQuery typedQueryShoppingItem = mock(TypedQuery.class);
    when(entityManagerMock.createQuery(anyString(), eq(ShoppingItem.class)))
        .thenReturn(typedQueryShoppingItem);

    mockPurchaseQuery(entityManagerMock, purchase);
    mockSupplierQuery(entityManagerMock);
    mockUserQuery(entityManagerMock);
    mockTupleQuery(entityManagerMock);
  }

  private static void mockTupleQuery(EntityManager entityManagerMock) {
    TypedQuery typedQueryTuple = mock(TypedQuery.class);
    when(entityManagerMock.createQuery(anyString(), eq(Tuple.class))).thenReturn(typedQueryTuple);
    when(typedQueryTuple.setParameter(anyString(), any())).thenReturn(typedQueryTuple);
    when(typedQueryTuple.getResultStream())
        .thenReturn(EntityManagerMockHelper.getStream(new UserGroup()));
  }

  private static void mockPurchaseQuery(
      EntityManager entityManagerMock, @NotNull Purchase purchase) {
    TypedQuery<Purchase> typedQueryPurchase = Mockito.mock(TypedQuery.class);
    when(entityManagerMock.createQuery(anyString(), eq(Purchase.class)))
        .thenReturn(typedQueryPurchase);
    when(typedQueryPurchase.setParameter(anyString(), any())).thenReturn(typedQueryPurchase);
    when(typedQueryPurchase.getResultList()).thenReturn(Collections.singletonList(purchase));
  }

  @NotNull
  private static Purchase createPurchase() {
    Purchase purchase = new Purchase();
    SaleSession saleSession = mock(SaleSession.class);
    when(saleSession.getSeller()).thenReturn(new User());
    when(saleSession.getCustomer()).thenReturn(new User());
    purchase.setSession(saleSession);
    return purchase;
  }

  private static void mockSupplierQuery(EntityManager entityManagerMock) {
    TypedQuery typedQuerySupplier = mock(TypedQuery.class);
    when(entityManagerMock.createQuery(anyString(), eq(Supplier.class)))
        .thenReturn(typedQuerySupplier);
    when(typedQuerySupplier.setParameter(anyString(), any())).thenReturn(typedQuerySupplier);
    when(typedQuerySupplier.getSingleResult()).thenReturn(new Supplier());
  }

  private static void mockUserQuery(EntityManager entityManagerMock) {
    TypedQuery typedQueryUser = mock(TypedQuery.class);
    when(entityManagerMock.createQuery(anyString(), eq(User.class))).thenReturn(typedQueryUser);
    when(typedQueryUser.setMaxResults(anyInt())).thenReturn(typedQueryUser);
    when(typedQueryUser.getSingleResult()).thenReturn(new User());
  }

  @NotNull
  private static List<Transaction> createTransactions() {
    Transaction transaction = new Transaction();
    transaction.setFromUser(new User());
    transaction.setToUser(new User());
    transaction.setTransactionType(TransactionType.PURCHASE);
    transaction.setDate(Instant.now());
    return Collections.singletonList(transaction);
  }
}
