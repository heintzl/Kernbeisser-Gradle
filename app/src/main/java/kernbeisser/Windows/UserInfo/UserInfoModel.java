package kernbeisser.Windows.UserInfo;

import jakarta.persistence.EntityManager;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import kernbeisser.DBConnection.DBConnection;
import kernbeisser.DBEntities.Transaction;
import kernbeisser.DBEntities.User;
import kernbeisser.DBEntities.UserGroup;
import kernbeisser.Windows.MVC.IModel;
import lombok.Cleanup;
import lombok.Getter;

public class UserInfoModel implements IModel<UserInfoController> {
  
  @Getter private final User user;
  @Getter private final Collection<Transaction> userTransactions;
  @Getter private final Map<Long, Double> transactionSums = new HashMap<>();

  public UserInfoModel(User user) {
    this.user = user;
    userTransactions = user.getAllValueChanges();
    collectTranscationSums(user);
  }

  private void collectTranscationSums(User user) {
    UserGroup userGroup = user.getUserGroup();
    double sum = 0.0;
    for (Transaction t : userTransactions) {
      double v = t.getValue();
      sum += getSignedTransactionValue(t);
      transactionSums.put(t.getId(), sum);
    }
  }

  public boolean incoming(Transaction transaction) {
    return (transaction.getToUser() != null
        && getUser().getUserGroup().containsUser(transaction.getToUser()));
  }
  

  public double getSignedTransactionValue(Transaction transaction) {
    if (incoming(transaction)) {
      return transaction.getValue();
    } else {
      return -transaction.getValue();
    }
  }
}
