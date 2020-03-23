package kernbeisser.CustomComponents.SearchBox;

import kernbeisser.CustomComponents.ObjectTable.Column;
import kernbeisser.Windows.Controller;
import kernbeisser.Windows.Model;
import kernbeisser.Windows.Searchable;
import kernbeisser.Windows.View;

import java.util.Arrays;
import java.util.Collection;
import java.util.function.Consumer;
import java.util.function.Function;

public class SearchBoxController<T> implements Controller {

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

    void search(){
        view.setObjects(model.getValues(view.getSearch()));
    }

    void select() {
        model.select(view.getSelectedObject());
    }

    @Override
    public SearchBoxView<T> getView() {
        return view;
    }

    @Override
    public SearchBoxModel<T> getModel() {
        return model;
    }

    public void refreshLoadSolutions() {
        search();
    }
}
