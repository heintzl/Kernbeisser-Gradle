package kernbeisser.Forms.FormImplemetations.Article;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import java.util.Collection;
import java.util.Comparator;
import java.util.Optional;
import kernbeisser.DBConnection.DBConnection;
import kernbeisser.DBEntities.Article;
import kernbeisser.DBEntities.PriceList;
import kernbeisser.DBEntities.Supplier;
import kernbeisser.DBEntities.SurchargeGroup;
import kernbeisser.Enums.MetricUnits;
import kernbeisser.Enums.VAT;
import kernbeisser.Useful.Tools;
import kernbeisser.Windows.MVC.IModel;
import lombok.Cleanup;

public class ArticleModel implements IModel<ArticleController> {

  boolean kbNumberExists(int kbNumber) {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    @Cleanup(value = "commit")
    EntityTransaction et = em.getTransaction();
    et.begin();
    return em.createQuery("select id from Article where kbNumber = :kbn", Integer.class)
        .setParameter("kbn", kbNumber)
        .getResultStream()
        .findAny()
        .isPresent();
  }

  boolean barcodeExists(long barcode) {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    @Cleanup(value = "commit")
    EntityTransaction et = em.getTransaction();
    et.begin();
    return em.createQuery("select id from Article where barcode = :b", Integer.class)
        .setParameter("b", barcode)
        .getResultStream()
        .findAny()
        .isPresent();
  }

  MetricUnits[] getAllUnits() {
    return MetricUnits.values();
  }

  VAT[] getAllVATs() {
    return VAT.values();
  }

  Collection<Supplier> getAllSuppliers() {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    @Cleanup(value = "commit")
    EntityTransaction et = em.getTransaction();
    et.begin();
    return em.createQuery("select s from Supplier s order by s.name asc", Supplier.class)
        .getResultList();
  }

  Collection<PriceList> getAllPriceLists() {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    @Cleanup(value = "commit")
    EntityTransaction et = em.getTransaction();
    et.begin();
    return em.createQuery("select s from PriceList s order by s.name asc", PriceList.class)
        .getResultList();
  }

  public int nextUnusedArticleNumber(int kbNumber) {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    @Cleanup(value = "commit")
    EntityTransaction et = em.getTransaction();
    et.begin();
    return em.createQuery(
                "select i.kbNumber from Article i where i.kbNumber >= :last and Not exists (select k from Article k where kbNumber = i.kbNumber+1)",
                Integer.class)
            .setMaxResults(1)
            .setParameter("last", kbNumber)
            .getSingleResult()
        + 1;
  }

  public static boolean nameExists(String name) {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    @Cleanup(value = "commit")
    EntityTransaction et = em.getTransaction();
    et.begin();
    return em.createQuery("select i from Article i where i.name like :name")
        .setMaxResults(1)
        .setParameter("name", name)
        .getResultStream()
        .findAny()
        .isPresent();
  }

  public Collection<SurchargeGroup> getAllSurchargeGroupsFor(Supplier s) {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    @Cleanup(value = "commit")
    EntityTransaction et = em.getTransaction();
    et.begin();
    return em.createQuery(
            "select s from SurchargeGroup s where supplier = :sid order by s.name asc",
            SurchargeGroup.class)
        .setParameter("sid", s)
        .getResultList();
  }

  public Collection<SurchargeGroup> getAllSurchargeGroups() {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    @Cleanup(value = "commit")
    EntityTransaction et = em.getTransaction();
    et.begin();
    return em.createQuery("select s from SurchargeGroup s", SurchargeGroup.class).getResultList();
  }

  public Optional<Article> findNearestArticle(Article a) {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    @Cleanup(value = "commit")
    EntityTransaction et = em.getTransaction();
    et.begin();
    return em.createQuery(
            "select a from Article a where supplier = :s and id != :id", Article.class)
        .setParameter("id", a.getId())
        .setParameter("s", a.getSupplier())
        .getResultStream()
        .min(Comparator.comparingInt(e -> Tools.calculate(a.getName(), e.getName())));
  }

  public boolean suppliersItemNumberExists(Supplier supplier, int suppliersItemNumber) {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    @Cleanup(value = "commit")
    EntityTransaction et = em.getTransaction();
    et.begin();
    return em.createQuery(
            "select a from Article a where a.supplier = :s and a.suppliersItemNumber = :sn",
            Article.class)
        .setParameter("s", supplier)
        .setParameter("sn", suppliersItemNumber)
        .getResultStream()
        .findAny()
        .isPresent();
  }
}
