package kernbeisser.DBConnection;


import kernbeisser.Config.ConfigManager;
import kernbeisser.StartUp.LogIn.DBLogIn;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.HashMap;

public class DBConnection {

    private static EntityManagerFactory entityManagerFactory;

    static {
        logInWithConfig();
    }

    public static boolean tryLogIn(String url,String username,String password){
        HashMap<String,String> properties = new HashMap<>(3);
        properties.put("javax.persistence.jdbc.user",username);
        properties.put("javax.persistence.jdbc.url",url);
        properties.put("javax.persistence.jdbc.password",password);
        try{
            entityManagerFactory = Persistence.createEntityManagerFactory("Kernbeisser",properties);
            return true;
        }catch (Exception e){
            return false;
        }
    }

    public static void logInWithConfig(){
        String[] conf = ConfigManager.getDBAccessData();
        if(!tryLogIn(conf[0],conf[1],conf[2])){
            new DBLogIn(null);
        }
    }

    public static EntityManagerFactory getEntityManagerFactory() {
        return entityManagerFactory;
    }

    public static EntityManager getEntityManager() {
        while (entityManagerFactory==null){
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return entityManagerFactory.createEntityManager();
    }

}

