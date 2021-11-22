package kernbeisser.Windows.UserInfo;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import kernbeisser.DBConnection.DBConnection;
import kernbeisser.DBEntities.Transaction;
import kernbeisser.DBEntities.User;
import kernbeisser.DBEntities.UserGroup;
import kernbeisser.Windows.MVC.IModel;
import lombok.Cleanup;

public class UserInfoModel implements IModel<UserInfoController> {

  private final int userId;

  public UserInfoModel(User user) {
    this.userId = user.getId();
  }

  public User getUser() {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    return em.find(User.class, userId);
  }

  public boolean incoming(Transaction transaction) {
    return (transaction.getToUser() != null
        && getUser().getAllGroupMembers().contains(transaction.getToUser()));
  }

  public double getSignedTransactionValue(Transaction transaction) {
    if (incoming(transaction)) {
      return transaction.getValue();
    } else {
      return -transaction.getValue();
    }
  }

  public double getValueAfterTransaction(Transaction transaction, UserGroup user) {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    @Cleanup("commit")
    EntityTransaction et = em.getTransaction();
    et.begin();
    Double outgoing =
        em.createQuery(
                "select sum(t.value) from Transaction t where t.date <= :cd and fromUserGroup = :u",
                Double.class)
            .setParameter("cd", transaction.getDate())
            .setParameter("u", user)
            .getSingleResult();
    Double incoming =
        em.createQuery(
                "select sum(t.value) from Transaction t where t.date <= :cd and toUserGroup = :u",
                Double.class)
            .setParameter("cd", transaction.getDate())
            .setParameter("u", user)
            .getSingleResult();
    return (incoming == null ? 0 : incoming) - (outgoing == null ? 0 : outgoing);
  }
}
