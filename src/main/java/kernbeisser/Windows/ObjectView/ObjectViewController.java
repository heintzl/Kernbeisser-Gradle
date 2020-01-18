package kernbeisser.Windows.ObjectView;

import kernbeisser.CustomComponents.ObjectTable.Column;
import kernbeisser.Enums.Mode;
import kernbeisser.Windows.Window;

import java.util.Collection;
import java.util.function.BiConsumer;

public class ObjectViewController <T>{
    private ObjectViewModel<T> model;
    private ObjectViewView<T> view;

    public ObjectViewController(Window current, BiConsumer<T, Mode> handler, Collection<T> items, Column<T>... columns){
        model = new ObjectViewModel<>(handler);
        view = new ObjectViewView<>(current,this);
        for (Column<T> column : columns) {
            view.addColumn(column);
        }
        view.setObjects(items);
    }

    void edit(){
        model.openEdit(view.getSelectedObject());
    }

    void add(){
        model.openAdd(view.getSelectedObject());
    }

    void delete(){
        model.remove(view.getSelectedObject());
    }

}
