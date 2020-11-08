package kernbeisser.Reports;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.swing.*;
import kernbeisser.DBConnection.DBConnection;
import kernbeisser.DBEntities.Purchase;
import kernbeisser.DBEntities.ShoppingItem;
import kernbeisser.DBEntities.Transaction;
import kernbeisser.DBEntities.User;
import kernbeisser.Enums.VAT;
import kernbeisser.Exeptions.InvalidVATValueException;
import lombok.Cleanup;
import org.jetbrains.annotations.NotNull;

public class AccountingReport extends Report {

  private final long startBon;
  private final long endBon;

  public AccountingReport(long startBon, long endBon) {
    super(
        "accountingReportFileName",
        String.format("KernbeisserBuchhaltungBonUebersicht_%d_%d.pdf", startBon, endBon));
    this.startBon = startBon;
    this.endBon = endBon;
  }

  private static List<Purchase> getPurchases(long startBon, long endBon) {
    @Cleanup EntityManager em = DBConnection.getEntityManager();

    List<Purchase> purchases =
        em.createQuery("select p from Purchase p where p.id between :from and :to", Purchase.class)
            .setParameter("from", startBon)
            .setParameter("to", endBon)
            .getResultList();
    if (purchases.isEmpty()) {
      throw new NoResultException();
    }
    return purchases;
  }

  private static Map<String, Object> reportParams(long startBon, long endBon)
      throws InvalidVATValueException {
    List<Purchase> purchases = getPurchases(startBon, endBon);
    Map<String, Object> reportParams = getAccountingPurchaseParams(purchases);
    reportParams.putAll(getAccountingTransactionParams(purchases));
    return reportParams;
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
    double sumVatHiPurchased = 0.0;
    double sumVatLoPurchased = 0.0;
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
    for (Purchase p : purchases) {
      sumVatHiPurchased += p.getVatSum(VAT.HIGH);
      sumVatLoPurchased += p.getVatSum(VAT.LOW);
      sumTotalPurchased += p.getSum();
    }

    Map<String, Object> reportParams = new HashMap<>();
    reportParams.put("end", endDate);
    reportParams.put("vatHiValue", vatHiValue);
    reportParams.put("vatLoValue", vatLoValue);
    reportParams.put("sumTotalPurchased", sumTotalPurchased);
    reportParams.put("sumVatHiPurchased", sumVatHiPurchased);
    reportParams.put("sumVatLoPurchased", sumVatLoPurchased);

    return reportParams;
  }

  private static Map<String, Object> getAccountingTransactionParams(List<Purchase> purchases) {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    double transactionSaldo = 0.0;
    double transactionCreditPayIn = 0.0;
    double transactionSpecialPayments = 0.0;
    double transactionPurchases = 0.0;
    Instant startDate;
    long lastReportedBonNo = purchases.get(0).getId() - 1;
    Purchase bon = em.find(Purchase.class, lastReportedBonNo);
    if (bon == null) {
      startDate =
          em.createQuery("select t from Transaction t order by t.date asc", Transaction.class)
              .setFirstResult(0)
              .setMaxResults(1)
              .getSingleResult()
              .getDate()
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

    for (Transaction t : transactions) {
      User kbUser = User.getKernbeisserUser();
      if (t.getFrom().equals(kbUser) || t.getTo().equals(kbUser)) {
        transactionSaldo += t.getValue();
        switch (t.getTransactionType()) {
          case PURCHASE:
            transactionPurchases += t.getValue();
            break;
          case USER_GENERATED:
            transactionSpecialPayments += t.getValue() * (t.getFrom().equals(kbUser) ? -1.0 : 1.0);
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
    List<Purchase> purchases = getPurchases(startBon, endBon);
    try {
      Map<String, Object> reportParams = getAccountingPurchaseParams(purchases);
      reportParams.putAll(getAccountingTransactionParams(purchases));
      return reportParams;
    } catch (InvalidVATValueException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  Collection<?> getDetailCollection() {
    return getPurchases(startBon, endBon);
  }
}
