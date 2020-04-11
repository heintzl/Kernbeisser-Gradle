package kernbeisser.Windows.Selector;

import kernbeisser.CustomComponents.ObjectTable.Column;
import kernbeisser.Windows.Model;
import kernbeisser.Windows.Searchable;

import java.util.Collection;

public class SelectorModel <T> implements Model<SelectorController<T>> {
    private final Collection<T> currentValues;
    private final Searchable<T> searchable;
    private final Column<T>[] columns;
    private final String title;

    public SelectorModel(Collection<T> currentValues,String title, Searchable<T> searchable, Column<T>[] columns) {
        this.currentValues = currentValues;
        this.searchable = searchable;
        this.title = title;
        this.columns = columns;
    }

    public Collection<T> getCurrentValues() {
        return currentValues;
    }

    public Searchable<T> getSearchable() {
        return searchable;
    }

    public Column<T>[] getColumns() {
        return columns;
    }

    public String getTitle() {
        return title;
    }
}
