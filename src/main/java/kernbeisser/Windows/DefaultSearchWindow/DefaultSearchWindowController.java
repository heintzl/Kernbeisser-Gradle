package kernbeisser.Windows.DefaultSearchWindow;

import kernbeisser.CustomComponents.ObjectTable.Column;
import kernbeisser.Windows.*;

import java.util.Arrays;
import java.util.Collection;
import java.util.function.Consumer;

public class DefaultSearchWindowController<T> implements Controller {
    DefaultSearchWindowView<T> view;
    DefaultSearchWindowModel<T> model;


    public DefaultSearchWindowController(Window current, Searchable<T> searchable, Consumer<T> selection, Column<T>... columns) {
        this(current, searchable, selection, Arrays.asList(columns));
    }

    DefaultSearchWindowController(Window current, Searchable<T> searchable, Consumer<T> selection, Collection<Column<T>> columns) {
        this.view = new DefaultSearchWindowView<>(current, this);
        this.model = new DefaultSearchWindowModel<>(searchable, selection);
        view.setColumns(columns);
    }

    public void choose() {
        model.choose(view.getSelectedValue());
        view.back();
    }


    @Override
    public void refresh() {
        view.setValues(model.getValues(view.getSearch(), view.getMax()));
        view.setChooseEnabled(false);
    }

    @Override
    public View getView() {
        return view;
    }

    @Override
    public Model getModel() {
        return model;
    }
}
