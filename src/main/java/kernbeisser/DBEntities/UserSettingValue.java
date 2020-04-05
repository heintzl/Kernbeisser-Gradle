package kernbeisser.DBEntities;

import kernbeisser.DBConnection.DBConnection;
import kernbeisser.Enums.Setting;
import kernbeisser.Enums.UserSetting;
import kernbeisser.Useful.Tools;

import javax.persistence.*;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

@Entity
@Table
public class UserSettingValue {
    private static User loaded;
    private static HashMap<UserSetting,String> values;

    @Id
    private int id;

    @JoinColumn
    @ManyToOne
    private User user;

    @Column
    @Enumerated(EnumType.STRING)
    private UserSetting userSetting;

    @Column
    private String value;

    public static User getLoaded() {
        return loaded;
    }

    private static void setLoaded(User loaded) {
        UserSettingValue.loaded = loaded;
    }

    public int getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    private void setUser(User user) {
        this.user = user;
    }

    public UserSetting getUserSetting() {
        return userSetting;
    }

    private void setUserSetting(UserSetting userSetting) {
        this.userSetting = userSetting;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public static List<UserSettingValue> getAll(String condition){
        return Tools.getAll(UserSettingValue.class, condition);
    }

    private static String loadOrCreateSettingValue(UserSetting setting,User user){
        EntityManager em = DBConnection.getEntityManager();
        try{
            return em.createQuery("select s from UserSettingValue s where userSetting = :sn and user.id = :uid",UserSettingValue.class)
                     .setParameter("sn",setting)
                     .setParameter("uid",user.getId())
                     .getSingleResult().value;
        }catch (NoResultException e){
            EntityTransaction et = em.getTransaction();
            et.begin();
            UserSettingValue value = new UserSettingValue();
            value.value = setting.getDefaultValue();
            value.setUser(user);
            value.setUserSetting(setting);
            em.persist(value);
            em.flush();
            et.commit();
            return loadOrCreateSettingValue(setting,user);
        }
        finally {
            em.close();
        }
    }

    private static Collection<UserSettingValue> getAllForUser(User user){
        EntityManager em = DBConnection.getEntityManager();
        List<UserSettingValue> out = em.createQuery("select u from UserSettingValue u where u.id = :uid",UserSettingValue.class)
          .setParameter("uid",user.getId()).getResultList();
        em.close();
        return out;
    }

    public static String getValueFor(User user,UserSetting setting){
        if(loaded==null || loaded.getId() != user.getId()){
            loadUser(user);
        }
        String out = values.get(setting);
        if(out == null){
            out = loadOrCreateSettingValue(setting,user);
            values.put(setting,out);
        }
        return out;
    }

    private static void loadUser(User user){
        loaded = user;
        values = new HashMap<>(UserSetting.values().length);
        getAllForUser(user).forEach(e -> values.put(e.userSetting,e.value));
    }
}
