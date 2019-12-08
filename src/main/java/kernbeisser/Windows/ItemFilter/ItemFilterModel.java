package kernbeisser.Windows.ItemFilter;

import kernbeisser.DBEntitys.PriceList;
import kernbeisser.DBEntitys.Supplier;
import kernbeisser.Windows.Model;

import java.util.function.BiConsumer;

public class ItemFilterModel implements Model{
    private BiConsumer<PriceList,Supplier> consumer;
    ItemFilterModel(BiConsumer<PriceList,Supplier> consumer){
        this.consumer=consumer;
    }
    public BiConsumer<PriceList,Supplier> getConsumer() {
        return consumer;
    }
}
