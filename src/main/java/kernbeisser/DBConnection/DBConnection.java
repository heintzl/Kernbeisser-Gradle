package kernbeisser.DBConnection;

import com.google.common.collect.Lists;
import java.util.*;
import javax.persistence.*;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.swing.*;
import kernbeisser.Config.Config;
import kernbeisser.Config.Config.DBAccess;
import kernbeisser.Config.IgnoreThis;
import kernbeisser.DBEntities.SystemSetting;
import kernbeisser.Enums.Setting;
import kernbeisser.Exeptions.ClassIsSingletonException;
import kernbeisser.Main;
import kernbeisser.Security.Access.Access;
import kernbeisser.StartUp.LogIn.DBLogInController;
import kernbeisser.Useful.Tools;
import kernbeisser.VersionIntegrationTools.Version;
import kernbeisser.Windows.ViewContainers.JFrameWindow;
import lombok.Cleanup;
import org.hibernate.service.spi.ServiceException;

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
    properties.put("javax.persistence.jdbc.user", dbAccessData.getUsername());
    properties.put(
        "javax.persistence.jdbc.url",
        dbAccessData.getUrl()
            + (dbAccessData.getUrl().contains("?") ? "&" : "")
            + "?characterEncoding="
            + dbAccessData.getEncoding());
    properties.put("javax.persistence.jdbc.password", dbAccessData.getPassword());
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
      Main.logger.warn("Log in failed");
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

  public static void updateDatabase() {
    Main.logger.info("updating Database");
    EntityManager em = getEntityManager();
    EntityTransaction et = em.getTransaction();
    et.begin();
    em.createNativeQuery("SET FOREIGN_KEY_CHECKS = 0").executeUpdate();
    for (Object o :
        em.createNativeQuery(
                "select TABLE_NAME from information_schema.TABLES where TABLE_SCHEMA = 'kernbeisser'")
            .getResultList()) {
      Main.logger.info("dropping DB Table " + o);
      if (o.equals("settingvalue")) {
        continue;
      }
      em.createNativeQuery("drop table " + o).executeUpdate();
    }
    em.createNativeQuery("SET FOREIGN_KEY_CHECKS = 1").executeUpdate();
    em.flush();
    et.commit();
    em.close();
    reload();
    SystemSetting.setValue(SystemSetting.DB_VERSION, Version.newestVersion().name());
    Setting.DB_INITIALIZED.changeValue(false);
    Setting.INFO_LINE_LAST_CATALOG.changeValue(Setting.INFO_LINE_LAST_CATALOG.getDefaultValue());
    Main.logger.info("DB update complete");
  }

  public static boolean isInitialized() {
    return entityManagerFactory != null;
  }

  public static <T> CriteriaQuery<T> getCriteriaQuery(EntityManager em, Class<T> clazz) {
    CriteriaBuilder cb = em.getCriteriaBuilder();
    return cb.createQuery(clazz);
  }

  public static <T, C> List<T> getConditioned(
      Class<T> clazz, String conditionFieldName, Collection<C> conditionValues) {
    @Cleanup EntityManager em = getEntityManager();
    CriteriaQuery<T> cr = getCriteriaQuery(em, clazz);
    Root<T> root = cr.from(clazz);
    cr.select(root);
    List<T> resultList;
    if (conditionFieldName.isEmpty()) {
      resultList = em.createQuery(cr).getResultList();
    } else {
      resultList =
          em.createQuery(cr.where(root.get(conditionFieldName).in(conditionValues)))
              .getResultList();
    }
    return resultList;
  }

  public static <T, C> List<T> getConditioned(
      Class<T> clazz, String conditionFieldName, C conditionValue) {
    return getConditioned(clazz, conditionFieldName, Lists.newArrayList(conditionValue));
  }

  public static <T> List<T> getAll(Class<T> clazz) {
    List<Object> emptyCondition = new ArrayList<>();
    return getConditioned(clazz, "", emptyCondition);
  }
}
