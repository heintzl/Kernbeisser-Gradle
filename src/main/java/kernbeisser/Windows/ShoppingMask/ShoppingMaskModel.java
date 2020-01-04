package kernbeisser.Windows.ShoppingMask;

import kernbeisser.DBConnection.DBConnection;
import kernbeisser.DBEntitys.Item;
import kernbeisser.DBEntitys.SaleSession;
import kernbeisser.DBEntitys.ShoppingItem;
import kernbeisser.Windows.Model;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.NoResultException;
import java.util.ArrayList;
import java.util.Collection;

public class ShoppingMaskModel implements Model {
    private Item selected = null;
    private int value;
    private Collection<ShoppingItem> shoppingCart = new ArrayList<>();
    private SaleSession saleSession;

    ShoppingMaskModel(SaleSession saleSession){
        this.saleSession=saleSession;
    }

    Item searchItem(String itemNumber){
        EntityManager em = DBConnection.getEntityManager();
        try{
            return em.createQuery("select i from Item i where kbNumber = '"+itemNumber+"'",Item.class).getSingleResult();
        }catch (NoResultException e) {
            try{
                return em.createQuery("select i from Item i where barcode like '%"+itemNumber+ "'",Item.class).setMaxResults(1).getSingleResult();
            }catch (NoResultException e1){
                return null;
            }
        }
    }

    Collection<ShoppingItem> getShoppingCart() {
        return shoppingCart;
    }

    int calculateTotalPrice(){
        int out = 0;
        for (ShoppingItem item : shoppingCart) {
            out+=item.getRawPrice();
        }
        return out;
    }

    Collection<Item> searchItems(String search, boolean searchName, boolean searchPriceList, boolean searchKBNumber, boolean searchBarcode){
        Collection<Item> out = new ArrayList<>();
        if(searchName||searchPriceList||searchKBNumber||searchBarcode){
            String query = "select i from Item i where "+
                    (searchBarcode ? "barcode like '%sh' OR " : "")+
                    (searchKBNumber ? "kbNumber like 'sh' OR ":"")+
                    (searchName ? "name like 'sh%' OR ":"")+
                    (searchPriceList ? "priceList.name like 'sh%' OR ":"");
            query=query.substring(0,query.length()-3).replaceAll("sh",search);
            EntityManager em = DBConnection.getEntityManager();
            out = em.createQuery(query,Item.class).getResultList();
            em.close();
        }
        return out;
    }

    boolean editBarcode(int itemId, long newBarcode){
        EntityManager em = DBConnection.getEntityManager();
        EntityTransaction et = em.getTransaction();
        et.begin();
        Item update = em.find(Item.class,itemId);
        update.setBarcode(newBarcode);
        try{
            em.persist(update);
            em.flush();
        }catch (Exception e){
            et.rollback();
            em.close();
            return false;
        }
        et.commit();
        em.close();
        return true;
    }


    Collection<Item> getAllItemsWithoutBarcode(){
        return Item.getAll("where barcode is null order by name asc");
    }

    public Item getSelected() {
        return selected;
    }

    public void setSelected(Item selected) {
        this.selected = selected;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public SaleSession getSaleSession() {
        return saleSession;
    }

    public void setSaleSession(SaleSession saleSession) {
        this.saleSession = saleSession;
    }
}
