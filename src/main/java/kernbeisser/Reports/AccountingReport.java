package kernbeisser.Reports;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.swing.*;
import kernbeisser.DBConnection.DBConnection;
import kernbeisser.DBEntities.*;
import kernbeisser.Enums.VAT;
import kernbeisser.Exeptions.InvalidVATValueException;
import kernbeisser.Exeptions.NoPurchasesFoundException;
import lombok.Cleanup;
import org.jetbrains.annotations.NotNull;

public class AccountingReport extends Report {

  private final long reportNo;
  private final long startBon;
  private final long endBon;
  private final List<Purchase> purchases;
  private final boolean withNames;

  public AccountingReport(long reportNo, long startBon, long endBon, boolean withNames)
      throws NoPurchasesFoundException {
    super(
        "accountingReportFileName",
        String.format("KernbeisserBuchhaltungBonUebersicht_%d_%d", startBon, endBon));
    this.reportNo = reportNo;
    this.startBon = startBon;
    this.endBon = endBon;
    this.purchases = getPurchases();
    this.withNames = withNames;
  }

  private List<Purchase> getPurchases() {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    @Cleanup(value = "commit")
    EntityTransaction et = em.getTransaction();
    et.begin();
    List<Purchase> purchases =
        em.createQuery("select p from Purchase p where p.id between :from and :to", Purchase.class)
            .setParameter("from", startBon)
            .setParameter("to", endBon)
            .getResultList();
    if (purchases.isEmpty()) {
      throw new NoPurchasesFoundException();
    }
    return purchases;
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
    Timestamp endDate = Timestamp.from(purchases.get(purchases.size() - 1).getCreateDate());

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
    reportParams.put("end", endDate);
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

  private static Map<String, Object> getAccountingTransactionParams(List<Purchase> purchases) {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    @Cleanup(value = "commit")
    EntityTransaction et = em.getTransaction();
    et.begin();
    double transactionSaldo = 0.0;
    double transactionCreditPayIn = 0.0;
    double transactionSpecialPayments = 0.0;
    double transactionPurchases = 0.0;
    Instant startDate;
    long lastReportedBonNo = purchases.get(0).getId() - 1;
    Purchase bon = em.find(Purchase.class, lastReportedBonNo);
    if (bon == null) {
      startDate =
          em.createQuery("select min(t.date) from Transaction t", Instant.class)
              .getSingleResult()
              .minusSeconds(1);
    } else {
      startDate = bon.getSession().getTransaction().getDate();
    }
    Instant endDate = purchases.get(purchases.size() - 1).getCreateDate();
    List<Transaction> transactions =
        em.createQuery(
                "select t from Transaction t where t.date > :from and t.date <= :to",
                Transaction.class)
            .setParameter("from", startDate)
            .setParameter("to", endDate)
            .getResultList();

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
    Map<String, Object> reportParams = new HashMap<>();
    reportParams.put("start", Timestamp.from(startDate));
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
      reportParams.putAll(getAccountingTransactionParams(purchases));
      reportParams.put(
          "reportTitle", reportNo == 0 ? "Umsatzbericht" : "LD-Endabrechnung Nr. " + reportNo);
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
