package kernbeisser.Windows.SpecialPriceEditor;

import java.util.Collection;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import kernbeisser.DBConnection.DBConnection;
import kernbeisser.DBEntities.Article;
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

  public void addOffer(Article article, Offer offer) {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    EntityTransaction et = em.getTransaction();
    et.begin();
    Article i =
        em.createQuery("select i from Article i where id = :id", Article.class)
            .setParameter("id", article.getId())
            .getSingleResult();
    offer.setArticle(i);
    em.persist(offer);

    em.flush();
    et.commit();
    em.close();
  }

  public Collection<Article> searchArticle(
      String search, int maxResults, boolean onlyActionArticle) {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    return em.createQuery(
            "select a from Article a where (a.kbNumber like '%"
                + search
                + "%' or a.name like :s or a.suppliersItemNumber like '%"
                + search
                + "%')"
                + (onlyActionArticle ? " and a.id in (select article.id from Offer)" : ""),
            Article.class)
        .setParameter("s", search)
        .setMaxResults(maxResults)
        .getResultList();
  }
}
