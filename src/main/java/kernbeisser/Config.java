package kernbeisser;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashMap;

@Table
@Entity
public class Config implements Serializable {
    @Id
    @GeneratedValue
    private int cid;
    @Column(unique = true)
    private String name;
    @Column
    private String value;

    private static HashMap<String,String> configs = new HashMap<>();

    public static void loadConfigs(){
        EntityManager em = DBConnection.getEntityManager();
        for (Config config : em.createQuery("select c from Config c", Config.class).getResultList()) {
            configs.put(config.name,config.value);
        }
        em.close();
    }
    public static String getConfig(String name){
        return configs.get(name);
    }
    public static void setConfig(String name,String value){
        Config config = new Config();
        EntityManager em = DBConnection.getEntityManager();
        EntityTransaction et = em.getTransaction();
        try{
            config = em.createQuery("select c from Config c where name like '"+name+"'",Config.class).getSingleResult();
        }catch (NoResultException e){
            config.name=name;
            config.value=value;
            et.begin();
            em.persist(config);
            et.commit();
            em.close();
            configs.put(name,value);
            return;
        }
        et.begin();
        config.value=value;
        em.persist(config);
        em.flush();
        et.commit();
        em.close();
        configs.replace(name,value);
    }
    public static void remove(String configName){
        EntityManager em = DBConnection.getEntityManager();
        EntityTransaction et = em.getTransaction();
        try{
            Config config = em.createQuery("select c from Config c where name like '"+configName+"'",Config.class).getSingleResult();
            et.begin();
            em.persist(config);
            em.flush();
            et.commit();
            em.close();
        }catch (NoResultException e){
            et.rollback();
            em.close();
        }
    }
}
