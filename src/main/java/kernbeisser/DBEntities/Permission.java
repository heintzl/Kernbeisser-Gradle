package kernbeisser.DBEntities;

import kernbeisser.DBConnection.DBConnection;
import kernbeisser.Enums.PermissionKey;
import kernbeisser.Security.Key;
import kernbeisser.Useful.Tools;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table
@EqualsAndHashCode
public class Permission {
    @Id
    @GeneratedValue
    @Getter(onMethod_= {@Key(PermissionKey.PERMISSION_ID_READ)})
    @Setter(onMethod_= {@Key(PermissionKey.PERMISSION_ID_WRITE)})
    private int id;

    @Column
    @Getter(onMethod_= {@Key(PermissionKey.PERMISSION_NAME_READ)})
    @Setter(onMethod_= {@Key(PermissionKey.PERMISSION_NAME_WRITE)})
    private String name;

    @JoinColumn
    @Enumerated(EnumType.STRING)
    @ElementCollection(fetch = FetchType.EAGER)
    @Getter(onMethod_= {@Key(PermissionKey.PERMISSION_NAME_READ)})
    @Setter(onMethod_= {@Key(PermissionKey.PERMISSION_NAME_WRITE)})
    private Set<PermissionKey> keySet = new HashSet<>();

    public static List<Permission> getAll(String condition) {
        return Tools.getAll(Permission.class, condition);
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
}
