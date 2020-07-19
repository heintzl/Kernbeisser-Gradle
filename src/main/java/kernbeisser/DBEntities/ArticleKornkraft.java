package kernbeisser.DBEntities;

import kernbeisser.DBConnection.DBConnection;
import kernbeisser.Enums.PermissionKey;
import kernbeisser.Security.Key;
import kernbeisser.Useful.Tools;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "catalog")
public class ArticleKornkraft extends ArticleBase implements Serializable {

    @Column
    @Getter(onMethod_ = {@Key(PermissionKey.ARTICLE_KORNKRAFT_SYNCHRONISED_READ)})
    @Setter(onMethod_ = {@Key(PermissionKey.ARTICLE_KORNKRAFT_SYNCHRONISED_WRITE)})
    private boolean synchronised = false;

    public static List<ArticleKornkraft> getAll(String condition) {
        return Tools.getAll(ArticleKornkraft.class, condition);
    }

    public static ArticleKornkraft getByKkNumber(int kkNumber) {
        EntityManager em = DBConnection.getEntityManager();
        try {
            return em.createQuery("select k from ArticleKornkraft k where k.base.suppliersItemNumber = :n",
                                  ArticleKornkraft.class)
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
                     .setParameter("supplier", Supplier.getKKSupplier().getSid())
                     .setParameter("number", getSuppliersItemNumber())
                     .setMaxResults(1)
                     .getSingleResult();
        } catch (NoResultException e) {
            return SurchargeTable.DEFAULT;
        }
    }

    @Override
    public String toString() {
        return Tools.decide(this::getName, "ArtikelBase[" + super.toString() + "]");
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        ArticleKornkraft that = (ArticleKornkraft) o;
        return synchronised == that.synchronised;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), synchronised);
    }
}
