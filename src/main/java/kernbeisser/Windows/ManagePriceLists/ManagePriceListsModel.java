package kernbeisser.Windows.ManagePriceLists;

import kernbeisser.CustomComponents.PriceListTree;
import kernbeisser.DBConnection.DBConnection;
import kernbeisser.DBEntities.PriceList;
import kernbeisser.Windows.Model;
import org.hibernate.Session;
import org.hibernate.testing.transaction.TransactionUtil;

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
        TransactionUtil.doInJPA(DBConnection::getEntityManagerFactory, em -> { em.unwrap(Session.class).update(toRename);});
    }
}