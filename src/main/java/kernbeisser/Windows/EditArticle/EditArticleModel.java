package kernbeisser.Windows.EditArticle;

import java.util.Collection;
import java.util.Comparator;
import java.util.Optional;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import kernbeisser.DBConnection.DBConnection;
import kernbeisser.DBEntities.Article;
import kernbeisser.DBEntities.PriceList;
import kernbeisser.DBEntities.Supplier;
import kernbeisser.DBEntities.SurchargeGroup;
import kernbeisser.Enums.MetricUnits;
import kernbeisser.Enums.Mode;
import kernbeisser.Enums.VAT;
import kernbeisser.Useful.Tools;
import kernbeisser.Windows.MVC.IModel;
import lombok.Cleanup;

public class EditArticleModel implements IModel<EditArticleController> {
  private final Mode mode;
  private final Article article;

  EditArticleModel(Article article, Mode mode) {
    this.mode = mode;
    this.article = article;
  }

  Article getSource() {
    return article;
  }

  int kbNumberExists(int kbNumber) {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    try {
      return em.createQuery("select id from Article where kbNumber = " + kbNumber, Integer.class)
          .getSingleResult();
    } catch (NoResultException e) {
      return -1;
    } finally {
      em.close();
    }
  }

  int barcodeExists(long barcode) {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    try {
      return em.createQuery("select id from Article where barcode = " + barcode, Integer.class)
          .getSingleResult();
    } catch (NoResultException e) {
      return -1;
    } finally {
      em.close();
    }
  }

  MetricUnits[] getAllUnits() {
    return MetricUnits.values();
  }

  VAT[] getAllVATs() {
    return VAT.values();
  }

  Collection<Supplier> getAllSuppliers() {
    return Supplier.getAll(null);
  }

  Collection<PriceList> getAllPriceLists() {
    return PriceList.getAll(null);
  }

  public Mode getMode() {
    return mode;
  }

  public int nextUnusedArticleNumber(int kbNumber) {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    int out =
        em.createQuery(
                    "select i.kbNumber from Article i where i.kbNumber >= :last and Not exists (select k from Article k where kbNumber = i.kbNumber+1)",
                    Integer.class)
                .setMaxResults(1)
                .setParameter("last", kbNumber)
                .getSingleResult()
            + 1;
    em.close();
    return out;
  }

  public static boolean nameExists(String name) {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    try {
      em.createQuery("select i from Article i where i.name like :name")
          .setMaxResults(1)
          .setParameter("name", name)
          .getSingleResult();
      em.close();
      return true;
    } catch (NoResultException e) {
      em.close();
      return false;
    }
  }

  public Collection<SurchargeGroup> getAllSurchargeGroupsFor(Supplier s) {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    return em.createQuery(
            "select s from SurchargeGroup s where supplier = :sid", SurchargeGroup.class)
        .setParameter("sid", s)
        .getResultList();
  }

  public Collection<SurchargeGroup> getAllSurchargeGroups() {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    return em.createQuery("select s from SurchargeGroup s", SurchargeGroup.class).getResultList();
  }

  public Optional<Article> findNearestArticle(Article a) {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    return em.createQuery(
            "select a from Article a where supplier = :s and id != :id", Article.class)
        .setParameter("id", a.getId())
        .setParameter("s", a.getSupplier())
        .getResultStream()
        .min(Comparator.comparingInt(e -> Tools.calculate(a.getName(), e.getName())));
  }
}
