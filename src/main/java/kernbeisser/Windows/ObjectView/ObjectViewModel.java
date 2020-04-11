package kernbeisser.Windows.ObjectView;

import kernbeisser.CustomComponents.ObjectTable.Column;
import kernbeisser.Enums.Mode;
import kernbeisser.Windows.*;

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

    void openEdit(Window window, T selected) {
        maskLoader.accept(selected, Mode.EDIT).openAsWindow(window, JFrameWindow::new);
    }

    Collection<T> getItems(String search, int max) {
        return itemSupplier.search(search, max);
    }

    void openAdd(Window window,T selected) {
        maskLoader.accept(selected, Mode.ADD).openAsWindow(window,JFrameWindow::new);
    }

    void remove(T selected) {
        maskLoader.accept(selected, Mode.REMOVE);
    }

    public Column<T>[] getColumns() {
        return columns;
    }
}
