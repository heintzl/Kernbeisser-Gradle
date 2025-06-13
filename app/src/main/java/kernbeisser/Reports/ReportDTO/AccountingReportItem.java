package kernbeisser.Reports.ReportDTO;

import java.time.Instant;
import kernbeisser.DBEntities.Purchase;
import kernbeisser.DBEntities.Transaction;
import kernbeisser.DBEntities.User;
import kernbeisser.Enums.AccountingReportGroups;
import kernbeisser.Useful.Constants;
import lombok.Data;

@Data
public class AccountingReportItem {

  private final Instant date;
  private final String description;
  private final String customerIdentification;
  private final String sellerIdentification;
  private final double sum;
  private final AccountingReportGroups reportGroup;

  public AccountingReportItem(Purchase purchase, boolean withNames) {
    this.date = purchase.getCreateDate();
    this.description = Long.toString(purchase.getBonNo());
    this.customerIdentification =
        userIdentification(purchase.getSession().getCustomer(), withNames);
    this.sellerIdentification = userIdentification(purchase.getSession().getSeller(), withNames);
    this.sum = purchase.getSum();
    switch (purchase.getSession().getSessionType()) {
      case ASSISTED -> this.reportGroup = AccountingReportGroups.ASSISTED_PURCHASE;
      case SOLO -> this.reportGroup = AccountingReportGroups.SOLO_PURCHASE;
      case null, default -> this.reportGroup = AccountingReportGroups.OTHER;
    }
  }

  public AccountingReportItem(Transaction transaction, boolean withNames) {
    this.date = transaction.getDate();
    this.description = transaction.getDescription();
    User customer;
    if (transaction.getFromUser().equals(Constants.SHOP_USER)) {
      customer = transaction.getToUser();
      sum = -transaction.getValue();
    } else {
      customer = transaction.getFromUser();
      sum = transaction.getValue();
    }
    customerIdentification = userIdentification(customer, withNames);
    sellerIdentification = null;
    switch (transaction.getTransactionType()) {
      case PAYIN -> this.reportGroup = AccountingReportGroups.PAYIN;
      case USER_GENERATED -> this.reportGroup = AccountingReportGroups.REFUND;
      case null, default -> this.reportGroup = AccountingReportGroups.OTHER;
    }
  }

  private static String userIdentification(User user, boolean withNames) {
    return withNames ? user.getFullName() : Integer.toString(user.getId());
  }
}
