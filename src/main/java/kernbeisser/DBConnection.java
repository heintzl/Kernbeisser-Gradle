package kernbeisser;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class DBConnection {

    private static EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("Kernbeisser",DBConfiguration.getConfig());
    public static EntityManagerFactory getEntityManagerFactory() {
        return entityManagerFactory;
    }
    public static EntityManager getEntityManager(){
        return entityManagerFactory.createEntityManager();
    }
}
