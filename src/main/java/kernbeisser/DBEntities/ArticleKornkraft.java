package kernbeisser.DBEntities;

import kernbeisser.DBConnection.DBConnection;
import kernbeisser.Enums.MetricUnits;
import kernbeisser.Enums.PermissionKey;
import kernbeisser.Enums.VAT;
import kernbeisser.Security.Key;
import kernbeisser.Useful.Tools;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "catalog")
public class ArticleKornkraft extends ArticleBase implements Serializable {

    @Column
    @Getter(onMethod_= {@Key(PermissionKey.ARTICLE_KORNKRAFT_SYNCHRONISED_READ)})
    @Setter(onMethod_= {@Key(PermissionKey.ARTICLE_KORNKRAFT_SYNCHRONISED_WRITE)})
    private boolean synchronised = false;

    public static List<ArticleKornkraft> getAll(String condition) {
        return Tools.getAll(ArticleKornkraft.class, condition);
    }

    public static ArticleKornkraft getByKkNumber(int kkNumber) {
        EntityManager em = DBConnection.getEntityManager();
        try {
            return em.createQuery("select k from ArticleKornkraft k where k.base.suppliersItemNumber = :n", ArticleKornkraft.class)
                     .setParameter("n", kkNumber)
                     .getSingleResult();
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
    }

    public static ArticleKornkraft getByKbNumber(int kbNumber) {
        EntityManager em = DBConnection.getEntityManager();
        try {
            return em.createQuery(
                    "select ik from ArticleKornkraft ik where ik.suppliersItemNumber = (select i.suppliersItemNumber from Article i where i.kbNumber = :n and i.supplier.shortName = 'KK')",
                    ArticleKornkraft.class)
                     .setParameter("n", kbNumber)
                     .setMaxResults(1)
                     .getSingleResult();
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
    }


    public SurchargeTable getSurcharge() {
        //TODO really expensive!
        EntityManager em = DBConnection.getEntityManager();
        try {
            return em.createQuery(
                    "select st from SurchargeTable st where st.supplier.id = :supplier and st.from <= :number and st.to >= :number",
                    SurchargeTable.class)
                     .setParameter("supplier", Supplier.getKKSupplier().getId())
                     .setParameter("number", getSuppliersItemNumber())
                     .setMaxResults(1)
                     .getSingleResult();
        } catch (NoResultException e) {
            return SurchargeTable.DEFAULT;
        }
    }
}
