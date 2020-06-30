package kernbeisser.DBEntities;

import kernbeisser.DBConnection.DBConnection;
import kernbeisser.Enums.PermissionKey;
import kernbeisser.Useful.Tools;

import javax.persistence.*;
import java.util.Collection;
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
    @Enumerated(EnumType.STRING)
    @ElementCollection(fetch = FetchType.EAGER)
    private Set<PermissionKey> keySet = new HashSet<>();

    public static List<Permission> getAll(String condition) {
        return Tools.getAll(Permission.class, condition);
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<PermissionKey> getKeySet() {
        return keySet;
    }

    public void setKeySet(Set<PermissionKey> keySet) {
        this.keySet = keySet;
    }

    public boolean contains(PermissionKey key) {
        return keySet.contains(key);
    }

    @Override
    public String toString() {
        return name;
    }

    public static Collection<Permission> defaultSearch(String s, int max) {
        EntityManager em = DBConnection.getEntityManager();
        Collection<Permission> out = em.createQuery(
                "select p from Permission p where p.name like :s",
                Permission.class
        )
                                .setParameter("s", s + "%")
                                .setMaxResults(max)
                                .getResultList();
        em.close();
        return out;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Permission)) {
            return false;
        }

        Permission that = (Permission) o;

        if (getId() != that.getId()) {
            return false;
        }
        if (getName() != null ? !getName().equals(that.getName()) : that.getName() != null) {
            return false;
        }
        return getKeySet() != null ? getKeySet().equals(that.getKeySet()) : that.getKeySet() == null;
    }

    @Override
    public int hashCode() {
        int result = getId();
        result = 31 * result + (getName() != null ? getName().hashCode() : 0);
        result = 31 * result + (getKeySet() != null ? getKeySet().hashCode() : 0);
        return result;
    }
}
