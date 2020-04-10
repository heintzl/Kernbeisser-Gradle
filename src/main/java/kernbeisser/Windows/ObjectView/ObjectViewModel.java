package kernbeisser.Windows.ObjectView;

import kernbeisser.CustomComponents.ObjectTable.Column;
import kernbeisser.Enums.Mode;
import kernbeisser.Windows.Window;
import kernbeisser.Windows.MaskLoader;
import kernbeisser.Windows.Model;
import kernbeisser.Windows.Searchable;

import java.util.Collection;

public class ObjectViewModel<T> implements Model<ObjectViewController<T>> {
    private final MaskLoader<T> maskLoader;
    private final Searchable<T> itemSupplier;
    private final Column<T>[] columns;

    ObjectViewModel(MaskLoader<T> maskLoader, Searchable<T> itemSupplier, Column<T>... columns) {
        this.maskLoader = maskLoader;
        this.itemSupplier = itemSupplier;
        this.columns = columns;
    }

    void openEdit(Window Window, T selected) {
        maskLoader.accept(Window, selected, Mode.EDIT);
    }

    Collection<T> getItems(String search, int max) {
        return itemSupplier.search(search, max);
    }

    void openAdd(Window Window, T selected) {
        maskLoader.accept(Window, selected, Mode.ADD);
    }

    void remove(Window Window, T selected) {
        maskLoader.accept(Window, selected, Mode.REMOVE);
    }

    public Column<T>[] getColumns() {
        return columns;
    }
}
