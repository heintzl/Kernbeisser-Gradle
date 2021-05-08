package kernbeisser.Windows.Selector;

import java.util.Collection;
import kernbeisser.CustomComponents.ObjectTable.Column;
import kernbeisser.CustomComponents.ObjectTable.ObjectSelectionListener;
import kernbeisser.CustomComponents.SearchBox.SearchBoxController;
import kernbeisser.Exeptions.NoSelectionException;
import kernbeisser.Windows.MVC.Controller;
import kernbeisser.Windows.Searchable;
import kernbeisser.Windows.ViewContainer;
import kernbeisser.Windows.ViewContainers.SubWindow;
import lombok.var;
import org.jetbrains.annotations.NotNull;

public class SelectorController<T> extends Controller<SelectorView<T>, SelectorModel<T>> {

  @SafeVarargs
  public SelectorController(
      String title, Collection<T> currentValues, Searchable<T> searchable, Column<T>... columns) {
    super(new SelectorModel<T>(currentValues, title, searchable, columns));
  }

  @Override
  public @NotNull SelectorModel<T> getModel() {
    return model;
  }

  @Override
  public void fillView(SelectorView<T> tSelectorView) {
    tSelectorView.setObjects(model.getCurrentValues());
    tSelectorView.setColumns(model.getColumns());
    tSelectorView.setTitle(model.getTitle());
  }

  public void remove() {

    try {
      var view = getView();
      T value = view.getSelectedValue().orElseThrow(NoSelectionException::new);
      model.getCurrentValues().remove(value);
      view.removeValue(value);
    } catch (NoSelectionException e) {
      getView().messageSelectObjectFirst();
    }
  }

  private ViewContainer selectionWindow;

  public void add() {
    var view = getView();
    if (selectionWindow != null) {
      return;
    }
    SearchBoxController<T> controller =
        new SearchBoxController<T>(model.getSearchable(), model.getColumns());
    ObjectSelectionListener<T> selection =
        e -> {
          view.addValue(e);
          model.getCurrentValues().add(e);
          selectionWindow.requestClose();
          selectionWindow = null;
        };
    controller.addDoubleClickListener(selection);
    selectionWindow = controller.openIn(new SubWindow(view.traceViewContainer()));
  }
}
