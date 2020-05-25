package kernbeisser.DBConnection;


import kernbeisser.Config.ConfigManager;
import kernbeisser.Enums.Setting;
import kernbeisser.Main;
import kernbeisser.StartUp.LogIn.DBLogInController;
import kernbeisser.Useful.Tools;
import kernbeisser.Windows.Window;
import kernbeisser.Windows.WindowImpl.JFrameWindow;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.util.HashMap;

public class DBConnection {

    private static EntityManagerFactory entityManagerFactory = null;

    public static boolean tryLogIn(String url, String username, String password) {
        Main.logger.info("Try to Login in with Username: \""+username+"\" Password: ***********");
        HashMap<String,String> properties = new HashMap<>(3);
        properties.put("javax.persistence.jdbc.user", username);
        properties.put("javax.persistence.jdbc.url", url);
        properties.put("javax.persistence.jdbc.password", password);
        try {
            entityManagerFactory = Persistence.createEntityManagerFactory("Kernbeisser", properties);
            Main.logger.info("Login successful");
            return true;
        } catch (Exception e) {
            Main.logger.warn("Log in failed");
            return false;
        }
    }

    public static void logInWithConfig() {
        Object lock = new Object();
        synchronized (lock) {
            String[] conf = ConfigManager.getDBAccessData();
            if (!tryLogIn(conf[0], conf[1], conf[2])) {
                new DBLogInController().openAsWindow(Window.NEW_VIEW_CONTAINER, JFrameWindow::new).addCloseEventListener(e -> lock.notify());
                try {
                    lock.wait();
                } catch (InterruptedException e) {
                    Tools.showUnexpectedErrorWarning(e);
                }
            }

        }
    }

    public static void reload(){
        Main.logger.info("reconnecting to DB");
        entityManagerFactory.close();
        logInWithConfig();
    }

    public static EntityManager getEntityManager() {
        if(entityManagerFactory==null)logInWithConfig();
        return entityManagerFactory.createEntityManager();
    }

    public static void updateDatabase(){
        Main.logger.info("updating Database");
        EntityManager em = getEntityManager();
        EntityTransaction et = em.getTransaction();
        et.begin();
        em.createNativeQuery("SET FOREIGN_KEY_CHECKS = 0").executeUpdate();
        for (Object o : em.createNativeQuery("select TABLE_NAME from information_schema.TABLES where TABLE_SCHEMA = 'kernbeisser'").getResultList()) {
            Main.logger.info("dropping DB Table "+o);
            if(o.equals("settingvalue"))continue;
            em.createNativeQuery("drop table "+o).executeUpdate();
        }
        em.createNativeQuery("SET FOREIGN_KEY_CHECKS = 1").executeUpdate();
        em.flush();
        et.commit();
        em.close();
        reload();
        Setting.DB_VERSION.setValue(Setting.DB_VERSION.getDefaultValue());
        Setting.DB_INITIALIZED.setValue(false);
        Setting.INFO_LINE_LAST_CATALOG.setValue(Setting.INFO_LINE_LAST_CATALOG.getDefaultValue());
        Main.logger.info("DB update complete");
    }
}

