package kernbeisser.Tasks;

import static kernbeisser.DBConnection.ExpressionFactory.asExpression;
import static kernbeisser.DBConnection.PredicateFactory.greaterOrEq;
import static kernbeisser.DBConnection.PredicateFactory.or;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Tuple;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import kernbeisser.DBConnection.DBConnection;
import kernbeisser.DBConnection.QueryBuilder;
import kernbeisser.DBEntities.*;
import kernbeisser.Enums.Setting;
import kernbeisser.Enums.TransactionType;
import kernbeisser.Exeptions.InconsistentUserGroupValueException;
import kernbeisser.Exeptions.InvalidValue;
import kernbeisser.Exeptions.MissingFullMemberException;
import kernbeisser.Exeptions.handler.UnexpectedExceptionHandler;
import kernbeisser.Reports.UserBalanceReport;
import lombok.Cleanup;
import rs.groump.AccessDeniedException;

public class AutomaticTasks {

  public static void entryChecks()
      throws InvalidValue, InconsistentUserGroupValueException, MissingFullMemberException {
    exportMonthlyUserBalanceReport();
    refreshActivity();
    checkAdminConsistency();
    checkUserGroupConsistency();
  }

  private static void exportMonthlyUserBalanceReport() {
    LocalDate lastUserBalance =
        Instant.parse(Setting.LAST_USER_BALANCE_REPORT.getStringValue())
            .atZone(ZoneId.systemDefault())
            .toLocalDate();

    if (lastUserBalance.getMonth() != LocalDate.now().getMonth()) {
      final AtomicBoolean success = new AtomicBoolean(true);
      new UserBalanceReport(null, true)
          .exportPdfToCloud(
              "Kontostände werden exportiert",
              (e) -> {
                success.set(false);
              });
      if (success.get()) {
        Setting.LAST_USER_BALANCE_REPORT.changeValue(
            Instant.now().truncatedTo(ChronoUnit.DAYS).toString());
      }
    }
  }

  private static void checkValidUserGroupMemberships() throws MissingFullMemberException {
    int lastUgId = -1;
    boolean hasFullMember = true;
    int memberCount = 1;
    // orders all users by ug, then looks for invalid userGroups
    for (Tuple tuple :
        QueryBuilder.select(User.class, User_.userGroup.child(UserGroup_.id), User.IS_FULL_USER)
            .orderBy(User_.userGroup.child(UserGroup_.id).asc())
            .getResultList()) {
      int ugId = Objects.requireNonNull(tuple.get(0, Integer.class));
      boolean isFullMember = Objects.requireNonNull(tuple.get(1, Boolean.class));
      if (ugId != lastUgId) {
        checkValidUserGroup(lastUgId, memberCount, hasFullMember);
        hasFullMember = false;
        memberCount = 0;
        lastUgId = ugId;
      }
      hasFullMember = hasFullMember | isFullMember;
      memberCount++;
    }
    checkValidUserGroup(lastUgId, memberCount, hasFullMember);
  }

  private static void checkValidUserGroup(int ugId, int memberCount, boolean hasFullMember)
      throws MissingFullMemberException {
    if (memberCount <= 1) return;
    if (hasFullMember) return;
    throw new MissingFullMemberException(
        "Benutzergruppe(n) ohne Vollmitglied gefunden. Bitte den Vorstand informieren.");
  }

  private static void refreshActivity() {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    @Cleanup(value = "commit")
    EntityTransaction et = em.getTransaction();
    et.begin();
    List<Integer> activeUserIds =
        QueryBuilder.select(Transaction_.fromUser.child(User_.id))
            .where(
                greaterOrEq(
                    Transaction_.date,
                    asExpression(
                        Instant.now()
                            .minus(Setting.DAYS_BEFORE_INACTIVITY.getIntValue(), ChronoUnit.DAYS))),
                Transaction_.transactionType.eq(TransactionType.PURCHASE))
            .distinct()
            .getResultList();
    List<User> inactiveUsers =
        QueryBuilder.selectAll(User.class)
            .where(User_.active.eq(true), User_.id.in(activeUserIds).not())
            .getResultList(em);
    for (User u : inactiveUsers) {
      u.setSetUpdatedBy(false);
      u.setActive(false);
      em.persist(u);
    }
    em.flush();
  }

  private static void checkAdminConsistency() throws InvalidValue {
    try {
      User adminUser = QueryBuilder.getByProperty(User_.username, "Admin");
      if (QueryBuilder.selectAll(Transaction.class)
          .where(
              or(
                  Transaction_.toUserGroup.eq(adminUser.getUserGroup()),
                  Transaction_.fromUserGroup.eq(adminUser.getUserGroup())))
          .hasResult()) {
        throw new InvalidValue("Found transactions involving the admin user!");
      }
      if (adminUser.getUserGroup().getValue() != 0.0)
        throw new InvalidValue("The admin user group has value!");
    } catch (AccessDeniedException ignored) {
    } catch (Exception e) {
      throw UnexpectedExceptionHandler.showUnexpectedErrorWarning(e);
    }
  }

  private static Map<Integer, Double> getInvalidUserGroupTransactionSums() {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    @Cleanup(value = "commit")
    EntityTransaction et = em.getTransaction();
    et.begin();
    Map<Integer, Double> overValueTransactionSumThreshold = new HashMap<>();
    Map<Integer, Double> valueMap = UserGroup.getValueMapAt(em, Instant.now());
    for (Tuple tuple : QueryBuilder.select(UserGroup_.id, UserGroup_.value).getResultList()) {
      Integer id = tuple.get(0, Integer.class);
      Double value = tuple.get(1, Double.class);
      Double transactionSum = valueMap.getOrDefault(id, 0.0);
      if (Math.abs(transactionSum - value) > 0.004) {
        overValueTransactionSumThreshold.put(id, transactionSum);
      }
    }
    return overValueTransactionSumThreshold;
  }

  private static void checkUserGroupConsistency()
      throws InconsistentUserGroupValueException, MissingFullMemberException {
    if (!getInvalidUserGroupTransactionSums().isEmpty()) {
      throw new InconsistentUserGroupValueException();
    }
    checkValidUserGroupMemberships();
  }
}
