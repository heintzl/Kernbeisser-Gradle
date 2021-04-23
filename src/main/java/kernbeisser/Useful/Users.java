package kernbeisser.Useful;

import at.favre.lib.crypto.bcrypt.BCrypt;
import java.nio.charset.StandardCharsets;
import java.util.Random;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import kernbeisser.Config.IgnoreThis;
import kernbeisser.DBConnection.DBConnection;
import kernbeisser.DBEntities.User;
import kernbeisser.Enums.Setting;
import lombok.Cleanup;

public class Users {
  public String resetPassword(User selectedUser) {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    @Cleanup(value = "commit")
    EntityTransaction et = em.getTransaction();
    et.begin();
    selectedUser = em.find(User.class, selectedUser.getId());
    String token = generateToken();
    selectedUser.setPassword(
        BCrypt.withDefaults().hashToString(Setting.HASH_COSTS.getIntValue(), token.toCharArray()));
    selectedUser.setForcePasswordChange(true);
    em.persist(selectedUser);
    em.flush();
    return token;
  }

  public static String generateToken() {
    int generationLength = Setting.PASSWORD_TOKEN_GENERATION_LENGTH.getIntValue();
    char[] charTable = "AaBbCcDdEeFfGgHhJjKkLlMmNnPpQqRrSsTtUuVvWwXxYyZz23456789".toCharArray();
    Random random = new Random();
    StringBuilder sb = new StringBuilder(generationLength);
    for (int i = 0; i < generationLength; i++) {
      sb.append(charTable[Math.abs(random.nextInt()) % charTable.length]);
    }
    return sb.toString();
  }

  public static String generateUserRelatedToken(String username) {
    return new String(
            BCrypt.withDefaults()
                .hash(
                    4,
                    new byte[16],
                    xor(username.getBytes(StandardCharsets.UTF_8), IgnoreThis.ignoreIt())),
            StandardCharsets.UTF_8)
        .substring(40);
  }

  public static byte pos(byte[] bytes, int index) {
    return bytes[index % bytes.length];
  }

  public static byte[] xor(byte[] a, byte[] b) {
    byte[] out = new byte[a.length];
    for (int i = 0; i < a.length; i++) {
      out[i] = (byte) (0xff & ((int) a[i]) ^ ((int) pos(b, i)));
    }
    return out;
  }
}
