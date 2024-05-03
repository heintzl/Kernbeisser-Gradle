package kernbeisser.Reports.ReportDTO;

import static kernbeisser.DBConnection.ExpressionFactory.asExpression;
import static kernbeisser.DBConnection.PredicateFactory.isMember;

import jakarta.persistence.Tuple;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import kernbeisser.DBConnection.QueryBuilder;
import kernbeisser.DBEntities.SaleSession;
import kernbeisser.DBEntities.SaleSession_;
import kernbeisser.DBEntities.Transaction_;
import kernbeisser.DBEntities.User;
import kernbeisser.DBEntities.User_;
import kernbeisser.Enums.PermissionConstants;
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
    double sum = 0;
    int lastUid = -1;
    User lastUser = null;
    Instant lastSaleSession = Instant.MIN;
    List<TrialMemberReportEntry> entries = new ArrayList<>();
    List<Tuple> resultList =
        QueryBuilder.select(
                SaleSession.class,
                SaleSession_.customer,
                SaleSession_.transaction.child(Transaction_.value),
                SaleSession_.transaction.child(Transaction_.date))
            .where(
                isMember(
                    asExpression(PermissionConstants.TRIAL_MEMBER.getPermission()),
                    SaleSession_.customer.child(User_.permissions)))
            .orderBy(SaleSession_.customer.asc())
            .getResultList();
    for (Tuple tuple : resultList) {
      User user = tuple.get(0, User.class);
      Double saleSessionTransactionValue = tuple.get(1, Double.class);
      Instant saleSessionDate = tuple.get(2, Instant.class);
      if (lastUid != user.getId()) {
        if (lastUid != -1) {
          entries.add(new TrialMemberReportEntry(user, sum, lastSaleSession));
        }
        sum = 0;
        lastUid = user.getId();
        lastUser = user;
        lastSaleSession = saleSessionDate;
      }
      sum += saleSessionTransactionValue;
      lastSaleSession =
          lastSaleSession.isAfter(saleSessionDate) ? lastSaleSession : saleSessionDate;
    }
    if (lastUid != -1) entries.add(new TrialMemberReportEntry(lastUser, sum, lastSaleSession));
    return entries;
  }

  public String getLastPurchaseAsString() {
    return lastPurchase.map(i -> Date.INSTANT_DATE.format(i)).orElse("-");
  }
}
