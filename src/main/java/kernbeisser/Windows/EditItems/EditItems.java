package kernbeisser.Windows.EditItems;

import kernbeisser.CustomComponents.ObjectTable.Column;
import kernbeisser.DBEntities.Article;
import kernbeisser.Windows.EditItem.EditItemController;
import kernbeisser.Windows.ObjectView.ObjectViewController;
import kernbeisser.Windows.Window;

public class EditItems extends ObjectViewController<Article> {
    public EditItems(Window current) {
        super(current, EditItemController::new, Article::defaultSearch,
              Column.create("Name", Article::getName),
              Column.create("Ladennummer", Article::getKbNumber),
              Column.create("Lieferantenummer", Article::getSuppliersItemNumber)
        );
    }
}
