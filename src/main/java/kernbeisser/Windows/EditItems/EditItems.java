package kernbeisser.Windows.EditItems;

import kernbeisser.CustomComponents.ObjectTable.Column;
import kernbeisser.DBEntities.Article;
import kernbeisser.Enums.Key;
import kernbeisser.Windows.EditItem.EditItemController;
import kernbeisser.Windows.ObjectView.ObjectViewController;
import kernbeisser.Windows.Window;

public class EditItems extends ObjectViewController<Article> {
    public EditItems(Window current) {
        super(current, EditItemController::new, Article::defaultSearch,
              Column.create("Name", Article::getName, Key.ARTICLE_NAME_READ),
              Column.create("Ladennummer", Article::getKbNumber,Key.ARTICLE_KB_NUMBER_READ),
              Column.create("Lieferantenummer", Article::getSuppliersItemNumber,Key.ARTICLE_SUPPLIERS_ITEM_NUMBER_READ)
        );
    }
}
