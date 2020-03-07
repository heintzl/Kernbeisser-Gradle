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

    EditItemModel(Item item, Mode mode) {
        this.mode = mode;
        this.item = item;
    }

    Item getSource() {
        return item;
    }

    boolean doAction(Item item) {
        try {
            switch (mode) {
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
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }

    private void removeItem(Item item) {
        Tools.delete(item.getIid(), item);
    }

    private void editItem(Item item) {
        Tools.edit(item.getIid(), item);
    }

    boolean kbNumberExists(int kbNumber) {
        EntityManager em = DBConnection.getEntityManager();
        boolean exists = em.createQuery("select id from Item where kbNumber = " + kbNumber).getResultList().size() > 0;
        em.close();
        return exists;
    }

    boolean barcodeExists(long barcode) {
        EntityManager em = DBConnection.getEntityManager();
        boolean exists = em.createQuery("select id from Item where barcode = " + barcode).getResultList().size() > 0;
        em.close();
        return exists;
    }

    private void addItem(Item item) {
        item.setSurcharge(item.getSurchargeTable().getSurcharge());
        EntityManager em = DBConnection.getEntityManager();
        EntityTransaction et = em.getTransaction();
        et.begin();
        em.persist(Tools.mergeWithoutId(item));
        em.flush();
        et.commit();
        em.close();
    }

    Unit[] getAllUnits() {
        return Unit.values();
    }

    ContainerDefinition[] getAllContainerDefinitions() {
        return ContainerDefinition.values();
    }

    VAT[] getAllVATs() {
        return VAT.values();
    }

    Collection<Supplier> getAllSuppliers() {
        return Supplier.getAll(null);
    }

    Collection<PriceList> getAllPriceLists() {
        return PriceList.getAll(null);
    }

    public Mode getMode() {
        return mode;
    }
}
