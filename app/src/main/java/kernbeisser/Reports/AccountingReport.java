package kernbeisser.Reports;

import java.awt.*;
import java.time.Instant;
import java.util.*;
import java.util.List;
import javax.swing.*;
import kernbeisser.DBConnection.QueryBuilder;
import kernbeisser.DBEntities.*;
import kernbeisser.DBEntities.Purchase_;
import kernbeisser.DBEntities.Repositories.TransactionRepository;
import kernbeisser.DBEntities.SaleSession_;
import kernbeisser.Enums.VAT;
import kernbeisser.Exeptions.InvalidVATValueException;
import kernbeisser.Exeptions.NoTransactionsFoundException;
import kernbeisser.Reports.ReportDTO.AccountingReportItem;
import kernbeisser.Useful.Constants;
import kernbeisser.Useful.Date;
import org.jetbrains.annotations.NotNull;

public class AccountingReport extends Report {
  private final long reportNo;
  private final List<AccountingReportItem> reportItems = new ArrayList<>();
  private final boolean withNames;
  private final Map<String, Object> reportParams;

  private AccountingReport(long reportNo, boolean withNames, List<Transaction> transactions)
      throws NoTransactionsFoundException, InvalidVATValueException {
    super(ReportFileNames.ACCOUNTING_REPORT_FILENAME);
    this.reportNo = reportNo;
    this.withNames = withNames;
    List<Purchase> purchases =
        QueryBuilder.selectAll(Purchase.class)
            .where(Purchase_.session.child(SaleSession_.transaction).in(transactions))
            .getResultList();
    this.addAccountingReportItems(transactions, purchases);
    this.reportParams =
        UserGroup.getValueAggregatesAt(Date.shiftInstantToUTC(getLastReportedInstant()));
    this.addPurchaseAndTransactionParams(purchases);
  }

  public static AccountingReport old(long reportNo, boolean withNames)
      throws InvalidVATValueException {
    return new AccountingReport(
        reportNo, withNames, TransactionRepository.getTransactionsByReportNo(reportNo));
  }

  public static AccountingReport latest(
      long reportNo, List<Transaction> unreportedTransactions, boolean withNames)
      throws InvalidVATValueException {
    return new AccountingReport(reportNo, withNames, unreportedTransactions);
  }

  @Override
  String createOutFileName() {
    return String.format("KernbeisserUmsaetze_%d", reportNo);
  }

  private @NotNull void addAccountingReportItems(
      List<Transaction> transactions, List<Purchase> purchases) {
    this.reportItems.addAll(
        purchases.stream().map(p -> new AccountingReportItem(p, withNames)).toList());
    this.reportItems.addAll(
        transactions.stream()
            .filter(TransactionRepository::isAccountingReportTransaction)
            .map(t -> new AccountingReportItem(t, withNames))
            .sorted(Comparator.comparingInt(reportItem -> reportItem.getReportGroup().ordinal()))
            .toList());
  }

  private Instant getLastReportedInstant() {
    return reportItems.stream()
        .map(AccountingReportItem::getDate)
        .max(Instant::compareTo)
        .orElse(Instant.MIN);
  }

  private String getReportTitle(long reportNo) {
    return (reportNo == 0 ? "Umsatzbericht " : "LD-Endabrechnung Nr. " + reportNo)
        + "    "
        + Date.INSTANT_DATE.format(reportItems.getFirst().getDate())
        + " bis "
        + Date.INSTANT_DATE.format(getLastReportedInstant());
  }

