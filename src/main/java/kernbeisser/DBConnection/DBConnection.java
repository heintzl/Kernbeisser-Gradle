package kernbeisser.DBConnection;

import jakarta.persistence.*;
import jakarta.persistence.criteria.*;
import java.util.*;
import javax.swing.*;
import kernbeisser.Config.Config;
import kernbeisser.Config.Config.DBAccess;
import kernbeisser.Config.IgnoreThis;
import kernbeisser.Exeptions.ClassIsSingletonException;
import kernbeisser.Main;
import kernbeisser.StartUp.LogIn.DBLogInController;
import kernbeisser.Useful.Tools;
import kernbeisser.Windows.ViewContainers.JFrameWindow;
import lombok.Cleanup;
import org.hibernate.service.spi.ServiceException;
import rs.groump.Access;

public class DBConnection {

  static {
    try {
      IgnoreThis.class.getDeclaredField("TEST_FLAG");
    } catch (NoSuchFieldException e) {
      if (!Access.isActive()) {
        System.err.println("Access checking is not active:");
        JOptionPane.showMessageDialog(null, "Security system disabled shutting down system...");
        System.exit(-1);
      }
    }
  }

  private static EntityManagerFactory entityManagerFactory = null;

  private static EntityManagerFactory establishConnection(String name, DBAccess dbAccessData) {
    HashMap<String, String> properties = new HashMap<>(3);
    properties.put("jakarta.persistence.jdbc.user", dbAccessData.getUsername());
    properties.put(
        "jakarta.persistence.jdbc.url",
        dbAccessData.getUrl()
            + (dbAccessData.getUrl().contains("?") ? "&" : "")
            + "?characterEncoding="
            + dbAccessData.getEncoding());
    properties.put("hibernate.connection.pool_size", "50");
    properties.put("jakarta.persistence.jdbc.password", dbAccessData.getPassword());
    return Persistence.createEntityManagerFactory(name, properties);
  }

  public static boolean checkValidDBAccess(DBAccess dbAccessData) {
    try {
      establishConnection("Kernbeisser", dbAccessData).close();
      return true;
    } catch (ServiceException e) {
      return false;
    } catch (Exception e) {
      return true;
    }
  }

  public static boolean tryLogIn(DBAccess dbAccessData) {
    Main.logger.info(
        "Try to Login in with Username: \""
            + dbAccessData.getUsername()
            + "\" Password: ***********");
    try {
      entityManagerFactory = establishConnection("Kernbeisser", dbAccessData);
      Main.logger.info("Login successful");
      return true;
    } catch (ServiceException e) {
      Main.logger.warn("Log in failed: " + e.getMessage());
      e.printStackTrace();
      return false;
    } catch (Exception e) {
      Tools.showUnexpectedErrorWarning(e);
      return false;
    }
  }

  private static final Object DB_LOGIN_LOCK = new Object();

  public static void logInWithConfig() {
    if (!tryLogIn(Config.getConfig().getDBAccessData())) {
      try {
        DBLogInController.openDBLogInController(true)
            .withCloseEvent(DB_LOGIN_LOCK::notify)
            .openIn(new JFrameWindow());
      } catch (ClassIsSingletonException e) {
        JOptionPane.showMessageDialog(
            JOptionPane.getRootFrame(), "Bitte starte die Anwendung neu!");
      }
    }
  }

  public static void reload() {
    Main.logger.info("reconnecting to DB");
    entityManagerFactory.close();
    logInWithConfig();
  }

  public static EntityManager getEntityManager() {
    if (!isInitialized()) {
      logInWithConfig();
    }
    return entityManagerFactory.createEntityManager();
  }

  public static boolean isInitialized() {
    return entityManagerFactory != null;
  }

  public static <T> CriteriaQuery<T> getCriteriaQuery(EntityManager em, Class<T> clazz) {
    CriteriaBuilder cb = em.getCriteriaBuilder();
    return cb.createQuery(clazz);
  }

  public static <T> List<T> getConditioned(Class<T> clazz, FieldCondition... conditions) {
    @Cleanup EntityManager em = getEntityManager();
    CriteriaQuery<T> cr = getCriteriaQuery(em, clazz);
    Root<T> root = cr.from(clazz);
    cr.select(root);
    if (conditions.length == 0) {
      return em.createQuery(cr).getResultList();
    }
    return em.createQuery(
            cr.where(
                Arrays.stream(conditions)
                    .map(e -> e.buildPredicate(root))
                    .toArray(Predicate[]::new)))
        .getResultList();
  }

  public static <T> List<T> getAll(Class<T> clazz) {
    return getConditioned(clazz);
  }
}
