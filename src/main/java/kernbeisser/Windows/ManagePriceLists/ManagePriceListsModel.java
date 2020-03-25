package kernbeisser.Windows.ManagePriceLists;

import kernbeisser.CustomComponents.PriceListTree;
import kernbeisser.DBEntities.PriceList;
import kernbeisser.Windows.Model;

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

}