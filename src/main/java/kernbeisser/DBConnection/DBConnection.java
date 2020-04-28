package kernbeisser.DBConnection;


import kernbeisser.Config.ConfigManager;
import kernbeisser.Enums.Setting;
import kernbeisser.StartUp.LogIn.DBLogInController;
import kernbeisser.StartUp.LogIn.DBLogInView;
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
        HashMap<String,String> properties = new HashMap<>(3);
        properties.put("javax.persistence.jdbc.user", username);
        properties.put("javax.persistence.jdbc.url", url);
        properties.put("javax.persistence.jdbc.password", password);
        try {
            entityManagerFactory = Persistence.createEntityManagerFactory("Kernbeisser", properties);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
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
                    e.printStackTrace();
                }
            }

        }
    }

    public static void reload(){
        entityManagerFactory.close();
        logInWithConfig();
    }

    public static EntityManagerFactory getEntityManagerFactory() {
        return entityManagerFactory;
    }

    public static EntityManager getEntityManager() {
        if(entityManagerFactory==null)logInWithConfig();
        return entityManagerFactory.createEntityManager();
    }

    public static void updateDatabase(){
        EntityManager em = getEntityManager();
        EntityTransaction et = em.getTransaction();
        et.begin();
        em.createNativeQuery("SET FOREIGN_KEY_CHECKS = 0").executeUpdate();
        for (Object o : em.createNativeQuery("select TABLE_NAME from information_schema.TABLES where TABLE_SCHEMA = 'kernbeisser'").getResultList()) {
            System.out.println("dropping "+o);
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
    }
}

