package kernbeisser.Reports;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.NoResultException;
import java.time.Instant;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.swing.*;
import kernbeisser.DBConnection.DBConnection;
import kernbeisser.DBEntities.*;
import kernbeisser.Enums.VAT;
import kernbeisser.Exeptions.InvalidVATValueException;
import kernbeisser.Exeptions.NoTransactionsFoundException;
import kernbeisser.Useful.Date;
import lombok.Cleanup;
import org.jetbrains.annotations.NotNull;

public class AccountingReport extends Report {
  private final long reportNo;
  private final List<Transaction> transactions;
  private final List<Purchase> purchases;
  private final boolean withNames;

  public AccountingReport(long reportNo, List<Transaction> transactions, boolean withNames)
      throws NoTransactionsFoundException {
    super(ReportFileNames.ACCOUNTING_REPORT_FILENAME);
    this.reportNo = reportNo;
    this.transactions = transactions;
    this.purchases = getPurchases();
    this.withNames = withNames;
  }

  @Override
  String createOutFileName() {
    return String.format("KernbeisserBuchhaltungBonUebersicht_%d", reportNo);
  }

  private List<Purchase> getPurchases() throws NoResultException {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    @Cleanup(value = "commit")
    EntityTransaction et = em.getTransaction();
    et.begin();
    List<Purchase> purchases =
        em.createQuery(
                "select p from Purchase p where p.session in (select s from SaleSession s where s.transaction in (:transactions))",
                Purchase.class)
            .setParameter("transactions", transactions)
            .getResultList();
    if (purchases.isEmpty()) {
      throw new NoTransactionsFoundException();
    }
    return purchases;
  }

  static String getReportTitle(long reportNo, List<Transaction> transactions) {
    return (reportNo == 0 ? "Umsatzbericht " : "LD-Endabrechnung Nr. " + reportNo)
        + "    "
        + Date.INSTANT_DATE.format(transactions.getFirst().getDate())
        + " bis "
        + Date.INSTANT_DATE.format(transactions.getLast().getDate());
  }

  static long countVatValues(Collection<Purchase> purchases, VAT vat) {
    return purchases.stream()
        .flatMap(p -> p.getAllItems().stream())
        .filter(s -> s.getVat() == vat)
        .mapToDouble(ShoppingItem::getVatValue)
        .distinct()
        .count();
  }

  static double getVatValue(Collection<Purchase> purchases, VAT vat) {
    return purchases.stream()
        .flatMap(p -> p.getAllItems().stream())
        .filter(s -> s.getVat() == vat)
        .mapToDouble(ShoppingItem::getVatValue)
        .findFirst()
        .orElse(vat.getValue());
  }

