package kernbeisser.DBConnection;

import kernbeisser.Config.Config;
import kernbeisser.Config.Config.DBAccess;
import kernbeisser.Enums.Setting;
import kernbeisser.Exeptions.ClassIsSingletonException;
import kernbeisser.Main;
import kernbeisser.StartUp.LogIn.DBLogInController;
import kernbeisser.Useful.Tools;
import kernbeisser.Windows.ViewContainers.JFrameWindow;
import org.hibernate.service.spi.ServiceException;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.swing.*;
import java.util.HashMap;

public class DBConnection {

  private static EntityManagerFactory entityManagerFactory = null;

  private static EntityManagerFactory establishConnection(String name, DBAccess dbAccessData) {
    HashMap<String, String> properties = new HashMap<>(3);
    properties.put("javax.persistence.jdbc.user", dbAccessData.getUsername());
    properties.put(
        "javax.persistence.jdbc.url",
        dbAccessData.getUrl() + "?characterEncoding=" + dbAccessData.getEncoding());
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
    Setting.DB_VERSION.changeValue(Setting.DB_VERSION.getDefaultValue());
    Setting.DB_INITIALIZED.changeValue(false);
    Setting.INFO_LINE_LAST_CATALOG.changeValue(Setting.INFO_LINE_LAST_CATALOG.getDefaultValue());
    Main.logger.info("DB update complete");
  }

  public static boolean isInitialized() {
    return entityManagerFactory != null;
  }
}
