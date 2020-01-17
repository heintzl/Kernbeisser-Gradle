package kernbeisser.Windows.ManageItems.EditItem;

import kernbeisser.DBEntitys.PriceList;
import kernbeisser.DBEntitys.Supplier;
import kernbeisser.Enums.ContainerDefinition;
import kernbeisser.Enums.Unit;
import kernbeisser.Enums.VAT;
import kernbeisser.Windows.Model;

import java.util.Collection;

public class EditItemModel implements Model {
    Unit[] getAllUnits(){return Unit.values();}
    ContainerDefinition[] getAllContainerDefinitions(){return ContainerDefinition.values();}
    VAT[] getAllVATs(){
        return VAT.values();
    }
    Collection<Supplier> getAllSuppliers(){return Supplier.getAll(null);}
    Collection<PriceList> getAllPriceLists(){return PriceList.getAll(null);}

}
