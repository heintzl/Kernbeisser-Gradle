package kernbeisser.DBEntitys;

import kernbeisser.DBConnection.DBConnection;
import kernbeisser.Useful.Tools;

import javax.persistence.*;
import java.util.Collection;
import java.util.List;

@Table
@Entity
public class UserGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int gid;

    @Column
    private int value;

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public int getId() {
        return gid;
    }

    public Collection<User> getMembers(){
        EntityManager em = DBConnection.getEntityManager();
        Collection<User> out = em.createQuery("select u from User u where userGroup.id = "+gid,User.class).getResultList();
        em.close();
        return out;
    }

    public static List<UserGroup> getAll(String condition){
        return Tools.getAll(UserGroup.class,condition);
    }

}