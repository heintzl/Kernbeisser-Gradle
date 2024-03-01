package kernbeisser.Tasks;

import at.favre.lib.crypto.bcrypt.BCrypt;
import com.google.common.collect.Sets;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import java.util.*;
import javax.swing.*;
import kernbeisser.DBConnection.DBConnection;
import kernbeisser.DBEntities.*;
import kernbeisser.Enums.PermissionConstants;
import kernbeisser.Enums.Setting;
import kernbeisser.Enums.StatementType;
import kernbeisser.Exeptions.InvalidTransactionException;
import kernbeisser.Exeptions.MissingFullMemberException;
import kernbeisser.Reports.TransactionStatement;
import kernbeisser.Useful.Tools;
import kernbeisser.Windows.LogIn.LogInModel;
import lombok.Cleanup;
import rs.groump.Access;
import rs.groump.AccessDeniedException;
import rs.groump.AccessManager;

public class Users {

  private static final String defaultPassword =
      BCrypt.withDefaults().hashToString(Setting.HASH_COSTS.getIntValue(), "start".toCharArray());

  public static User[] parse(
      String[] stringRaw, HashSet<String> usernames, HashMap<String, Job> jobHashMap) {

    User user = new User();
    User secondary = new User();
    user.setShares(Integer.parseInt(stringRaw[3]));
    secondary.setFirstName(stringRaw[5]);
    secondary.setSurname(stringRaw[6]);
    // ExtraJobs: Unused, column 7
    user.setJobs(parseJobs(Arrays.copyOfRange(stringRaw, 7, 16), jobHashMap));

    user.setKernbeisserKey(Boolean.parseBoolean(stringRaw[18]) ? 0 : -1);
    user.setEmployee(Boolean.parseBoolean(stringRaw[19]));
    // IdentityCode: Unused, column 12
    // Username: Unknown, column 13
    // Password: Start, column 14
    user.setFirstName(stringRaw[23]);
    user.setSurname(stringRaw[24]);
    user.setPhoneNumber1(stringRaw[25]);
    /* omit sensitive user data
    user.setPhoneNumber2(stringRaw[26]);
    for (String s : stringRaw[27].split(" ")) {
      if (s.equals("")) {
        continue;
      }
      try {
        user.setTownCode(s);
      } catch (NumberFormatException e) {
        user.setTown(s);
      }
    }
    */
    // Permission?
    Permission permission = new Permission();
    Integer role = Integer.parseInt(stringRaw[28]);
    if (user.getShares() > 0) {
      permission = PermissionConstants.FULL_MEMBER.getPermission();
    } else if (role == 1) {
      permission = PermissionConstants.TRIAL_MEMBER.getPermission();
    } else {
      permission = PermissionConstants.BASIC_ACCESS.getPermission();
    }
    user.getPermissions().add(permission);
    user.setEmail(stringRaw[29]);
    // TransactionDates: not used, column 24
    // TransactionValues: not used, column 25
    // ommitted: user.setStreet(stringRaw[34]);
    user.setPassword(defaultPassword);
    secondary.setPassword(defaultPassword);
    generateUsername(usernames, user);
    generateUsername(usernames, secondary);
    user.setUpdateBy(User.getKernbeisserUser());
    return new User[] {user, secondary};
  }

  private static Set<Job> parseJobs(String[] stringJobs, HashMap<String, Job> jobHashMap) {
    Set<Job> userJobs = new HashSet<>();
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    @Cleanup(value = "commit")
    EntityTransaction et = em.getTransaction();
    et.begin();
    for (String job : stringJobs) {
      if (job.equals("no")) {
        continue;
      }
      if (!jobHashMap.containsKey(job)) {
        Job newJob = new Job();
        newJob.setName(job);
        em.persist(newJob);
        jobHashMap.put(job, newJob);
      }
      userJobs.add(jobHashMap.get(job));
    }
    em.flush();
    return userJobs;
  }

  public static final int INTEREST_THIS_YEAR_COLUMN = 2;
  public static final int SOLIDARITY_SURCHARGE_COLUMN = 4;

  public static UserGroup getUserGroup(String[] rawData) {
    UserGroup userGroup = new UserGroup();
    userGroup.setInterestThisYear(
        (int) (Float.parseFloat(rawData[INTEREST_THIS_YEAR_COLUMN].replace(",", "."))));
    userGroup.setSolidaritySurcharge(Integer.parseInt(rawData[SOLIDARITY_SURCHARGE_COLUMN]) / 100.);
    userGroup.setUpdateBy(User.getKernbeisserUser());
    return userGroup;
  }

  public static double getValue(String[] rawData) {
    return Tools.roundCurrency(Double.parseDouble(rawData[31].replace(",", ".")));
  }

  private static void generateUsername(HashSet<String> usernames, User user) {
    for (int i = 1; i < user.getSurname().length(); i++) {
      String generatedUsername =
          (user.getFirstName().split(" ")[0] + "." + user.getSurname().substring(0, i))
              .toLowerCase();
      if (!usernames.contains(generatedUsername)) {
        user.setUsername(generatedUsername);
        usernames.add(generatedUsername);
        break;
      }
    }
    if (user.getUsername() == null) {
      user.setUsername(user.getFirstName() + "." + user.getSurname() + new Random().nextLong());
    }
  }

  public static boolean switchUserGroup(int userId, int userGroupId)
      throws MissingFullMemberException {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    try {
      @Cleanup(value = "commit")
      EntityTransaction et = em.getTransaction();
      et.begin();
      User currentUser = em.find(User.class, userId);
      UserGroup current = currentUser.getUserGroup();
      UserGroup destination = em.find(UserGroup.class, userGroupId);
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
            .sendToPrinter("Auszug wird erstellt", Tools::showUnexpectedErrorWarning);
      }
      ;
    }
    return response != JOptionPane.CANCEL_OPTION;
  }

  public static void leaveUserGroup(User user) throws MissingFullMemberException {
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
    } catch (MissingFullMemberException e) {
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
