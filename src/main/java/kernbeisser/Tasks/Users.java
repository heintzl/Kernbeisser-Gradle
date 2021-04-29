package kernbeisser.Tasks;

import at.favre.lib.crypto.bcrypt.BCrypt;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.swing.*;
import kernbeisser.DBConnection.DBConnection;
import kernbeisser.DBEntities.Job;
import kernbeisser.DBEntities.Transaction;
import kernbeisser.DBEntities.User;
import kernbeisser.DBEntities.UserGroup;
import kernbeisser.Enums.Setting;
import kernbeisser.Enums.StatementType;
import kernbeisser.Exeptions.InvalidTransactionException;
import kernbeisser.Reports.TransactionStatement;
import kernbeisser.Useful.Tools;
import lombok.Cleanup;

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
    user.setExtraJobs(stringRaw[7]);
    user.setJobs(Tools.extract(HashSet::new, stringRaw[8], "§", jobHashMap::get));
    user.setKernbeisserKey(Boolean.parseBoolean(stringRaw[10]) ? 0 : -1);
    user.setEmployee(Boolean.parseBoolean(stringRaw[11]));
    // IdentityCode: Unused, column 12
    // Username: Unknown, column 13
    // Password: Start, column 14
    user.setFirstName(stringRaw[15]);
    user.setSurname(stringRaw[16]);
    user.setPhoneNumber1(stringRaw[17]);
    user.setPhoneNumber2(stringRaw[18]);
    for (String s : stringRaw[19].split(" ")) {
      if (s.equals("")) {
        continue;
      }
      try {
        user.setTownCode(s);
      } catch (NumberFormatException e) {
        user.setTown(s);
      }
    }
    // Permission?
    switch (Integer.parseInt(stringRaw[20])) {
        // TODO
    }
    user.setEmail(stringRaw[21]);
    // CreateDate: isn't used(create new CreateDate), column 22
    // TransactionDates: not used, column 24
    // TransactionValues: not used, column 25
    user.setStreet(stringRaw[26]);
    user.setPassword(defaultPassword);
    secondary.setPassword(defaultPassword);
    generateUsername(usernames, user);
    generateUsername(usernames, secondary);
    return new User[] {user, secondary};
  }

  public static final int INTEREST_THIS_YEAR_COLUMN = 2;
  public static final int SOLIDARITY_SURCHARGE_COLUMN = 3;

  public static UserGroup getUserGroup(String[] rawData) {
    UserGroup userGroup = new UserGroup(getValue(rawData));
    userGroup.setInterestThisYear(
        (int) (Float.parseFloat(rawData[INTEREST_THIS_YEAR_COLUMN].replace(",", "."))));
    userGroup.setSolidaritySurcharge(Integer.parseInt(rawData[SOLIDARITY_SURCHARGE_COLUMN]) / 100.);
    return userGroup;
  }

  public static double getValue(String[] rawData) {
    return Tools.roundCurrency(Double.parseDouble(rawData[23].replace(",", ".")));
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
    if (user.getUsername().equals(new User().getUsername())) {
      user.setUsername(user.getFirstName() + "." + user.getSurname() + new Random().nextLong());
    }
  }

  public static void switchUserGroup(int userId, int userGroupId) {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    try {
      @Cleanup(value = "commit")
      EntityTransaction et = em.getTransaction();
      et.begin();
      User currentUser = em.find(User.class, userId);
      UserGroup current = currentUser.getUserGroup();
      UserGroup destination = em.find(UserGroup.class, userGroupId);
      if (current.getMembers().size() < 2) {
        if (!confirmGroupVoid(currentUser)) return;
        Transaction.switchGroupTransaction(
            em, currentUser, current, destination, current.getValue());
        destination.setInterestThisYear(
            destination.getInterestThisYear() + current.getInterestThisYear());
      }
      em.persist(destination);
      currentUser.setUserGroup(destination);
      em.persist(currentUser);
      em.close();
    } catch (InvalidTransactionException e) {
      JOptionPane.showMessageDialog(
          null,
          "Die Kontoübertragung ist fehlgeschlagen!",
          "Gruppenwechsel",
          JOptionPane.ERROR_MESSAGE);
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
      Arrays.stream(StatementType.values()).forEach(s -> statementType.addItem(s));
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

  public static void leaveUserGroup(User user) {
    UserGroup newUserGroup = new UserGroup(0);
    Tools.persist(newUserGroup);
    switchUserGroup(user.getId(), newUserGroup.getId());
  }
}
