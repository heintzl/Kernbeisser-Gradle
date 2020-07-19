package kernbeisser.Windows.ObjectView;

import kernbeisser.Enums.Mode;
import kernbeisser.Useful.Tools;
import kernbeisser.Windows.MaskLoader;
import kernbeisser.Windows.Model;
import kernbeisser.Windows.Searchable;
import kernbeisser.Windows.Window;
import kernbeisser.Windows.WindowImpl.SubWindow;

import java.util.Collection;

public class ObjectViewModel<T> implements Model<ObjectViewController<T>> {
    private final MaskLoader<T> maskLoader;
    private final Searchable<T> itemSupplier;

    private final boolean copyValuesToAdd;

    ObjectViewModel(MaskLoader<T> maskLoader, Searchable<T> itemSupplier, boolean copyValuesToAdd) {
        this.maskLoader = maskLoader;
        this.itemSupplier = itemSupplier;
        this.copyValuesToAdd = copyValuesToAdd;
    }

    Window openEdit(Window window, T selected) {
        return maskLoader.accept(selected, Mode.EDIT).openAsWindow(window, SubWindow::new);
    }

    Collection<T> getItems(String search, int max) {
        return itemSupplier.search(search, max);
    }

    Window openAdd(Window window, T selected) {
        return maskLoader.accept(copyValuesToAdd ? selected : (T) Tools.invokeConstructor(selected.getClass()),
                                 Mode.ADD).openAsWindow(window, SubWindow::new);
    }

    void remove(T selected) {
        maskLoader.accept(selected, Mode.REMOVE);
    }

}
