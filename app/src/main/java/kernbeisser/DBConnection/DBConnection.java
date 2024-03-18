package kernbeisser.DBConnection;

import jakarta.persistence.*;
import jakarta.persistence.criteria.*;
import java.util.*;
import javax.swing.*;
import kernbeisser.Config.Config;
import kernbeisser.Config.Config.DBAccess;
import kernbeisser.Config.IgnoreThis;
import kernbeisser.Exeptions.ClassIsSingletonException;
import kernbeisser.Exeptions.handler.UnexpectedExceptionHandler;
import kernbeisser.StartUp.LogIn.DBLogInController;
import kernbeisser.Windows.ViewContainers.JFrameWindow;
import lombok.Cleanup;
import lombok.extern.log4j.Log4j2;
import org.hibernate.service.spi.ServiceException;
import rs.groump.Access;

@Log4j2
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
    log.info(
        "Try to Login in with Username: \""
            + dbAccessData.getUsername()
            + "\" Password: ***********");
    try {
      entityManagerFactory = establishConnection("Kernbeisser", dbAccessData);
      log.info("Login successful");
      return true;
    } catch (ServiceException e) {
      log.warn("Log in failed: " + e.getMessage());
      e.printStackTrace();
      return false;
    } catch (Exception e) {
      throw UnexpectedExceptionHandler.showUnexpectedErrorWarning(e);
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
    log.info("reconnecting to DB");
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

  public static <T> List<T> getConditioned(Class<T> clazz, Condition... conditions) {
    @Cleanup EntityManager em = getEntityManager();
    @Cleanup("commit")
    EntityTransaction et = em.getTransaction();
    et.begin();
    return getConditioned(em, clazz, conditions);
  }

  public static <T> List<T> getConditioned(
      EntityManager em, Class<T> clazz, Condition... conditions) {
    CriteriaBuilder cb = em.getCriteriaBuilder();
    CriteriaQuery<T> cr = cb.createQuery(clazz);
    Root<T> root = cr.from(clazz);
    cr.select(root);
    if (conditions.length == 0) {
      return em.createQuery(cr).getResultList();
    }

    return em.createQuery(
            cr.where(
                    Arrays.stream(conditions)
                        .map(e -> e.buildPredicate(cb, root))
                        .toArray(Predicate[]::new))
                .orderBy())
        .getResultList();
  }

  public static <T> List<T> getAll(Class<T> clazz) {
    return getConditioned(clazz);
  }
}
