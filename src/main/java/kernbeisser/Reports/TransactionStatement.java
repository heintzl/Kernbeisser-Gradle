package kernbeisser.Reports;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import kernbeisser.DBEntities.User;
import kernbeisser.Enums.StatementType;

public class TransactionStatement extends Report {

  private final User user;
  private final StatementType statementType;
  private final boolean current;

  public TransactionStatement(User user, StatementType statementType, boolean current) {
    super("transactionStatement", "Kontoauszug_" + user.toString() + ".pdf");
    this.user = user;
    this.statementType = statementType;
    this.current = current;
  }

  @Override
  Map<String, Object> getReportParams() {
    Map<String, Object> params = new HashMap<>();
    params.put("user", user.getFullName());
    params.put("userGroup", user.getUserGroup().getMemberString());
    return params;
  }

  @Override
  Collection<?> getDetailCollection() {
    return null;
  }
}
