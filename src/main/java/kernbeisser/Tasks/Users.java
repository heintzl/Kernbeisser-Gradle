package kernbeisser.Tasks;

import at.favre.lib.crypto.bcrypt.BCrypt;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;
import kernbeisser.DBEntities.Job;
import kernbeisser.DBEntities.User;
import kernbeisser.DBEntities.UserGroup;
import kernbeisser.Enums.Setting;
import kernbeisser.Useful.Tools;

public class Users {

  private static final String defaultPassword =
      BCrypt.withDefaults().hashToString(Setting.HASH_COSTS.getIntValue(), "start".toCharArray());

  public static User[] parse(
      String[] stringRaw, HashSet<String> usernames, HashMap<String, Job> jobHashMap) {

    User user = new User();
    User secondary = new User();
    user.setShares(Integer.parseInt(stringRaw[3]));
    user.setSolidaritySurcharge(Integer.parseInt(stringRaw[4]) / 100.);
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
        user.setTownCode(Integer.parseInt(s));
      } catch (NumberFormatException e) {
        user.setTown(s);
      }
    }
    // Permission?
    switch (Integer.parseInt(stringRaw[20])) {
        // TODO
    }
    user.setEmail(stringRaw[21]);
    // CreateDate: is't used(create new CreateDate), column 22
    // TransactionDates: not used, column 24
    // TransactionValues: not used, column 25
    user.setStreet(stringRaw[26]);
    user.setPassword(defaultPassword);
    secondary.setPassword(defaultPassword);
    generateUsername(usernames, user);
    generateUsername(usernames, secondary);
    return new User[] {user, secondary};
  }

  public static UserGroup getUserGroup(String[] rawData) {
    UserGroup userGroup = new UserGroup();
    userGroup.setInterestThisYear((int) (Float.parseFloat(rawData[2].replace(",", "."))));
    return userGroup;
  }

  public static double getValue(String[] rawData) {
    return Double.parseDouble(rawData[23].replace(",", "."));
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
}
