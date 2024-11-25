package kernbeisser.Windows.UserInfo;

import static kernbeisser.DBConnection.PredicateFactory.or;

import jakarta.persistence.Tuple;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import kernbeisser.DBConnection.QueryBuilder;
import kernbeisser.DBEntities.*;
import kernbeisser.Windows.MVC.IModel;
import lombok.Getter;

public class UserInfoModel implements IModel<UserInfoController> {

  @Getter private final User user;

  @Getter(lazy = true)
  private final Collection<Transaction> userTransactions = user.getAllValueChanges();

  @Getter(lazy = true)
  private final Collection<Purchase> userPurchases =
      user.getAllPurchases().stream()
          .sorted(Comparator.comparing(Purchase::getCreateDate).reversed())
          .toList();

  @Getter private final Map<Long, Double> transactionSums;

  public UserInfoModel(User user) {
    this.user = user;
    transactionSums = createTransactionSums(user.getUserGroup());
  }

  public Map<Long, Double> createTransactionSums(UserGroup userGroup) {
    double sum = 0.0;
    var resultList =
        QueryBuilder.select(
                Transaction.class,
                Transaction_.seqNo,
                Transaction_.value,
                Transaction_.toUserGroup.eq(userGroup))
            .where(
                or(
                    Transaction_.fromUserGroup.eq(userGroup),
                    Transaction_.toUserGroup.eq(userGroup)))
            .orderBy(Transaction_.date.asc())
            .getResultList();
    Map<Long, Double> idValueAfterMap = new HashMap<>(resultList.size());
    for (Tuple tuple : resultList) {
      long seqNo = tuple.get(0, Long.class);
      double value = tuple.get(1, Double.class);
      boolean incoming = tuple.get(2, Boolean.class);
      if (incoming) {
        sum += value;
      } else {
        sum -= value;
      }
      idValueAfterMap.put(seqNo, sum);
    }
    return idValueAfterMap;
  }

  public boolean incoming(Transaction transaction) {
    return (transaction.getToUserGroup() != null
        && getUser().getUserGroup().equals(transaction.getToUserGroup()));
  }
}
