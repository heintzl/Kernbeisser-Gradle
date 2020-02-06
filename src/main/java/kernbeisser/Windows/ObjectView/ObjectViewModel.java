package kernbeisser.Windows.ObjectView;

import kernbeisser.Enums.Mode;
import kernbeisser.Windows.MaskLoader;
import kernbeisser.Windows.Searchable;
import kernbeisser.Windows.Window;

import java.util.Collection;
import java.util.function.Supplier;

public class ObjectViewModel <T> {
    private final MaskLoader<T> maskLoader;
    private Searchable<T> itemSupplier;

    ObjectViewModel(MaskLoader<T> maskLoader, Searchable<T> itemSupplier){
        this.maskLoader = maskLoader;
        this.itemSupplier = itemSupplier;
    }

    void openEdit(Window window,T selected){
        maskLoader.accept(window,selected,Mode.EDIT);
    }

    Collection<T> getItems(String search,int max){
        return itemSupplier.search(search,max);
    }

    void openAdd(Window window,T selected){
        maskLoader.accept(window,selected,Mode.ADD);
    }
    void remove(Window window,T selected) {
        maskLoader.accept(window,selected,Mode.REMOVE);
    }

}
