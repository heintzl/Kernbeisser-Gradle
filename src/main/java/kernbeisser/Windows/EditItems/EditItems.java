package kernbeisser.Windows.EditItems;

import kernbeisser.CustomComponents.ObjectTable.Column;
import kernbeisser.DBEntities.Item;
import kernbeisser.Windows.EditItem.EditItemController;
import kernbeisser.Windows.ObjectView.ObjectViewController;
import kernbeisser.Windows.Window;

public class EditItems extends ObjectViewController <Item>{
    public EditItems(Window current){
        super(current, (i, m) -> new EditItemController(current, i, m), Item.getAll(null),
                Column.create("Name", Item::getName),
                Column.create("Ladennummer", Item::getKbNumber),
                Column.create("Lieferantenummer", Item::getSuppliersItemNumber)
        );
    }
}
