package kernbeisser.Windows.UserInfo;

import javax.persistence.EntityManager;
import kernbeisser.DBConnection.DBConnection;
import kernbeisser.DBEntities.Transaction;
import kernbeisser.DBEntities.User;
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

  public double getSignedTransactionValue(Transaction transaction) {
    if (transaction.getToUser() != null
        && getUser().getAllGroupMembers().contains(transaction.getToUser())) {
      return transaction.getValue();
    } else {
      return -transaction.getValue();
    }
  }
}
