package kernbeisser.Windows.ItemFilter;

import kernbeisser.CustomComponents.ObjectTable.Column;
import kernbeisser.CustomComponents.ObjectTable.ObjectTable;
import kernbeisser.CustomComponents.ObjectTree.ObjectTree;
import kernbeisser.CustomComponents.PriceListTree;
import kernbeisser.DBEntitys.PriceList;
import kernbeisser.DBEntitys.Supplier;
import kernbeisser.Windows.Controller;
import kernbeisser.Windows.ItemFilter.ItemFilterController;
import kernbeisser.Windows.View;
import kernbeisser.Windows.Window;

import javax.swing.*;
import java.util.Collection;

public class ItemFilterView extends Window implements View {
    private JButton commit;
    private ObjectTree<PriceList> priceLists;
    private ObjectTable<Supplier> suppliers;

    private ItemFilterController controller;

    public ItemFilterView(Window current){
        super(current);
        controller = new ItemFilterController(this);
        commit.addActionListener(e -> back());
    }

    public PriceList getSelectedPriceList(){
        return priceLists.getSelected();
    }

    public Supplier getSelectedSupplier(){
        return suppliers.getSelectedObject();
    }

    private void createUIComponents() {
        priceLists= new PriceListTree();
        suppliers = new ObjectTable<>(Column.create("Name", Supplier::getName), Column.create("Abk\u00fcrzung", Supplier::getShortName));
    }

    void setSuppliers(Collection<Supplier> allSuppliers) {
        suppliers.setObjects(allSuppliers);
    }

    @Override
    public Controller getController() {
        return controller;
    }
}
