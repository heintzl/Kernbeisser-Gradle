package kernbeisser.Windows.ObjectView;

import kernbeisser.CustomComponents.ObjectTable.Column;
import kernbeisser.Enums.Mode;
import kernbeisser.Windows.*;
import kernbeisser.Windows.WindowImpl.JFrameWindow;
import kernbeisser.Windows.WindowImpl.SubWindow;

import java.util.Collection;

public class ObjectViewModel<T> implements Model<ObjectViewController<T>> {
    private final MaskLoader<T> maskLoader;
    private final Searchable<T> itemSupplier;

    ObjectViewModel(MaskLoader<T> maskLoader, Searchable<T> itemSupplier) {
        this.maskLoader = maskLoader;
        this.itemSupplier = itemSupplier;
    }

    Window openEdit(Window window, T selected) {
        return maskLoader.accept(selected, Mode.EDIT).openAsWindow(window,SubWindow::new);
    }

    Collection<T> getItems(String search, int max) {
        return itemSupplier.search(search, max);
    }

    Window openAdd(Window window, T selected) {
        return maskLoader.accept(selected, Mode.ADD).openAsWindow(window,SubWindow::new);
    }

    void remove(T selected) {
        maskLoader.accept(selected, Mode.REMOVE);
    }

}
