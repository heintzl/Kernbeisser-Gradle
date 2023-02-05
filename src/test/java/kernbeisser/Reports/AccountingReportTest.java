package kernbeisser.Reports;

import kernbeisser.DBConnection.DBConnection;
import kernbeisser.DBEntities.*;
import kernbeisser.Enums.TransactionType;
import net.sf.jasperreports.engine.JasperPrint;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.persistence.EntityManager;
import javax.persistence.Tuple;
import javax.persistence.TypedQuery;
import java.time.Instant;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountingReportTest {
    @Test
    void lazyGetJspPrint() {

        try (MockedStatic<DBConnection> dbConnectionMock = mockStatic(DBConnection.class)) {
            // arrange
            EntityManager entityManagerMock = EntityManagerMockHelper.mockEntityManager(dbConnectionMock);

            Purchase purchase = createPurchase();
            mockQuerys(entityManagerMock, purchase);

            List<Transaction> transactions = createTransactions();
            AccountingReport accountingReport = new AccountingReport(1L, transactions, false);

            // act
            JasperPrint jspPrint = accountingReport.lazyGetJspPrint();

            // assert
            Assertions.assertNotNull(jspPrint);
            Assertions.assertEquals(ReportFileNames.ACCOUNTING_REPORT_FILENAME, jspPrint.getName() + ".jrxml");
        }
    }

    @Test
    void createOutFileName() {
        try (MockedStatic<DBConnection> dbConnectionMock = mockStatic(DBConnection.class)) {
            // arrange
            EntityManager entityManagerMock = EntityManagerMockHelper.mockEntityManager(dbConnectionMock);
            mockPurchaseQuery(entityManagerMock, new Purchase());

            AccountingReport accountingReport = new AccountingReport(1337L, null, false);

            // act
            String result = accountingReport.createOutFileName();

            // assert
            Assertions.assertEquals("KernbeisserBuchhaltungBonUebersicht_1337", result);
        }
    }

    private static void mockQuerys(EntityManager entityManagerMock, @NotNull Purchase purchase) {
        TypedQuery typedQueryShoppingItem = mock(TypedQuery.class);
        when(entityManagerMock.createQuery(anyString(), eq(ShoppingItem.class))).thenReturn(typedQueryShoppingItem);

        mockPurchaseQuery(entityManagerMock, purchase);
        mockSupplierQuery(entityManagerMock);
        mockUserQuery(entityManagerMock);
        mockTupleQuery(entityManagerMock);
    }

    private static void mockTupleQuery(EntityManager entityManagerMock) {
        TypedQuery typedQueryTuple = mock(TypedQuery.class);
        when(entityManagerMock.createQuery(anyString(), eq(Tuple.class))).thenReturn(typedQueryTuple);
        when(typedQueryTuple.setParameter(anyString(), any())).thenReturn(typedQueryTuple);
        when(typedQueryTuple.getResultStream()).thenReturn(EntityManagerMockHelper.getStream(new UserGroup()));
    }

    private static void mockPurchaseQuery(EntityManager entityManagerMock, @NotNull Purchase purchase) {
        TypedQuery<Purchase> typedQueryPurchase = Mockito.mock(TypedQuery.class);
        when(entityManagerMock.createQuery(anyString(), eq(Purchase.class))).thenReturn(typedQueryPurchase);
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
        when(entityManagerMock.createQuery(anyString(), eq(Supplier.class))).thenReturn(typedQuerySupplier);
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