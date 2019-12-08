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
    ItemFilterController(ItemFilterView view, BiConsumer<PriceList,Supplier> consumer){
        this.view=view;
        this.model=new ItemFilterModel(consumer);
    }
    Collection<Supplier> getAllSuppliers(){
        return Supplier.getAll(null);
    }

    void selectFilter(PriceList p,Supplier s){
        model.getConsumer().accept(p,s);
    }

    PriceList getByName(String name){
        return PriceList.getAll("where name like '"+name+"'").get(0);
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
