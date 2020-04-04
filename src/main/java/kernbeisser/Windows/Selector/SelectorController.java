package kernbeisser.Windows.Selector;

import kernbeisser.CustomComponents.ObjectTable.Column;
import kernbeisser.CustomComponents.SearchBox.SearchBoxController;
import kernbeisser.Windows.Controller;
import kernbeisser.Windows.Model;
import kernbeisser.Windows.Searchable;
import kernbeisser.Windows.Window;

import java.util.Collection;

public class SelectorController <T> implements Controller {
    private SelectorModel<T> model;
    private SelectorView<T> view;

    public SelectorController(Window current,String title, Collection<T> currentValues, Searchable<T> searchable, Column<T> ... columns) {
        this.view = new SelectorView<T>(current, this);
        this.model = new SelectorModel<T>(currentValues,title,searchable,columns);
        view.setObjects(currentValues);
        view.setColumns(columns);
        view.setTitle(model.getTitle());
    }


    @Override
    public SelectorView<T> getView() {
        return view;
    }

    @Override
    public Model getModel() {
        return model;
    }


    public void remove() {
        model.getCurrentValues().remove(view.getSelectedValue());
        view.removeValue(view.getSelectedValue());
    }

    public void add() {
        Window window = new Window(null);
        SearchBoxController<T> controller = new SearchBoxController<T>(model.getSearchable(),e -> {
            model.getCurrentValues().add(e);
            SelectorController.this.view.addValue(e);
            window.back();
        },model.getColumns());
        window.setTitle(model.getTitle());
        window.add(controller.getView());
        window.windowInitialized();
    }
}
