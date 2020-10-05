package kernbeisser.DBConnection;

import java.util.HashMap;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import kernbeisser.Config.ConfigManager;
import kernbeisser.Enums.Setting;
import kernbeisser.Main;
import kernbeisser.StartUp.LogIn.DBLogInController;
import kernbeisser.Useful.Tools;
import kernbeisser.Windows.Window;
import kernbeisser.Windows.WindowImpl.JFrameWindow;
import org.hibernate.service.spi.ServiceException;

public class DBConnection {

  private static EntityManagerFactory entityManagerFactory = null;

  public static boolean tryLogIn(String url, String username, String password) {
    Main.logger.info("Try to Login in with Username: \"" + username + "\" Password: ***********");
    HashMap<String, String> properties = new HashMap<>(3);
    properties.put("javax.persistence.jdbc.user", username);
    properties.put("javax.persistence.jdbc.url", url);
    properties.put("javax.persistence.jdbc.password", password);
    try {
      entityManagerFactory = Persistence.createEntityManagerFactory("Kernbeisser", properties);
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
    String[] conf = ConfigManager.getDBAccessData();
    if (!tryLogIn(conf[0], conf[1], conf[2])) {
      synchronized (DB_LOGIN_LOCK) {
        new DBLogInController()
            .openAsWindow(Window.NEW_VIEW_CONTAINER, JFrameWindow::new)
            .addCloseEventListener(e -> DB_LOGIN_LOCK.notify());
      }
      synchronized (DB_LOGIN_LOCK) {
        try {
          DB_LOGIN_LOCK.wait();
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
    }
  }

  public static void reload() {
    Main.logger.info("reconnecting to DB");
    entityManagerFactory.close();
    logInWithConfig();
  }

  public static EntityManager getEntityManager() {
    if (entityManagerFactory == null) {
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
