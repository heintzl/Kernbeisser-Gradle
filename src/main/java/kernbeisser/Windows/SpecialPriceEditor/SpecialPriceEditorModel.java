package kernbeisser.Windows.SpecialPriceEditor;

import jiconfont.swing.IconFontSwing;
import kernbeisser.DBConnection.DBConnection;
import kernbeisser.DBEntities.Item;
import kernbeisser.DBEntities.Offer;
import kernbeisser.Useful.Tools;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

public class SpecialPriceEditorModel {
    private Offer selected;
    private Item selectedItem;

    public void setSelectedOffer(Offer o) {
        this.selected = o;
    }

    Offer getSelectedOffer(){
        return selected;
    }

    void edit(int offerId, Offer offer){
        Tools.edit(offerId,offer);
    }

    void remove(Item item,Offer offer){
        EntityManager em = DBConnection.getEntityManager();
        EntityTransaction et = em.getTransaction();
        et.begin();
        Item i = em.find(Item.class,item.getIid());
        Offer o = em.find(Offer.class,offer.getOid());
        i.getSpecialPriceMonths().remove(o);
        em.remove(o);
        em.persist(i);
        em.flush();
        et.commit();
        em.close();
    }

    public void refreshItem(){
        selectedItem = DBConnection.getEntityManager().find(Item.class,selectedItem.getIid());
    }

    public Item getSelectedItem() {
        return selectedItem;
    }

    public void setSelectedItem(Item selectedItem) {
        this.selectedItem = selectedItem;
    }

    public void addOffer(Item item,Offer offer) {
        EntityManager em = DBConnection.getEntityManager();
        EntityTransaction et = em.getTransaction();
        et.begin();
        Item i = em.createQuery("select i from Item i where id = :id",Item.class).setParameter("id",item.getIid()).getSingleResult();
        em.persist(offer);
        i.getSpecialPriceMonths().add(offer);
        em.flush();
        et.commit();
        em.close();
    }

}
