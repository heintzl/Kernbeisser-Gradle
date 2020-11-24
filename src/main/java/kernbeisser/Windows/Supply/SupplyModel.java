package kernbeisser.Windows.Supply;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import kernbeisser.DBConnection.DBConnection;
import kernbeisser.DBEntities.Article;
import kernbeisser.DBEntities.ArticleBase;
import kernbeisser.DBEntities.PriceList;
import kernbeisser.DBEntities.ShoppingItem;
import kernbeisser.DBEntities.Supplier;
import kernbeisser.Useful.Tools;
import kernbeisser.Windows.MVC.IModel;
import lombok.Cleanup;
import lombok.Getter;

public class SupplyModel implements IModel<SupplyController> {

  private final Set<ArticleBase> print = new HashSet<>();
  @Getter private final Collection<ShoppingItem> shoppingItems = new ArrayList<>();

  public Article findNextTo(ArticleBase articleKornkraft) {
    return Article.nextArticleTo(
        articleKornkraft.getSuppliersItemNumber(), Supplier.getKKSupplier());
  }

  public Article persistArticleBase(
      ArticleBase ab, boolean weighable, PriceList priceList, int kbNumber) {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    EntityTransaction et = em.getTransaction();
    et.begin();
    Article article = Article.fromArticleBase(ab, weighable, priceList, kbNumber);
    em.persist(article);
    em.flush();
    et.commit();
    return article;
  }

  public int getNextUnusedKBNumber(int kbNumber) {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    return em.createQuery(
                "select a.kbNumber from Article a where a.kbNumber >= :kb and not exists (select u from Article u where u.kbNumber = a.kbNumber+1)",
                Integer.class)
            .setParameter("kb", kbNumber)
            .setMaxResults(1)
            .getSingleResult()
        + 1;
  }

  public Article getBySuppliersItemNumber(Supplier supplier, int suppliersItemNumber) {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    return getArticleViaSuppliersItemNumber(supplier, suppliersItemNumber, em).getSingleResult();
  }

  void commit() {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    EntityTransaction et = em.getTransaction();
    et.begin();
    for (ShoppingItem item : shoppingItems) {
      item.setItemMultiplier(-item.getItemMultiplier());
      em.persist(item);
    }
    em.flush();
    et.commit();
  }

  Collection<Supplier> getAllSuppliers() {
    return Tools.getAll(Supplier.class, null);
  }

  private TypedQuery<Article> getArticleViaSuppliersItemNumber(
      Supplier supplier, int suppliersItemNumber, EntityManager entityManager)
      throws NoResultException {
    return entityManager
        .createQuery(
            "select a from Article a where supplier.id = :sid and suppliersItemNumber = :sin",
            Article.class)
        .setParameter("sid", supplier.getId())
        .setParameter("sin", suppliersItemNumber);
  }

  private TypedQuery<ArticleBase> getArticleBaseViaSuppliersItemNumber(
      Supplier supplier, int suppliersItemNumber, EntityManager entityManager)
      throws NoResultException {
    return entityManager
        .createQuery(
            "select a from ArticleBase a where supplier.id = :sid and suppliersItemNumber = :sin",
            ArticleBase.class)
        .setParameter("sid", supplier.getId())
        .setParameter("sin", suppliersItemNumber);
  }

  public boolean isArticle(ArticleBase a) {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    try {
      a.getKernbeisserArticle(em);
      return true;
    } catch (NoResultException e) {
      return false;
    }
  }

  public Collection<ArticleBase> findBySuppliersItemNumber(
      Supplier supplier, int suppliersItemNumber) {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    return getArticleBaseViaSuppliersItemNumber(supplier, suppliersItemNumber, em).getResultList();
  }

  public Collection<PriceList> getAllPriceLists() {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    return em.createQuery("select p from PriceList p", PriceList.class).getResultList();
  }

  void print() {}

  public void togglePrint(ArticleBase bases) {
    if (!print.remove(bases)) print.add(bases);
  }

  public boolean becomePrinted(Article article) {
    return print.contains(article);
  }
}
