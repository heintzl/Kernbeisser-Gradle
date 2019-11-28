package kernbeisser.Windows.ManageItems;

import kernbeisser.DBConnection.DBConnection;
import kernbeisser.DBEntitys.Item;
import kernbeisser.DBEntitys.ItemKK;
import kernbeisser.DBEntitys.PriceList;
import kernbeisser.DBEntitys.Supplier;
import kernbeisser.Useful.Tools;
import kernbeisser.Windows.Controller;
import kernbeisser.Windows.Model;
import kernbeisser.Windows.View;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.NoResultException;
import javax.persistence.RollbackException;
import java.util.List;

public class ManageItemsController implements Controller {

    private ManageItemsView view;
    private ManageItemsModel model;
    ManageItemsController(ManageItemsView view){
        this.view=view;
        this.model=new ManageItemsModel();
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
    List<PriceList> getPriceLists(String condition){
        return PriceList.getAll(condition);
    }
    List<Item> getItems(String condition){
        return Item.getAll(condition);
    }
    List<ItemKK> getItemKKs(String condition){
        return ItemKK.getAll(condition);
    }
    List<Item> searchItems(String barcode, String name, String kbNumber){
        EntityManager em = DBConnection.getEntityManager();
        List<Item> out = em.createQuery( "select i from Item i where "+
                (model.getItemFilterPriceList()==null? "" : "i.priceList = "+model.getItemFilterPriceList().getId()+" and ")+
                (model.getItemFilterSupplier()==null?"":"i.supplier = "+model.getItemFilterSupplier().getName()+ " and ")+
                "(i.barcode like '%"+barcode+
                "%' or UPPER(i.name) like '%"+name.toUpperCase()+
                "%' or i.kbNumber like '%"+kbNumber+"%') order by i.name asc",Item.class).getResultList();
        em.close();
       return out;
    }
    List<ItemKK> searchItemKKs(String barcode, String name, String kkNumber){
        EntityManager em = DBConnection.getEntityManager();
        List<ItemKK> out = em.createQuery("select i from ItemKK i.barcode like '%"+barcode+
                "%' or UPPER(i.name) like '%"+name.toUpperCase()+
                "%' or i.kkNumber like '%"+kkNumber+"%' order by i.name asc",ItemKK.class).getResultList();
        em.close();
        return out;
    }
    void setFilter(Supplier supplier){
        model.setItemFilterSupplier(supplier);
    }
    void setFilter(PriceList priceList){
        model.setItemFilterPriceList(priceList);
    }
    void setFilter(PriceList p,Supplier s){
        model.setItemFilterPriceList(p);
        model.setItemFilterSupplier(s);
    }
    List<PriceList> getAllPriceLists(){
        return PriceList.getAll(null);
    }
    List<Supplier> getAllSupplier(){
        return Supplier.getAll(null);
    }
    List<String> getAllPriceListNames(){
        return Tools.transform(getPriceLists(null),PriceList::getName);
    }

    @Override
    public void refresh() {

    }

    @Override
    public ManageItemsView getView() {
        return view;
    }

    @Override
    public ManageItemsModel getModel() {
        return model;
    }
}
