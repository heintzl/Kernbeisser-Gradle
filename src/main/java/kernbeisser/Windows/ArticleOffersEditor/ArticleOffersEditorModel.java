package kernbeisser.Windows.ArticleOffersEditor;

import java.time.Instant;
import java.util.Collection;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import kernbeisser.DBConnection.DBConnection;
import kernbeisser.DBEntities.Article;
import kernbeisser.DBEntities.Offer;
import kernbeisser.Windows.MVC.IModel;
import lombok.Cleanup;

public class ArticleOffersEditorModel implements IModel<ArticleOffersEditorController> {

  void remove(Offer offer) {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    @Cleanup(value = "commit")
    EntityTransaction et = em.getTransaction();
    et.begin();
    em.remove(em.find(Offer.class, offer.getId()));
    em.flush();
  }

  public Collection<Article> searchArticle(
      String search, int maxResults, boolean onlyActionArticle) {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    @Cleanup(value = "commit")
    EntityTransaction et = em.getTransaction();
    et.begin();
    return em.createQuery(
            "select a from Article a where "
                + "(a.name like :s or cast(a.suppliersItemNumber as string) like :s)"
                + (onlyActionArticle ? " and a.id in (select article.id from Offer)" : ""),
            Article.class)
        .setParameter("s", "%" + search + "%")
        .getResultList();
  }

  public Collection<Offer> getAllOffersBetween(Instant from, Instant to) {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    @Cleanup(value = "commit")
    EntityTransaction et = em.getTransaction();
    et.begin();
    CriteriaBuilder cb = em.getCriteriaBuilder();
    CriteriaQuery<Offer> criteriaQuery = cb.createQuery(Offer.class);
    Root<Offer> root = criteriaQuery.from(Offer.class);
    criteriaQuery
        .select(root)
        .where(
            cb.and(
                cb.lessThan(root.get("fromDate"), to), cb.greaterThan(root.get("toDate"), from)));
    return em.createQuery(criteriaQuery).getResultList();
  }
}
