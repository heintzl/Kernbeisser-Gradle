package kernbeisser.Reports;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;
import kernbeisser.DBConnection.DBConnection;
import kernbeisser.DBEntities.Transaction;
import kernbeisser.DBEntities.User;
import kernbeisser.Enums.StatementType;
import lombok.Cleanup;

public class TransactionStatement extends Report {

  private final User user;
  private final StatementType statementType;
  private final boolean current;

  public TransactionStatement(User user, StatementType statementType, boolean current) {
    super("priceList", "Kontoauszug_" + user.toString() + ".pdf");
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
    LocalDate endDate = LocalDate.now();
    LocalDate startDate = LocalDate.parse("2010-01-01");
    switch (statementType) {
      case MONTH:
        startDate = endDate.withDayOfMonth(1);
        if (!current) {
          endDate = startDate.minus(1, ChronoUnit.DAYS);
          startDate = startDate.minus(1, ChronoUnit.MONTHS);
        }
        break;
      case QUARTER:
        int monthOfQuarter = endDate.getMonthValue() % 3;
        startDate = endDate.minus(monthOfQuarter, ChronoUnit.MONTHS).withDayOfMonth(1);
        if (!current) {
          endDate = startDate.minus(1, ChronoUnit.DAYS);
          startDate = startDate.minus(3, ChronoUnit.MONTHS);
        }
        break;
      case ANNUAL:
        startDate = endDate.withDayOfYear(1);
        if (!current) {
          endDate = startDate.minus(1, ChronoUnit.DAYS);
          startDate = startDate.minus(1, ChronoUnit.YEARS);
        }
        break;
      default:
        break;
    }
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    return em.createQuery(
            "select t from Transaction t where (from.id = :u or to.id = :u) and date >= :sd and date <= :ed",
            Transaction.class)
        .setParameter("u", user.getId())
        .setParameter("sd", startDate)
        .setParameter("ed", endDate)
        .getResultStream()
        .collect(Collectors.toList());
  }
}
