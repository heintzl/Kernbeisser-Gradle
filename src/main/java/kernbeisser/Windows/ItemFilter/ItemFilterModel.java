package kernbeisser.Windows.ItemFilter;

import kernbeisser.DBEntitys.PriceList;
import kernbeisser.DBEntitys.Supplier;
import kernbeisser.Windows.Model;

import java.util.Collection;
import java.util.List;
import java.util.function.BiConsumer;

public class ItemFilterModel implements Model{
    private BiConsumer<PriceList,Supplier> consumer;
    ItemFilterModel(BiConsumer<PriceList,Supplier> consumer){
        this.consumer=consumer;
    }
    BiConsumer<PriceList,Supplier> getConsumer() {
        return consumer;
    }
    PriceList searchPriceListByName(String name){
        return PriceList.getAll("where name like '"+name+"'").get(0);
    }
    Collection<Supplier> getAllSuppliers(){
        return Supplier.getAll(null);
    }
}
