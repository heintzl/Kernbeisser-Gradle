package kernbeisser.Windows.EditItems;

import kernbeisser.CustomComponents.ObjectTable.Column;
import kernbeisser.DBEntities.Item;
import kernbeisser.Windows.EditItem.EditItemController;
import kernbeisser.Windows.ObjectView.ObjectViewController;
import kernbeisser.Windows.Window;

public class EditItems extends ObjectViewController <Item>{
    public EditItems(Window current){
        super(current,EditItemController::new, Item::defaultSearch,
                Column.create("Name", Item::getName),
                Column.create("Ladennummer", Item::getKbNumber),
                Column.create("Lieferantenummer", Item::getSuppliersItemNumber)
        );
    }
}
