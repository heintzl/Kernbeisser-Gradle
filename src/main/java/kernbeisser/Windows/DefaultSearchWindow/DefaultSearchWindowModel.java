package kernbeisser.Windows.DefaultSearchWindow;

import kernbeisser.Windows.Searchable;

import java.util.Collection;
import java.util.function.Consumer;

public class DefaultSearchWindowModel <T> {
    private final Searchable<T> searchable;
    private final Consumer<T> selectionAction;

    public DefaultSearchWindowModel(Searchable<T> searchable, Consumer<T> selectionAction) {
        this.searchable = searchable;
        this.selectionAction = selectionAction;
    }

    Collection<T> getValues(String search,int max){
        return searchable.search(search,max);
    }

    public void choose(T selectedValue) {
        selectionAction.accept(selectedValue);
    }
}
