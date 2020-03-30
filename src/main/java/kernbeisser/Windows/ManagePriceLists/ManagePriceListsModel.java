package kernbeisser.Windows.ManagePriceLists;

import kernbeisser.CustomComponents.PriceListTree;
import kernbeisser.DBConnection.DBConnection;
import kernbeisser.DBEntities.PriceList;
import kernbeisser.Useful.Tools;
import kernbeisser.Windows.Model;

import javax.persistence.PersistenceException;
import javax.swing.tree.TreeModel;
import org.hibernate.testing.transaction.TransactionUtil;

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
}