  @NotNull
  private void addPurchaseAndTransactionParams(List<Purchase> purchases)
      throws InvalidVATValueException {
    double sumTotalPurchased = 0.0;
    double sumVatHiProductsPurchased = 0.0;
    double sumVatLoProductsPurchased = 0.0;
    double sumVatHiSolidarity = 0.0;
    double sumVatLoSolidarity = 0.0;
    double sumDeposit = 0.0;
    double transactionSaldo = 0.0;
    double transactionCreditPayIn = 0.0;
    double transactionSpecialPayments = 0.0;
    double transactionPurchases = 0.0;

    // audit and calculate purchase sums

    List<ShoppingItem> reportedItems =
        QueryBuilder.selectAll(ShoppingItem.class)
            .where(ShoppingItem_.purchase.in(purchases))
            .getResultList();

    double vatLoValue = -1.0, vatHiValue = -1.0;
    final String DEPOSIT = Constants.DEPOSIT_SUPPLIER.getShortName();
    final String SOLIDARITY = Constants.SOLIDARITY_SUPPLIER.getShortName();
    for (ShoppingItem i : reportedItems) {
      final double itemValue = i.getRetailPrice();
      final double itemVatValue = i.getVatValue();
      final String itemSupplier = i.getSafeSuppliersShortName();
      if (i.getVat() == VAT.HIGH) {
        if (vatHiValue == -1.0) {
          vatHiValue = itemVatValue;
        } else if (vatHiValue != itemVatValue) {
          throw new InvalidVATValueException(VAT.HIGH, itemVatValue);
        }
        if (itemSupplier.equals(DEPOSIT)) {
          sumDeposit += itemValue;
        } else if (itemSupplier.equals(SOLIDARITY)) {
          sumVatHiSolidarity += itemValue;
        } else {
          sumVatHiProductsPurchased += itemValue;
        }
      } else {
        if (vatLoValue == -1.0) {
          vatLoValue = itemVatValue;
        } else if (vatLoValue != itemVatValue) {
          throw new InvalidVATValueException(VAT.LOW, itemVatValue);
        }
        if (itemSupplier.equals(SOLIDARITY)) {
          sumVatLoSolidarity += itemValue;
        } else {
          sumVatLoProductsPurchased += itemValue;
        }
      }
      sumTotalPurchased += itemValue;
    }

    for (AccountingReportItem i : reportItems) {
      double value = i.getSum();
      transactionSaldo -= value;
      switch (i.getReportGroup()) {
        case ASSISTED_PURCHASE, SOLO_PURCHASE -> transactionPurchases += value;
        case REFUND, OTHER -> transactionSpecialPayments += value;
        case PAYIN -> transactionCreditPayIn -= value;
      }
    }
    reportParams.put("vatHiValue", vatHiValue);
    reportParams.put("vatLoValue", vatLoValue);
    reportParams.put("sumTotalPurchased", sumTotalPurchased);
    reportParams.put("sumVatHiProductsPurchased", sumVatHiProductsPurchased);
    reportParams.put("sumVatLoProductsPurchased", sumVatLoProductsPurchased);
    reportParams.put("sumDeposit", sumDeposit);
    reportParams.put("sumVatHiSolidarity", sumVatHiSolidarity);
    reportParams.put("sumVatLoSolidarity", sumVatLoSolidarity);
    reportParams.put("transactionSaldo", transactionSaldo);
    reportParams.put("transactionCreditPayIn", transactionCreditPayIn);
    reportParams.put("transactionSpecialPayments", transactionSpecialPayments);
    reportParams.put("transactionPurchases", transactionPurchases);
  }

  public static void messageInvalidVatValues(Component parentComponent, String errorMessage) {
    JOptionPane.showMessageDialog(
        parentComponent,
        "Inkonsistente Steuersätze im Berichtszeitraum!\n"
            + "(%s)\n".formatted(errorMessage)
            + "Gab es zwischenzeitlich einen Wechsel des Steuersatzes?\n"
            + "Der Bericht muss für einen Zeitraum mit eindeutigen Steuersätzen ausgegeben werden!",
        "Uneindeutige Steuersätze",
        JOptionPane.ERROR_MESSAGE);
  }

  @Override
  Map<String, Object> getReportParams() {
    reportParams.put("reportTitle", getReportTitle(reportNo));
    return reportParams;
  }

  @Override
  Collection<AccountingReportItem> getDetailCollection() {
    return reportItems;
  }
}
