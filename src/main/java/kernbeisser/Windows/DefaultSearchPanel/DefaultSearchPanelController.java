package kernbeisser.Windows.DefaultSearchPanel;

import kernbeisser.CustomComponents.ObjectTable.Column;
import kernbeisser.Windows.*;

import java.util.Arrays;
import java.util.Collection;
import java.util.function.Consumer;

public class DefaultSearchPanelController<T> implements Controller {
    private DefaultSearchPanelView<T> view;
    private DefaultSearchPanelModel<T> model;


    public DefaultSearchPanelController(Searchable<T> searchable, Consumer<T> selection, Column<T>... columns) {
        this(searchable, selection, Arrays.asList(columns));
    }

    DefaultSearchPanelController(Searchable<T> searchable, Consumer<T> selection, Collection<Column<T>> columns) {
        this.view = new DefaultSearchPanelView<>(this);
        this.model = new DefaultSearchPanelModel<>(searchable, selection);
        view.setColumns(columns);
    }

    public void choose() {
        model.choose(view.getSelectedValue());
    }


    @Override
    public void refresh() {
        view.setValues(model.getValues(view.getSearch(), view.getMax()));
        view.setChooseEnabled(false);
    }

    @Override
    public DefaultSearchPanelView<T> getView() {
        return view;
    }

    @Override
    public Model getModel() {
        return model;
    }
}
