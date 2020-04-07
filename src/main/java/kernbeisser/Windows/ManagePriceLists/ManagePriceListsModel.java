package kernbeisser.Windows.ManagePriceLists;

import kernbeisser.CustomComponents.PriceListTree;
import kernbeisser.DBConnection.DBConnection;
import kernbeisser.DBEntities.PriceList;
import kernbeisser.Useful.Tools;
import kernbeisser.Windows.Model;
import org.hibernate.Session;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.PersistenceException;
import javax.swing.tree.TreeModel;

public class ManagePriceListsModel implements Model {

    private TreeModel PriceListTreeModel;

    public TreeModel getPriceListTreeModel() {
        return PriceListTreeModel;
    }

    void refresh() {
        PriceListTreeModel = new PriceListTree(false).getModel();
    }

    void savePriceList(String name, PriceList superPriceList) {
        PriceList.savePriceList(name, superPriceList);
    }

    public void deletePriceList(PriceList toDelete) throws PersistenceException {
        PriceList.deletePriceList(toDelete);
    }

    public void renamePriceList(PriceList toRename, String newName) {
        toRename.setName(newName);
        Tools.persistInDB(em -> em.unwrap(Session.class).update(toRename));
    }
}