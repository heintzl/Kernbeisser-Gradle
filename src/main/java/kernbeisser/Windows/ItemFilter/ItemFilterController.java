package kernbeisser.Windows.ItemFilter;

import kernbeisser.DBEntitys.PriceList;
import kernbeisser.DBEntitys.Supplier;
import kernbeisser.Windows.Controller;
import kernbeisser.Windows.Model;
import kernbeisser.Windows.View;

import java.util.Collection;
import java.util.function.BiConsumer;

public class ItemFilterController implements Controller {
    private ItemFilterView view;
    private ItemFilterModel model;
    ItemFilterController(ItemFilterView view){
        this.view=view;
        this.model=new ItemFilterModel();
        view.setSuppliers(model.getAllSuppliers());
    }

    @Override
    public void refresh() {

    }

    @Override
    public ItemFilterView getView() {
        return view;
    }

    @Override
    public ItemFilterModel getModel() {
        return model;
    }
}
