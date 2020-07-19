package kernbeisser.Windows.CollectionView;

import kernbeisser.CustomComponents.ObjectTable.Column;
import kernbeisser.Windows.Model;

import java.util.Collection;

public class CollectionModel<T> implements Model<CollectionController<T>> {

    private final Collection<T> loaded;

    private final Collection<T> available;

    private final Column<T>[] columns;

    private final boolean editable;

    public CollectionModel(Collection<T> loaded, Collection<T> available, boolean editable, Column<T>[] columns) {
        this.columns = columns;
        this.loaded = loaded;
        this.editable = editable;
        this.available = available;
    }

    public Collection<T> getLoaded() {
        return loaded;
    }

    public Collection<T> getAvailable() {
        return available;
    }

    public Column<T>[] getColumns() {
        return columns;
    }

    public boolean isEditable() {
        return editable;
    }
}
