package kernbeisser.Windows.ObjectView;

import kernbeisser.Enums.Mode;
import kernbeisser.Windows.MaskLoader;
import kernbeisser.Windows.Window;

import java.util.Collection;
import java.util.function.Supplier;

public class ObjectViewModel <T> {
    private final MaskLoader<T> maskLoader;
    private Supplier<Collection<T>> itemSupplier;

    ObjectViewModel(MaskLoader<T> maskLoader,Supplier<Collection<T>> itemSupplier){
        this.maskLoader = maskLoader;
        this.itemSupplier = itemSupplier;
    }

    void openEdit(Window window,T selected){
        maskLoader.accept(window,selected,Mode.EDIT);
    }

    Collection<T> getItems(){
        return itemSupplier.get();
    }

    void openAdd(Window window,T selected){
        maskLoader.accept(window,selected,Mode.ADD);
    }
    void remove(Window window,T selected) {
        maskLoader.accept(window,selected,Mode.REMOVE);
    }

}
