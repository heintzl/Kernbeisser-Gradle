package kernbeisser.Windows.UserInfo;

import static kernbeisser.DBConnection.PredicateFactory.or;

import jakarta.persistence.Tuple;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import kernbeisser.DBConnection.QueryBuilder;
import kernbeisser.DBEntities.Purchase;
import kernbeisser.DBEntities.Transaction;
import kernbeisser.DBEntities.TypeFields.TransactionField;
import kernbeisser.DBEntities.User;
import kernbeisser.DBEntities.UserGroup;
import kernbeisser.Windows.MVC.IModel;
import lombok.Getter;

public class UserInfoModel implements IModel<UserInfoController> {

  @Getter private final User user;

  @Getter(lazy = true)
  private final Collection<Transaction> userTransactions = user.getAllValueChanges();

  @Getter(lazy = true)
  private final Collection<Purchase> userPurchases = user.getAllPurchases();

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
                TransactionField.id,
                TransactionField.value,
                TransactionField.toUserGroup.eq(userGroup))
            .where(
                or(
                    TransactionField.fromUserGroup.eq(userGroup),
                    TransactionField.toUserGroup.eq(userGroup)))
            .orderBy(TransactionField.date.asc())
            .getResultList();
    Map<Long, Double> idValueAfterMap = new HashMap<>(resultList.size());
    for (Tuple tuple : resultList) {
      long id = tuple.get(0, Long.class);
      double value = tuple.get(1, Double.class);
      boolean incoming = tuple.get(2, Boolean.class);
      if (incoming) {
        sum += value;
      } else {
        sum -= value;
      }
      idValueAfterMap.put(id, sum);
    }
    return idValueAfterMap;
  }

  public boolean incoming(Transaction transaction) {
    return (transaction.getToUser() != null
        && getUser().getUserGroup().containsUser(transaction.getToUser()));
  }
}
