package kernbeisser.Windows.SpecialPriceEditor;

import kernbeisser.DBConnection.DBConnection;
import kernbeisser.DBEntities.Article;
import kernbeisser.DBEntities.Offer;
import kernbeisser.Useful.Tools;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

public class SpecialPriceEditorModel {
    private Offer selected;
    private Article selectedArticle;

    public void setSelectedOffer(Offer o) {
        this.selected = o;
    }

    Offer getSelectedOffer(){
        return selected;
    }

    void edit(int offerId, Offer offer){
        Tools.edit(offerId,offer);
    }

    void remove(Article article, Offer offer){
        EntityManager em = DBConnection.getEntityManager();
        EntityTransaction et = em.getTransaction();
        et.begin();
        Article i = em.find(Article.class, article.getIid());
        Offer o = em.find(Offer.class,offer.getOid());
        i.getSpecialPriceMonths().remove(o);
        em.remove(o);
        em.persist(i);
        em.flush();
        et.commit();
        em.close();
    }

    public void refreshItem(){
        selectedArticle = DBConnection.getEntityManager().find(Article.class, selectedArticle.getIid());
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
        Article i = em.createQuery("select i from Article i where id = :id", Article.class).setParameter("id", article.getIid()).getSingleResult();
        em.persist(offer);
        i.getSpecialPriceMonths().add(offer);
        em.flush();
        et.commit();
        em.close();
    }

}
