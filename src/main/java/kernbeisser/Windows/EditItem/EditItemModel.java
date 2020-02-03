package kernbeisser.Windows.EditItem;

import kernbeisser.DBConnection.DBConnection;
import kernbeisser.DBEntities.Item;
import kernbeisser.DBEntities.PriceList;
import kernbeisser.DBEntities.Supplier;
import kernbeisser.Enums.*;
import kernbeisser.Useful.Tools;
import kernbeisser.Windows.Model;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import java.util.Collection;

public class EditItemModel implements Model {
    private final Mode mode;
    private Item item;
    EditItemModel(Item item,Mode mode){
        this.mode=mode;
        this.item = item;
    }
    Item getSource(){return item;}
    boolean doAction(Item item){
        try{
            switch (mode){
                case ADD:
                    addItem(item);
                    break;
                case EDIT:
                    editItem(item);
                    break;
                case REMOVE:
                    removeItem(item);
                    break;
            }
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }

    }
    private void removeItem(Item item){
        EntityManager em = DBConnection.getEntityManager();
        EntityTransaction et = em.getTransaction();
        et.begin();
        em.remove(item);
        em.flush();
        et.commit();
        em.close();
    }
    private void editItem(Item item){
        EntityManager em = DBConnection.getEntityManager();
        EntityTransaction et = em.getTransaction();
        et.begin();
        Item edit = em.find(Item.class,item.getIid());
        edit.setContainerDef(item.getContainerDef());
        edit.setSupplier(item.getSupplier());
        edit.setPriceList(item.getPriceList());
        edit.setInfo(item.getInfo());
        edit.setWeighAble(item.isWeighAble());
        edit.setShowInShop(item.isShowInShop());
        edit.setBarcode(item.getBarcode());
        edit.setAmount(item.getAmount());
        edit.setContainerSize(item.getContainerSize());
        edit.setCrateDeposit(item.getCrateDeposit());
        edit.setSuppliersItemNumber(item.getSuppliersItemNumber());
        edit.setVatLow(item.isVatLow());
        edit.setNetPrice(item.getNetPrice());
        edit.setKbNumber(item.getKbNumber());
        edit.setSingleDeposit(item.getSingleDeposit());
        edit.setName(item.getName());
        edit.setDeleteAllowed(item.isDeleteAllowed());
        edit.setCooling(item.getCooling());
        edit.setCoveredIntake(item.isCoveredIntake());
        edit.setDeleted(item.isDeleted());
        edit.setDeletedDate(item.getDeletedDate());
        edit.setDelivered(item.getDelivered());
        edit.setIntake(item.getIntake());
        edit.setListed(item.isListed());
        edit.setPrintAgain(item.isPrintAgain());
        edit.setSold(item.getSold());
        edit.setSpecialPriceMonth(item.getSpecialPriceMonth());
        edit.setSpecialPriceNet(item.getSpecialPriceNet());
        edit.setUnit(item.getUnit());
        em.persist(edit);
        em.flush();
        et.commit();
        em.close();
    }

    boolean kbNumberExists(int kbNumber){
        EntityManager em = DBConnection.getEntityManager();
        boolean exists = em.createQuery("select id from Item where kbNumber = "+kbNumber).getResultList().size()>0;
        em.close();
        return exists;
    }

    boolean barcodeExists(long barcode){
        EntityManager em = DBConnection.getEntityManager();
        boolean exists = em.createQuery("select id from Item where barcode = "+barcode).getResultList().size()>0;
        em.close();
        return exists;
    }

    private void addItem(Item item){
        EntityManager em = DBConnection.getEntityManager();
        EntityTransaction et = em.getTransaction();
        et.begin();
        em.persist(Tools.mergeWithoutId(item));
        em.flush();
        et.commit();
        em.close();
    }
    Unit[] getAllUnits(){return Unit.values();}
    ContainerDefinition[] getAllContainerDefinitions(){return ContainerDefinition.values();}
    VAT[] getAllVATs(){
        return VAT.values();
    }
    Collection<Supplier> getAllSuppliers(){return Supplier.getAll(null);}
    Collection<PriceList> getAllPriceLists(){return PriceList.getAll(null);}

    public Mode getMode() {
        return mode;
    }
}
