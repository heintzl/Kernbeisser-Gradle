package kernbeisser.Windows.ManageItems;

import kernbeisser.DBConnection.DBConnection;
import kernbeisser.DBEntitys.Item;
import kernbeisser.DBEntitys.ItemKK;
import kernbeisser.DBEntitys.PriceList;
import kernbeisser.DBEntitys.Supplier;
import kernbeisser.Useful.Tools;
import kernbeisser.Windows.Model;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.NoResultException;
import javax.persistence.RollbackException;
import java.util.List;

class ManageItemsModel implements Model {
    private Supplier itemFilterSupplier;
    private PriceList itemFilterPriceList;

    public Supplier getItemFilterSupplier() {
        return itemFilterSupplier;
    }

    void setItemFilterSupplier(Supplier itemFilterSupplier) {
        this.itemFilterSupplier = itemFilterSupplier;
    }

    public PriceList getItemFilterPriceList() {
        return itemFilterPriceList;
    }

    public void setItemFilterPriceList(PriceList itemFilterPriceList) {
        this.itemFilterPriceList = itemFilterPriceList;
    }

    List<PriceList> getAllPriceLists(){
        return PriceList.getAll(null);
    }
    List<Supplier> getAllSupplier(){
        return Supplier.getAll(null);
    }
    List<String> getAllPriceListNames(){
        return Tools.transform(PriceList.getAll(null),PriceList::getName);
    }

    List<ItemKK> searchItemKKs(String barcode, String name, String kkNumber){
        EntityManager em = DBConnection.getEntityManager();
        List<ItemKK> out = em.createQuery("select i from ItemKK i.barcode like '%"+barcode+
                "%' or UPPER(i.name) like '%"+name.toUpperCase()+
                "%' or i.kkNumber like '%"+kkNumber+"%' order by i.name asc",ItemKK.class).getResultList();
        em.close();
        return out;
    }

    List<Item> searchItems(String barcode, String name, String kbNumber){
        EntityManager em = DBConnection.getEntityManager();
        List<Item> out = em.createQuery( "select i from Item i where "+
                (itemFilterPriceList==null? "" : "i.priceList.id = "+itemFilterPriceList.getId()+" and ")+
                (itemFilterSupplier==null?"":"i.supplier.id = "+itemFilterSupplier.getId()+ " and ")+
                "(i.barcode like '%"+barcode+
                "%' or UPPER(i.name) like '%"+name.toUpperCase()+
                "%' or i.kbNumber like '%"+kbNumber+"%') order by i.name asc",Item.class).getResultList();
        em.close();
        return out;
    }
    Item searchItem(String barcode, String kbNumber) {
        EntityManager em = DBConnection.getEntityManager();
        try {
            return em.createQuery("select i from Item i where i.barcode like '" + barcode + "' or i.kbNumber like '" + kbNumber + "'", Item.class).getSingleResult();
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
    }
    boolean save(Item i){
        EntityManager em = DBConnection.getEntityManager();
        EntityTransaction et = em.getTransaction();
        et.begin();
        try{
            em.persist(i);
            em.flush();
            et.commit();
            em.close();
        }catch (RollbackException e){
            et.rollback();
            em.close();
            return false;
        }
        return true;
    }
}
