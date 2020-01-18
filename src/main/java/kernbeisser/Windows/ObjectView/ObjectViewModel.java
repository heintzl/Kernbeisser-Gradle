package kernbeisser.Windows.ObjectView;

import kernbeisser.Enums.Mode;

import java.util.function.BiConsumer;

public class ObjectViewModel <T> {
    private final BiConsumer<T, Mode> handler;
    ObjectViewModel(BiConsumer<T,Mode> mask){
        this.handler = mask;
    }
    void openEdit(T selected){
        handler.accept(selected,Mode.EDIT);
    }
    void openAdd(T selected){
        handler.accept(selected,Mode.ADD);
    }
    void remove(T selected) {
        handler.accept(selected,Mode.REMOVE);
    }

}
