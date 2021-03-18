package kernbeisser.Windows.UserInfo;

import kernbeisser.DBEntities.Transaction;
import kernbeisser.DBEntities.User;
import kernbeisser.Windows.MVC.IModel;

public class UserInfoModel implements IModel<UserInfoController> {

  private final User user;

  public UserInfoModel(User user) {
    this.user = user;
  }

  public User getUser() {
    return user;
  }

  public double getSignedTransactionValue(Transaction transaction) {
    if (transaction.getToUser() != null
        && user.getAllGroupMembers().contains(transaction.getToUser())) {
      return transaction.getValue();
    } else {
      return -transaction.getValue();
    }
  }
}
