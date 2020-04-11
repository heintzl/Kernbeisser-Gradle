package kernbeisser.Windows.ObjectView;

import kernbeisser.CustomComponents.ObjectTable.Column;
import kernbeisser.Enums.Key;
import kernbeisser.Windows.Controller;
import kernbeisser.Windows.MaskLoader;
import kernbeisser.Windows.Searchable;
import org.jetbrains.annotations.NotNull;

public class ObjectViewController<T> implements Controller<ObjectViewView<T>,ObjectViewModel<T>> {
    private ObjectViewModel<T> model;
    private ObjectViewView<T> view;

    public ObjectViewController(MaskLoader<T> loader, Searchable<T> items, Column<T>... columns) {
        model = new ObjectViewModel<>(loader, items,columns);
        view = new ObjectViewView<>(this);
    }

    void select() {
        view.setEditAvailable(true);
        view.setRemoveAvailable(true);
    }

    private void putItems() {
        view.setObjects(model.getItems(view.getSearch(), view.getMax()));
        view.setEditAvailable(false);
        view.setRemoveAvailable(false);
    }

    void edit() {
        model.openEdit(view.getWindow(),view.getSelectedObject());
    }

    void add() {
        model.openAdd(view.getWindow(), view.getSelectedObject());
    }

    void delete() {
        if (view.commitDelete()) {
            model.remove(view.getSelectedObject());
        }
        refresh();
    }

    public void refresh() {
        putItems();
    }

    @Override
    public @NotNull ObjectViewView<T> getView() {
        return view;
    }

    @Override
    public @NotNull ObjectViewModel<T> getModel() {
        return model;
    }

    @Override
    public void fillUI() {
        for (Column<T> column : model.getColumns()) {
            view.addColumn(column);
        }
        putItems();
    }

    @Override
    public Key[] getRequiredKeys() {
        return new Key[0];
    }
}
