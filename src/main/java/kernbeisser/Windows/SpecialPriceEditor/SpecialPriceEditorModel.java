package kernbeisser.Windows.SpecialPriceEditor;

import java.time.Instant;
import java.util.Collection;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import kernbeisser.DBConnection.DBConnection;
import kernbeisser.DBEntities.ArticleBase;
import kernbeisser.DBEntities.Offer;
import kernbeisser.Useful.Tools;
import kernbeisser.Windows.MVC.IModel;
import lombok.Cleanup;

public class SpecialPriceEditorModel implements IModel<SpecialPriceEditorController> {

  void edit(int offerId, Offer offer) {
    Tools.edit(offerId, offer);
  }

  void remove(Offer offer) {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    EntityTransaction et = em.getTransaction();
    et.begin();
    em.remove(offer);
    em.flush();
    et.commit();
    em.close();
  }

  public void addOffer(ArticleBase article, Offer offer) {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    EntityTransaction et = em.getTransaction();
    et.begin();
    ArticleBase i =
        em.createQuery("select i from ArticleBase i where id = :id", ArticleBase.class)
            .setParameter("id", article.getId())
            .getSingleResult();
    offer.setArticle(i);
    em.persist(offer);
    em.flush();
    et.commit();
    em.close();
  }

  public Collection<ArticleBase> searchArticle(
      String search, int maxResults, boolean onlyActionArticle) {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    return em.createQuery(
            "select a from ArticleBase a where "
                + "(a.name like :s or a.suppliersItemNumber like '%"
                + search
                + "%')"
                + (onlyActionArticle ? " and a.id in (select article.id from Offer)" : ""),
            ArticleBase.class)
        .setParameter("s", search)
        .getResultList();
  }

  public Collection<Offer> getAllOffersBetween(Instant from, Instant to) {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
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
