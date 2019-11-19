package kernbeisser.Windows.ItemFilter;

import kernbeisser.DBEntitys.Supplier;

import java.util.Collection;

public class ItemFilterController {
    Collection<Supplier> getAllSuppliers(){
        return Supplier.getAll(null);
    }
}
