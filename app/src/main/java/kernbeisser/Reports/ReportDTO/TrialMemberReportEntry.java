package kernbeisser.Reports.ReportDTO;

import static kernbeisser.DBConnection.ExpressionFactory.asExpression;
import static kernbeisser.DBConnection.PredicateFactory.isMember;

import java.time.Instant;
import java.util.*;
import kernbeisser.DBConnection.QueryBuilder;
import kernbeisser.DBEntities.*;
import kernbeisser.Enums.PermissionConstants;
import kernbeisser.Enums.TransactionType;
import kernbeisser.Useful.Date;
import lombok.Getter;

public class TrialMemberReportEntry {

  @Getter private final User user;
  @Getter private final double sumPurchase;
  private final Optional<Instant> lastPurchase;

  public TrialMemberReportEntry(User user, double sumPurchase, Instant lastPurchase) {
    this.user = user;
    this.sumPurchase = sumPurchase;
    this.lastPurchase = Optional.ofNullable(lastPurchase);
  }

  public static List<TrialMemberReportEntry> getAllTrialMembers() {
    List<User> trialMembers =
        QueryBuilder.selectAll(User.class)
            .where(
                isMember(
                    asExpression(PermissionConstants.TRIAL_MEMBER.getPermission()),
                    User_.permissions))
            .getResultList();

    List<Transaction> trialMemberTransactions =
        QueryBuilder.selectAll(Transaction.class)
            .where(
                Transaction_.transactionType.eq(TransactionType.PURCHASE),
                Transaction_.fromUser.in(trialMembers))
            .getResultList();

    Map<User, Instant> maxPurchaseDates = new HashMap<>();
    Map<User, Double> purchaseSums = new HashMap<>();
    for (Transaction transaction : trialMemberTransactions) {
      User purchaser = transaction.getFromUser();
      Instant purchaseDate = transaction.getDate();
      double purchaseValue = transaction.getValue();
      if (maxPurchaseDates.containsKey(purchaser)) {
        Instant oldDate = maxPurchaseDates.get(purchaser);
        if (purchaseDate.isAfter(oldDate)) {
          maxPurchaseDates.replace(purchaser, purchaseDate);
        }
        purchaseSums.replace(purchaser, purchaseSums.get(purchaser) + purchaseValue);
      } else {
        maxPurchaseDates.put(purchaser, purchaseDate);
        purchaseSums.put(purchaser, purchaseValue);
      }
    }

    return trialMembers.stream()
        .map(
            u ->
                new TrialMemberReportEntry(
                    u, purchaseSums.getOrDefault(u, 0.0), maxPurchaseDates.getOrDefault(u, null)))
        .toList();
  }

  public String getLastPurchaseAsString() {
    return lastPurchase.map(i -> Date.INSTANT_DATE.format(i)).orElse("-");
  }
}
