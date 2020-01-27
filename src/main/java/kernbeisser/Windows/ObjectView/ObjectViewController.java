package kernbeisser.Windows.ObjectView;

import kernbeisser.CustomComponents.ObjectTable.Column;
import kernbeisser.Enums.Mode;
import kernbeisser.Windows.MaskLoader;
import kernbeisser.Windows.Window;

import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class ObjectViewController <T>{
    private ObjectViewModel<T> model;
    private ObjectViewView<T> view;

    public ObjectViewController(Window current, MaskLoader<T> loader, Supplier<Collection<T>> items, Column<T>... columns){
        model = new ObjectViewModel<>(loader,items);
        view = new ObjectViewView<>(current,this);
        for (Column<T> column : columns) {
            view.addColumn(column);
        }
        putItems();
    }

    void select(){
        view.setEditAvailable(true);
        view.setRemoveAvailable(true);
    }

    private void putItems(){
        view.setObjects(model.getItems());
        view.setEditAvailable(false);
        view.setRemoveAvailable(false);
    }

    void edit(){
        model.openEdit(view,view.getSelectedObject());
    }

    void add(){
        model.openAdd(view,view.getSelectedObject());
    }

    void delete(){
        model.remove(view,view.getSelectedObject());
    }

    public void refresh() {
        putItems();
    }
}
