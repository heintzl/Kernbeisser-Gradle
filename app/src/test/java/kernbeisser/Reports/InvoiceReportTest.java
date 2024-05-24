package kernbeisser.Reports;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import kernbeisser.DBConnection.DBConnection;
import kernbeisser.DBEntities.*;
import kernbeisser.Exeptions.MissingFullMemberException;
import net.sf.jasperreports.engine.JasperPrint;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

class InvoiceReportTest {

  @Test
  void createOutFileName() throws MissingFullMemberException {
    // arrange
    Instant now = Instant.now().truncatedTo(ChronoUnit.MINUTES);
    Purchase purchase = createPurchase(now);
    Report report = new InvoiceReport(purchase);

    // act
    String result = report.createOutFileName();

    // assert
    Assertions.assertEquals("42_testVorname_testNachname_" + now, result);
  }

  @NotNull
  private static Purchase createPurchase(Instant now) {
    Purchase purchase = mock(Purchase.class);
    SaleSession session = mock(SaleSession.class);
    User user = mock(User.class);
    when(user.getFirstName()).thenReturn("testVorname");
    when(user.getSurname()).thenReturn("testNachname");
    when(user.getUserGroup()).thenReturn(mock(UserGroup.class));
    when(user.isFullMember()).thenReturn(true);
    when(session.getCustomer()).thenReturn(user);
    when(session.getSeller()).thenReturn(user);
    when(purchase.getSession()).thenReturn(session);
    when(purchase.getCreateDate()).thenReturn(now);
    when(purchase.getBonNo()).thenReturn(42L);
    when(purchase.getId()).thenReturn(42L);
    return purchase;
  }

  @Test
  void lazyGetJspPrint() throws MissingFullMemberException {
    try (MockedStatic<DBConnection> dbConnectionMock = mockStatic(DBConnection.class)) {
      // arrange
      EntityManager entityManagerMock = EntityManagerMockHelper.mockEntityManager(dbConnectionMock);
      EntityManagerMockHelper.mockTupleQuery(entityManagerMock);
      EntityManagerMockHelper.mockSettingValueQuery(entityManagerMock);
      mockQuery(entityManagerMock);

      Instant now = Instant.now().truncatedTo(ChronoUnit.MINUTES);
      Purchase purchase = createPurchase(now);

      // act
      Report report = new InvoiceReport(purchase);
      JasperPrint jspPrint = report.lazyGetJspPrint();

      // assert
      Assertions.assertNotNull(jspPrint);
      Assertions.assertEquals(
          ReportFileNames.INVOICE_REPORT_FILENAME, jspPrint.getName() + ".jrxml");
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
