package kernbeisser.Windows.Selector;

import java.util.Collection;
import java.util.function.Consumer;
import kernbeisser.CustomComponents.ObjectTable.Column;
import kernbeisser.CustomComponents.SearchBox.SearchBoxController;
import kernbeisser.Enums.PermissionKey;
import kernbeisser.Windows.MVC.IController;
import kernbeisser.Windows.Searchable;
import kernbeisser.Windows.Window;
import kernbeisser.Windows.WindowImpl.SubWindow;
import org.jetbrains.annotations.NotNull;

public class SelectorController<T> implements IController<SelectorView<T>, SelectorModel<T>> {
  private final SelectorModel<T> model;
  private SelectorView<T> view;

  @SafeVarargs
  public SelectorController(
      String title, Collection<T> currentValues, Searchable<T> searchable, Column<T>... columns) {
    this.model = new SelectorModel<T>(currentValues, title, searchable, columns);
  }

  @Override
  public @NotNull SelectorModel<T> getModel() {
    return model;
  }

  @Override
  public void fillUI() {
    view.setObjects(model.getCurrentValues());
    view.setColumns(model.getColumns());
    view.setTitle(model.getTitle());
  }

  @Override
  public PermissionKey[] getRequiredKeys() {
    return new PermissionKey[0];
  }

  public void remove() {
    model.getCurrentValues().remove(view.getSelectedValue());
    view.removeValue(view.getSelectedValue());
  }

  private Window selectionWindow;

  public void add() {
    if (selectionWindow != null) {
      return;
    }
    SearchBoxController<T> controller =
        new SearchBoxController<T>(model.getSearchable(), model.getColumns());
    Consumer<T> selection =
        e -> {
          view.addValue(e);
          model.getCurrentValues().add(e);
          selectionWindow.back();
          selectionWindow = null;
        };
    controller.addDoubleClickListener(selection);
    controller.addSelectionListener(selection);
    selectionWindow = controller.openAsWindow(view.getWindow(), SubWindow::new);
  }
}