  @NotNull
  private static Map<String, Object> getAccountingPurchaseParams(List<Purchase> purchases)
      throws InvalidVATValueException {
    double sumTotalPurchased = 0.0;
    double sumVatHiProductsPurchased = 0.0;
    double sumVatLoProductsPurchased = 0.0;
    double sumVatHiSolidarity = 0.0;
    double sumVatLoSolidarity = 0.0;
    double sumDeposit = 0.0;

    long t_high = countVatValues(purchases, VAT.HIGH);
    long t_low = countVatValues(purchases, VAT.LOW);
    if (t_high > 1 || t_low > 1) {
      String message = "";
      if (t_low > 1) {
        message += "Mehrere Werte für niedrigen MWSt.-Satz gefunden \n";
      }
      if (t_high > 1) {
        message += "Mehrere Werte für hohen MWSt.-Satz gefunden \n";
      }
      message +=
          "Gab es zwischenzeitlich einen Wechsel des Steuersatzes?\n"
              + "Der Bericht muss für einen Zeitraum mit eindeutigen Steuersätzen ausgegeben werden!";
      JOptionPane.showMessageDialog(
          JOptionPane.getRootFrame(),
          message,
          "Uneindeutige Steuersätze",
          JOptionPane.ERROR_MESSAGE);
      throw new InvalidVATValueException(1000.0);
    }
    double vatLoValue = getVatValue(purchases, VAT.LOW);
    double vatHiValue = getVatValue(purchases, VAT.HIGH);
    String solidaritySupplier = Supplier.getSolidaritySupplier().getShortName();
    String depositSupplier = Supplier.getDepositSupplier().getShortName();
    for (Purchase p : purchases) {
      sumVatHiProductsPurchased +=
          p.getFilteredSum(
              s ->
                  s.getVat() == VAT.HIGH
                      && !s.getSafeSuppliersShortName().equalsIgnoreCase(depositSupplier)
                      && !s.getSafeSuppliersShortName().equalsIgnoreCase(solidaritySupplier));
      sumVatLoProductsPurchased +=
          p.getFilteredSum(
              s ->
                  s.getVat() == VAT.LOW
                      && !s.getSafeSuppliersShortName().equalsIgnoreCase(depositSupplier)
                      && !s.getSafeSuppliersShortName().equalsIgnoreCase(solidaritySupplier));
      sumVatHiSolidarity +=
          p.getFilteredSum(
              s ->
                  s.getVat() == VAT.HIGH
                      && s.getSafeSuppliersShortName().equalsIgnoreCase(solidaritySupplier));
      sumVatLoSolidarity +=
          p.getFilteredSum(
              s ->
                  s.getVat() == VAT.LOW
                      && s.getSafeSuppliersShortName().equalsIgnoreCase(solidaritySupplier));
      sumDeposit +=
          p.getFilteredSum(
              s ->
                  s.getVat() == VAT.HIGH
                      && s.getSafeSuppliersShortName().equalsIgnoreCase(depositSupplier));
      sumTotalPurchased += p.getSum();
    }

    Map<String, Object> reportParams = new HashMap<>();
    reportParams.put("vatHiValue", vatHiValue);
    reportParams.put("vatLoValue", vatLoValue);
    reportParams.put("sumTotalPurchased", sumTotalPurchased);
    reportParams.put("sumVatHiProductsPurchased", sumVatHiProductsPurchased);
    reportParams.put("sumVatLoProductsPurchased", sumVatLoProductsPurchased);
    reportParams.put("sumDeposit", sumDeposit);
    reportParams.put("sumVatHiSolidarity", sumVatHiSolidarity);
    reportParams.put("sumVatLoSolidarity", sumVatLoSolidarity);

    return reportParams;
  }

  private Map<String, Object> getAccountingTransactionParams(List<Transaction> transactions) {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    @Cleanup(value = "commit")
    EntityTransaction et = em.getTransaction();
    et.begin();
    double transactionSaldo = 0.0;
    double transactionCreditPayIn = 0.0;
    double transactionSpecialPayments = 0.0;
    double transactionPurchases = 0.0;
    User kbUser = User.getKernbeisserUser();
    for (Transaction t : transactions) {
      double direction = t.getFromUser().equals(kbUser) ? -1.0 : 1.0;
      if (direction == -1.0 || t.getToUser().equals(kbUser)) {
        transactionSaldo -= t.getValue() * direction;
        switch (t.getTransactionType()) {
          case PURCHASE:
            transactionPurchases += t.getValue();
            break;
          case USER_GENERATED:
            transactionSpecialPayments += t.getValue() * direction;
            break;
          case PAYIN:
          case INITIALIZE:
            transactionCreditPayIn += t.getValue();
            break;
        }
      }
    }
    Map<String, Object> reportParams = UserGroup.getValueAggregatesAt(Instant.now());
    reportParams.put("transactionSaldo", transactionSaldo);
    reportParams.put("transactionCreditPayIn", transactionCreditPayIn);
    reportParams.put("transactionSpecialPayments", transactionSpecialPayments);
    reportParams.put("transactionPurchases", transactionPurchases);

    return reportParams;
  }

  @Override
  Map<String, Object> getReportParams() {
    try {
      Map<String, Object> reportParams = getAccountingPurchaseParams(purchases);
      reportParams.putAll(getAccountingTransactionParams(transactions));
      reportParams.put("reportTitle", getReportTitle(reportNo, transactions));
      return reportParams;
    } catch (InvalidVATValueException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  Collection<?> getDetailCollection() {
    return purchases.stream()
        .map(p -> p.withUserIdentification(withNames))
        .collect(Collectors.toList());
  }
}
