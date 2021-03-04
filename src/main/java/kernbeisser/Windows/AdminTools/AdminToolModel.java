package kernbeisser.Windows.AdminTools;

import at.favre.lib.crypto.bcrypt.BCrypt;
import java.util.Collection;
import java.util.Random;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import kernbeisser.DBConnection.DBConnection;
import kernbeisser.DBEntities.User;
import kernbeisser.Enums.Setting;
import kernbeisser.Useful.Tools;
import kernbeisser.Windows.MVC.IModel;
import lombok.Cleanup;

public class AdminToolModel implements IModel<AdminToolController> {

  public String resetPassword(User selectedUser) {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    EntityTransaction et = em.getTransaction();
    et.begin();
    selectedUser = em.find(User.class, selectedUser.getId());
    String token = generateToken();
    selectedUser.setPassword(
        BCrypt.withDefaults().hashToString(Setting.HASH_COSTS.getIntValue(), token.toCharArray()));
    selectedUser.setForcePasswordChange(true);
    em.persist(selectedUser);
    em.flush();
    et.commit();
    return token;
  }

  public static String generateToken() {
    int generationLength = Setting.PASSWORD_TOKEN_GENERATION_LENGTH.getIntValue();
    char[] charTable =
        "AaBbCcDdEeFfGgHhIiJjKkLlMmNnOoPpQqRrSsTtUuVvWwXxYyZz1234567890".toCharArray();
    Random random = new Random();
    StringBuilder sb = new StringBuilder(generationLength);
    for (int i = 0; i < generationLength; i++) {
      sb.append(charTable[Math.abs(random.nextInt()) % charTable.length]);
    }
    return sb.toString();
  }

  public Collection<User> getAllUsers() {
    // only admin access -> removes permission checks
    return Tools.getAllUnProxy(User.class);
  }
}
