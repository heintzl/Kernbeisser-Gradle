package kernbeisser.Windows.ObjectView;

import kernbeisser.CustomComponents.ObjectTable.Column;
import kernbeisser.CustomComponents.SearchBox.SearchBoxController;
import kernbeisser.CustomComponents.SearchBox.SearchBoxView;
import kernbeisser.Enums.Key;
import kernbeisser.Windows.Controller;
import kernbeisser.Windows.MaskLoader;
import kernbeisser.Windows.Searchable;
import org.jetbrains.annotations.NotNull;

public class ObjectViewController<T> implements Controller<ObjectViewView<T>,ObjectViewModel<T>> {
    private final ObjectViewModel<T> model;
    private final ObjectViewView<T> view;

    private final SearchBoxController<T> searchBoxController;

    public ObjectViewController(MaskLoader<T> loader, Searchable<T> items, Column<T>... columns) {
        searchBoxController = new SearchBoxController<T>(items, columns);
        searchBoxController.initView();
        searchBoxController.addSelectionListener(e -> select());
        searchBoxController.addDoubleClickListener(e -> edit());
        searchBoxController.addLostSelectionListener(this::putItems);

        model = new ObjectViewModel<>(loader, items);
        view = new ObjectViewView<>(this);
    }

    void select() {
        view.setEditAvailable(true);
        view.setRemoveAvailable(true);
    }

    private void putItems() {
        view.setEditAvailable(false);
        view.setRemoveAvailable(false);
    }

    void edit() {
        model.openEdit(view.getWindow(),searchBoxController.getSelectedObject()).addCloseEventListener(e -> search());
        putItems();
    }

    void add() {
        model.openAdd(view.getWindow(),searchBoxController.getSelectedObject()).addCloseEventListener(e -> search());
        putItems();
    }

    void delete() {
        if (view.commitDelete()) {
            model.remove(searchBoxController.getSelectedObject());
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
        putItems();
    }

    @Override
    public Key[] getRequiredKeys() {
        return new Key[0];
    }

    public SearchBoxView<T> getSearchBoxView() {
        return searchBoxController.getView();
    }

    public void setSearch(String s) {
        searchBoxController.setSearch(s);
    }


    public void search(){
        searchBoxController.refreshLoadSolutions();
    }
}
