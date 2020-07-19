package kernbeisser.Windows.SpecialPriceEditor;

import kernbeisser.DBConnection.DBConnection;
import kernbeisser.DBEntities.Article;
import kernbeisser.DBEntities.Offer;
import kernbeisser.Useful.Tools;
import kernbeisser.Windows.Model;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import java.util.Collection;

public class SpecialPriceEditorModel implements Model<SpecialPriceEditorController> {
    private Offer selected;
    private Article selectedArticle;

    public void setSelectedOffer(Offer o) {
        this.selected = o;
    }

    Offer getSelectedOffer() {
        return selected;
    }

    void edit(int offerId, Offer offer) {
        Tools.edit(offerId, offer);
    }

    void remove(Article article, Offer offer) {
        EntityManager em = DBConnection.getEntityManager();
        EntityTransaction et = em.getTransaction();
        et.begin();
        Article i = em.find(Article.class, article.getId());
        Offer o = em.find(Offer.class, offer.getOid());
        i.getOffers().remove(o);
        em.remove(o);
        em.persist(i);
        em.flush();
        et.commit();
        em.close();
    }

    public void refreshItem() {
        selectedArticle = DBConnection.getEntityManager().find(Article.class, selectedArticle.getId());
    }

    public Article getSelectedArticle() {
        return selectedArticle;
    }

    public void setSelectedArticle(Article selectedArticle) {
        this.selectedArticle = selectedArticle;
    }

    public void addOffer(Article article, Offer offer) {
        EntityManager em = DBConnection.getEntityManager();
        EntityTransaction et = em.getTransaction();
        et.begin();
        Article i = em.createQuery("select i from Article i where id = :id", Article.class)
                      .setParameter("id", article.getId())
                      .getSingleResult();
        em.persist(offer);
        i.getOffers().add(offer);
        em.flush();
        et.commit();
        em.close();
    }

    public Collection<Article> searchArticle(String search, int maxResults, boolean onlyActionArticle) {
        EntityManager em = DBConnection.getEntityManager();
        Collection<Article> out = em.createQuery(
                "select i from Article i where (i.suppliersItemNumber = :n or kbNumber = :n or i.supplier.shortName like :s or i.supplier.name like :s or i.name like :s or mod(barcode, 10000) = :n)" + (
                        onlyActionArticle
                        ? " and size(i.offers) > 0"
                        : ""),
                Article.class
        )
                                    .setParameter("n", Tools.tryParseInteger(search))
                                    .setParameter("s", search + "%")
                                    .setMaxResults(maxResults)
                                    .getResultList();
        em.close();
        return out;
    }

}
