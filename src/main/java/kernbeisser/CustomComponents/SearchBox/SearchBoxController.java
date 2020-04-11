package kernbeisser.CustomComponents.SearchBox;

import kernbeisser.CustomComponents.ObjectTable.Column;
import kernbeisser.Enums.Key;
import kernbeisser.Windows.Controller;
import kernbeisser.Windows.Searchable;
import kernbeisser.Windows.Window;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.function.Consumer;
import java.util.function.Function;

public class SearchBoxController<T> implements Controller<SearchBoxView<T>,SearchBoxModel<T>> {

    private final SearchBoxView<T> view;
    private final SearchBoxModel<T> model;

    public SearchBoxController(Searchable<T> searchFunction, Consumer<T> select, Column<T>... columns){
        this.model = new SearchBoxModel<>(searchFunction,select);
        this.view = new SearchBoxView<>(this);
        view.setColumns(Arrays.asList(columns));
        search();
    }

    public T getSelectedObject(){
        return view.getSelectedObject();
    }

    public void search(){
        view.setObjects(model.getValues(view.getSearch()));
    }

    void select() {
        model.select(view.getSelectedObject());
    }

    @Override
    public @NotNull SearchBoxView<T> getView() {
        return view;
    }

    @Override
    public @NotNull SearchBoxModel<T> getModel() {
        return model;
    }

    @Override
    public void fillUI() {

    }

    @Override
    public Key[] getRequiredKeys() {
        return new Key[0];
    }

    public void refreshLoadSolutions() {
        search();
    }
}
