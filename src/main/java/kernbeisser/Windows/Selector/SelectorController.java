package kernbeisser.Windows.Selector;

import java.util.Collection;
import java.util.function.Consumer;
import kernbeisser.CustomComponents.ObjectTable.Column;
import kernbeisser.CustomComponents.SearchBox.SearchBoxController;
import kernbeisser.Enums.PermissionKey;
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

  @Override
  public PermissionKey[] getRequiredKeys() {
    return new PermissionKey[0];
  }

  public void remove() {
    var view = getView();
    model.getCurrentValues().remove(view.getSelectedValue());
    view.removeValue(view.getSelectedValue());
  }

  private ViewContainer selectionWindow;

  public void add() {
    var view = getView();
    if (selectionWindow != null) {
      return;
    }
    SearchBoxController<T> controller =
        new SearchBoxController<T>(model.getSearchable(), model.getColumns());
    Consumer<T> selection =
        e -> {
          view.addValue(e);
          model.getCurrentValues().add(e);
          selectionWindow.requestClose();
          selectionWindow = null;
        };
    controller.addDoubleClickListener(selection);
    controller.addSelectionListener(selection);
    selectionWindow = controller.openIn(new SubWindow(view.traceViewContainer()));
  }
}
