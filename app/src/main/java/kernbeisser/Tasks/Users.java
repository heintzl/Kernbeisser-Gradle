package kernbeisser.Tasks;

import com.google.common.collect.Sets;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import java.util.*;
import javax.naming.OperationNotSupportedException;
import javax.swing.*;
import kernbeisser.DBConnection.DBConnection;
import kernbeisser.DBEntities.*;
import kernbeisser.Enums.StatementType;
import kernbeisser.Exeptions.InvalidTransactionException;
import kernbeisser.Exeptions.MissingFullMemberException;
import kernbeisser.Exeptions.handler.UnexpectedExceptionHandler;
import kernbeisser.Reports.TransactionStatement;
import kernbeisser.Useful.Tools;
import kernbeisser.Windows.LogIn.LogInModel;
import lombok.Cleanup;
import rs.groump.Access;
import rs.groump.AccessDeniedException;
import rs.groump.AccessManager;

public class Users {
  public static boolean switchUserGroup(int userId, int userGroupId)
      throws MissingFullMemberException, OperationNotSupportedException {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    try {
      @Cleanup(value = "commit")
      EntityTransaction et = em.getTransaction();
      et.begin();
      User currentUser = em.find(User.class, userId);
      UserGroup current = currentUser.getUserGroup();
      UserGroup destination = em.find(UserGroup.class, userGroupId);
      if (destination.getMembers().stream().anyMatch(User::isUnreadable)) {
        throw new OperationNotSupportedException("target usergroup has invalid member");
      }
      LogInModel.checkRefreshRequirements(currentUser, current, destination);
      if (current.getMembers().size() < 2) {
        if (!confirmGroupVoid(currentUser)) return false;
        Transaction.switchGroupTransaction(
            em, currentUser, current, destination, current.getValue());
        Access.runWithAccessManager(
            AccessManager.ACCESS_GRANTED,
            () ->
                destination.setInterestThisYear(
                    destination.getInterestThisYear() + current.getInterestThisYear()));
      }
      currentUser.setUserGroup(destination);
      em.persist(destination);
      em.persist(currentUser);
      em.close();
      return true;
    } catch (InvalidTransactionException e) {
      JOptionPane.showMessageDialog(
          null,
          "Die Kontoübertragung ist fehlgeschlagen!",
          "Gruppenwechsel",
          JOptionPane.ERROR_MESSAGE);
      return false;
    } catch (AccessDeniedException p) {
      JOptionPane.showMessageDialog(
          null,
          "Fehlende Berechtigung: " + p.getMessage(),
          "Gruppenwechsel",
          JOptionPane.ERROR_MESSAGE);
      return false;
    } finally {
      LogInModel.refreshAccessManagerIfRequired();
    }
  }

  private static boolean confirmGroupVoid(User user) {
    Tools.beep();
    int response =
        JOptionPane.showConfirmDialog(
            null,
            "Die bisherige Benutzergruppe von "
                + user.getFullName()
                + " wird durch diesen Vorgang aufgelöst. Danach können die Umsätze dieser Gruppe nicht mehr nachvollzogen werden. Soll zum Abschluss ein Kontoauszug erstellt werden?",
            "Gruppenwechsel",
            JOptionPane.YES_NO_CANCEL_OPTION,
            JOptionPane.QUESTION_MESSAGE);
    if (response == JOptionPane.YES_OPTION) {
      JComboBox<StatementType> statementType = new JComboBox<>();
      Arrays.stream(StatementType.values()).forEach(statementType::addItem);
      statementType.setSelectedItem(StatementType.ANNUAL);
      if (JOptionPane.showConfirmDialog(
              null, statementType, "Art des Auszugs", JOptionPane.OK_CANCEL_OPTION)
          == (JOptionPane.OK_OPTION)) {
        new TransactionStatement(user, (StatementType) statementType.getSelectedItem(), true)
            .sendToPrinter(
                "Auszug wird erstellt", UnexpectedExceptionHandler::showUnexpectedErrorWarning);
      }
      ;
    }
    return response != JOptionPane.CANCEL_OPTION;
  }

  public static void leaveUserGroup(User user)
      throws MissingFullMemberException, OperationNotSupportedException {
    UserGroup newUserGroup = new UserGroup();
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    @Cleanup(value = "commit")
    EntityTransaction et = em.getTransaction();
    et.begin();
    em.persist(newUserGroup);
    et.commit();
    et.begin();
    try {
      switchUserGroup(user.getId(), newUserGroup.getId());
    } catch (MissingFullMemberException | OperationNotSupportedException e) {
      em.remove(newUserGroup);
      throw e;
    }
  }

  public static User createTestUserFrom(User user) throws MissingFullMemberException {
    User newUser = new User();
    User loggedIn = LogInModel.getLoggedIn();
    newUser.setUsername("test." + user.getUsername());
    newUser.setFirstName(user.getFirstName());
    newUser.setSurname("Test: " + user.getSurname());
    newUser.setPermissions(Sets.newHashSet(user.getPermissions()));
    newUser.setPhoneNumber1(user.getPhoneNumber1());
    newUser.setTestOnly(true);
    newUser.setPassword(loggedIn.getPassword());
    newUser.setUserGroup(loggedIn.getUserGroup());
    Tools.persist(newUser);
    return newUser;
  }
}
