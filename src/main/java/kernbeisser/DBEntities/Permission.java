package kernbeisser.DBEntities;

import kernbeisser.Enums.Key;
import kernbeisser.Useful.Tools;

import javax.persistence.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table
public class Permission {
    @Id
    @GeneratedValue
    private int id;

    @Column
    private String name;

    @JoinColumn
    @ElementCollection(fetch = FetchType.EAGER)
    private Set<Key> keySet = new HashSet<>();

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<Key> getKeySet() {
        return keySet;
    }

    public void setKeySet(Set<Key> keySet) {
        this.keySet = keySet;
    }

    public boolean contains(Key key){
        return keySet.contains(key);
    }

    @Override
    public String toString() {
        return name;
    }

    public static List<Permission> getAll(String condition){
        return Tools.getAll(Permission.class,condition);
    }
}
