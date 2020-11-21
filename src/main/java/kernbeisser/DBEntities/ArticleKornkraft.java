package kernbeisser.DBEntities;

import java.io.Serializable;
import java.util.List;
import javax.persistence.*;
import kernbeisser.DBConnection.DBConnection;
import kernbeisser.Enums.PermissionKey;
import kernbeisser.Security.Key;
import kernbeisser.Useful.Tools;
import lombok.Cleanup;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "catalog")
@EqualsAndHashCode(doNotUseGetters = true, callSuper = true)
public class ArticleKornkraft extends ArticleBase implements Serializable {

  @Column
  @Getter(onMethod_ = {@Key(PermissionKey.ARTICLE_KORNKRAFT_SYNCHRONISED_READ)})
  @Setter(onMethod_ = {@Key(PermissionKey.ARTICLE_KORNKRAFT_SYNCHRONISED_WRITE)})
  private boolean synchronised = false;

  public static List<ArticleKornkraft> getAll(String condition) {
    return Tools.getAll(ArticleKornkraft.class, condition);
  }

  public static ArticleKornkraft getByKkNumber(int kkNumber) {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    return em.createQuery(
            "select k from ArticleKornkraft k where k.suppliersItemNumber = :n and k.supplier = :kbs",
            ArticleKornkraft.class)
        .setParameter("kbs", Supplier.getKKSupplier())
        .setParameter("n", kkNumber)
        .getSingleResult();
  }

  public static ArticleKornkraft getByKbNumber(int kbNumber) {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    return em.createQuery(
            "select ik from ArticleKornkraft ik where ik.supplier = :kbs and id in (select kb.id from Article kb where kbNumber = :n)",
            ArticleKornkraft.class)
        .setParameter("kbs", Supplier.getKKSupplier())
        .setParameter("n", kbNumber)
        .getSingleResult();
  }

  @Override
  public String toString() {
    return Tools.decide(this::getName, "ArtikelBase[" + super.toString() + "]");
  }
}
