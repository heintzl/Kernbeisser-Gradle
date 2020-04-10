package kernbeisser.CustomComponents.SearchBox;

import kernbeisser.Enums.Setting;
import kernbeisser.Windows.Model;
import kernbeisser.Windows.Searchable;

import java.util.Collection;
import java.util.function.Consumer;

public class SearchBoxModel<T> implements Model<SearchBoxController<T>> {
    private final Searchable<T> searchable;
    private final Consumer<T> select;

    SearchBoxModel(Searchable<T> searchable, Consumer<T> select){
        this.searchable = searchable;
        this.select = select;
    }
    Collection<T> getValues(String s){
        return searchable.search(s,Setting.DEFAULT_MAX_SEARCH.getIntValue());
    }

    public void select(T selectedObject) {
        select.accept(selectedObject);
    }
}
