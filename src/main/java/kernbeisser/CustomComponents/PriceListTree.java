package kernbeisser.CustomComponents;

import kernbeisser.DBConnection.DBConnection;
import kernbeisser.DBEntitys.PriceList;
import kernbeisser.Windows.ManagePriceLists.ManagePriceListsView;
import kernbeisser.Windows.Window;

import javax.persistence.EntityManager;
import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;

/**
 * A JTree filled with all SuperGroups and Groups from the PriceLists include the PriceLists from the Database
 */
public class PriceListTree extends JTree {
    public PriceListTree() {
        this(true);
    }

    /**
     * fills the JTree with the SuperGroups than the UnderGroups from the SuperGroups
     * the Groups and at least the PriceLists as UnderGroup from theGroups
     *
     * @param optionToEdit if true there is a Option to get to the Add PriceLists window to add the needed PriceList
     */
    public PriceListTree(boolean optionToEdit) {
        EntityManager em = DBConnection.getEntityManager();
        DefaultMutableTreeNode priceList = new DefaultMutableTreeNode("PriceList");
        getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        em.createQuery("select p from PriceList p where p.superPriceList is null", PriceList.class).getResultStream().
                forEach(e -> priceList.add(createTreeNode(e)));
        if (optionToEdit) {
            priceList.add(new DefaultMutableTreeNode("Neu Hinzuf\u00fcgen/Bearbeiten"));
            addTreeSelectionListener(e -> {
                Object o = getLastSelectedPathComponent();
                if (o != null && o.toString().equals("Neu Hinzuf\u00fcgen/Bearbeiten")) new ManagePriceListsView(null) {
                    @Override
                    public void finish(Window window) {
                        dispose();
                        PriceListTree.this.setModel(new PriceListTree().getModel());
                    }
                };
            });
        }
        DefaultTreeModel model = new DefaultTreeModel(priceList);
        setModel(model);
        em.close();
    }

    private DefaultMutableTreeNode createTreeNode(PriceList p) {
        EntityManager em = DBConnection.getEntityManager();
        DefaultMutableTreeNode treeNode = new DefaultMutableTreeNode(p.getName());
        em.createQuery("select p from PriceList p where p.superPriceList = " + p.getId(), PriceList.class).
                getResultList().
                forEach(e -> treeNode.add(createTreeNode(e)));
        em.close();
        return treeNode;
    }
}
