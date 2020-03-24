package kernbeisser.Windows.ManagePriceLists;

import kernbeisser.CustomComponents.PriceListTree;
import kernbeisser.Windows.Model;

import javax.swing.tree.TreeModel;

public class ManagePriceListsModel implements Model {

    private TreeModel PriceListTreeModel;

    ManagePriceListsModel() {
        PriceListTreeModel = new PriceListTree(false).getModel();
    }

    public TreeModel getPriceListTreeModel() {
        return PriceListTreeModel;
    }

}
