package kernbeisser.Reports;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import kernbeisser.DBConnection.DBConnection;
import kernbeisser.DBEntities.Transaction;
import kernbeisser.DBEntities.User;
import kernbeisser.DBEntities.UserGroup;
import kernbeisser.Enums.StatementType;
import lombok.Cleanup;

public class TransactionStatement extends Report {

  private final UserGroup userGroup;
  private final User user;
  private final StatementType statementType;
  private final boolean current;
  private ZonedDateTime startDate;
  private ZonedDateTime endDate;
  private final Collection<Transaction> userTransactions;

  public TransactionStatement(User user, StatementType statementType, boolean current) {
    this(user.getUserGroup(), user, statementType, current);
  }

  public TransactionStatement(
      UserGroup userGroup, User user, StatementType statementType, boolean current) {
    super(
        "transactionStatement",
        "Kontoauszug_" + (user == null ? String.valueOf(userGroup.getId()) : user.toString()));
    this.userGroup = userGroup;
    this.user = user;
    this.statementType = statementType;
    this.current = current;
    ZoneId local = ZoneId.systemDefault();
    endDate = ZonedDateTime.now(local);
    startDate = ZonedDateTime.of(2010, 1, 1, 0, 0, 0, 0, local);
    switch (statementType) {
      case MONTH:
        startDate = endDate.withDayOfMonth(1);
        if (!current) {
          endDate = startDate.minus(1, ChronoUnit.DAYS);
          startDate = startDate.minus(1, ChronoUnit.MONTHS);
        }
        break;
      case QUARTER:
        int monthOfQuarter = (endDate.getMonthValue() - 1) % 3;
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
    @Cleanup(value = "commit")
    EntityTransaction et = em.getTransaction();
    et.begin();
    userTransactions =
        em.createQuery(
                "select t from Transaction t where :ug IN (fromUserGroup.id, toUserGroup.id)",
                Transaction.class)
            .setParameter("ug", userGroup.getId())
            .getResultList();
  }

  @Override
  Map<String, Object> getReportParams() {
    Map<String, Object> params = new HashMap<>();
    double startValue =
        userTransactions.stream()
            .filter(t -> t.getDate().isBefore(startDate.toInstant()))
            .mapToDouble(t -> (t.getFromUserGroup().equals(userGroup) ? -1.0 : 1.0) * t.getValue())
            .reduce(Double::sum)
            .orElse(0.0);
    double endValue =
        userTransactions.stream()
            .filter(t -> !t.getDate().isAfter(endDate.toInstant()))
            .mapToDouble(t -> (t.getFromUserGroup().equals(userGroup) ? -1.0 : 1.0) * t.getValue())
            .reduce(Double::sum)
            .orElse(0.0);
    params.put(
        "userName",
        user == null
            ? userGroup.getMembersAsString() + "(" + userGroup.getId() + ")"
            : user.getFullName());
    params.put("userGroup", userGroup);
    params.put("startValue", startValue);
    params.put("endValue", endValue);
    String stType = statementType.toString();
    params.put(
        "statementType",
        (!current && statementType != StatementType.FULL
                ? "Vor" + stType.substring(0, 1).toLowerCase()
                : stType.substring(0, 1))
            + stType.substring(1));
    return params;
  }

  @Override
  Collection<Transaction> getDetailCollection() {
    return userTransactions.stream()
        .filter(
            t ->
                !(t.getDate().isBefore(startDate.toInstant())
                    || t.getDate().isAfter(endDate.toInstant())))
        .collect(Collectors.toList());
  }
}
