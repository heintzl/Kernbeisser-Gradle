package kernbeisser.Windows.ManageItems;

import kernbeisser.DBEntitys.PriceList;
import kernbeisser.DBEntitys.Supplier;
import kernbeisser.Windows.Model;

public class ManageItemsModel implements Model {
    private Supplier itemFilterSupplier;
    private PriceList itemFilterPriceList;

    public Supplier getItemFilterSupplier() {
        return itemFilterSupplier;
    }

    public void setItemFilterSupplier(Supplier itemFilterSupplier) {
        this.itemFilterSupplier = itemFilterSupplier;
    }

    public PriceList getItemFilterPriceList() {
        return itemFilterPriceList;
    }

    public void setItemFilterPriceList(PriceList itemFilterPriceList) {
        this.itemFilterPriceList = itemFilterPriceList;
    }
}
