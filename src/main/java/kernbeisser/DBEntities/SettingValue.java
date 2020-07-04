package kernbeisser.DBEntities;

import kernbeisser.DBConnection.DBConnection;
import kernbeisser.Enums.Setting;
import kernbeisser.Main;
import kernbeisser.Useful.Tools;

import javax.persistence.*;
import java.util.HashMap;
import java.util.List;

@Entity
@Table
public class SettingValue {

    private static HashMap<Setting,String> settingValueHashMap;

    @Id
    @GeneratedValue
    private int id;

    @Column
    @Enumerated(value = EnumType.STRING)
    private Setting setting;

    @Column
    private String value;

    public Setting getSetting() {
        return setting;
    }

    private void setSetting(Setting setting) {
        this.setting = setting;
    }

    public String getValue() {
        return value;
    }

    private void setValue(String value) {
        this.value = value;

    }

    public int getId() {
        return id;
    }

    public static String getValue(Setting s){
        if(settingValueHashMap==null){
            settingValueHashMap = new HashMap<>();
            getAll(null).forEach(e  -> settingValueHashMap.put(e.setting,e.value));
            logSettings();
        }
        String out = settingValueHashMap.get(s);
        if(out == null){
            out = loadOrCreateSettingValue(s);
            settingValueHashMap.put(s,out);
        }
        return out;
    }

    private static void logSettings(){
        StringBuilder sb = new StringBuilder("Setting Values:");
        settingValueHashMap.forEach((a,b) -> sb.append("\n").append(a).append(" = '").append(b).append("'"));
        Main.logger.info(sb);
    }


    public static String loadOrCreateSettingValue(Setting setting){
        EntityManager em = DBConnection.getEntityManager();
        try{
            return em.createQuery("select s from SettingValue s where setting = :sn",SettingValue.class).setParameter("sn",setting).getSingleResult().value;
        }catch (NoResultException e){
            EntityTransaction et = em.getTransaction();
            et.begin();
            SettingValue value = new SettingValue();
            value.value = setting.getDefaultValue();
            value.setSetting(setting);
            em.persist(value);
            em.flush();
            et.commit();
            return loadOrCreateSettingValue(setting);
        }
        finally {
            em.close();
        }
    }

    public static void setValue(Setting setting,String value){
        EntityManager em = DBConnection.getEntityManager();
        EntityTransaction et = em.getTransaction();
        et.begin();
        try {
            SettingValue settingValue = em.createQuery("select s from SettingValue s where s.setting = :setting",SettingValue.class).setParameter("setting",setting).getSingleResult();
            settingValue.setValue(value);
            em.persist(settingValue);
        }catch (NoResultException ignored){
            em.close();
            return;
        }
        et.commit();
        em.close();
        settingValueHashMap.replace(setting,value);
    }

    public static List<SettingValue> getAll(String condition){
        return Tools.getAll(SettingValue.class, condition);
    }

}
