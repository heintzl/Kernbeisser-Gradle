package kernbeisser.Windows.UserInfo;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;
import kernbeisser.DBConnection.DBConnection;
import kernbeisser.DBEntities.Purchase;
import kernbeisser.DBEntities.Transaction;
import kernbeisser.DBEntities.User;
import kernbeisser.DBEntities.UserGroup;
import kernbeisser.Windows.MVC.IModel;
import lombok.Cleanup;
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
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    @Cleanup("commit")
    EntityTransaction et = em.getTransaction();
    et.begin();
    return em.createQuery(
            """
			SELECT t.id,
			       (select sum(
			       case when sumTr.toUserGroup.id = :ug then sumTr.value
			       when sumTr.fromUserGroup.id = :ug then -sumTr.value else 0 end) from Transaction sumTr where sumTr.date <= t.date and (sumTr.fromUserGroup.id = :ug OR sumTr.toUserGroup.id = :ug))
			FROM Transaction t
			WHERE t.fromUserGroup.id = :ug OR t.toUserGroup.id = :ug
			GROUP BY t.date
			ORDER BY t.date
			""",
            Object[].class)
        .setParameter("ug", userGroup.getId())
        .getResultStream()
        .collect(
            Collectors.toMap(
                resultColumns -> (Long) resultColumns[0],
                resultColumns -> (Double) resultColumns[1]));
  }

  public boolean incoming(Transaction transaction) {
    return (transaction.getToUser() != null
        && getUser().getUserGroup().containsUser(transaction.getToUser()));
  }
}
