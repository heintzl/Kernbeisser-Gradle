package kernbeisser.Windows.ObjectView;

import kernbeisser.CustomComponents.ObjectTable.Column;
import kernbeisser.CustomComponents.SearchBox.SearchBoxController;
import kernbeisser.CustomComponents.SearchBox.SearchBoxView;
import kernbeisser.Enums.PermissionKey;
import kernbeisser.Windows.Controller;
import kernbeisser.Windows.MaskLoader;
import kernbeisser.Windows.Searchable;
import org.jetbrains.annotations.NotNull;

public class ObjectViewController<T> implements Controller<ObjectViewView<T>, ObjectViewModel<T>> {
  private final ObjectViewModel<T> model;
  private final ObjectViewView<T> view;

  private final SearchBoxController<T> searchBoxController;

  private boolean openWindow = false;

  public ObjectViewController(
      MaskLoader<T> loader, Searchable<T> items, boolean copyAdd, Column<T>... columns) {
    searchBoxController = new SearchBoxController<T>(items, columns);
    searchBoxController.initView();
    searchBoxController.addSelectionListener(e -> select());
    searchBoxController.addDoubleClickListener(e -> edit());
    searchBoxController.addLostSelectionListener(this::putItems);

    model = new ObjectViewModel<>(loader, items, copyAdd);
    view = new ObjectViewView<>(this);
  }

  void select() {
    if (openWindow) {
      return;
    }
    view.setEditAvailable(true);
    view.setRemoveAvailable(true);
  }

  private void putItems() {
    view.setEditAvailable(false);
    view.setRemoveAvailable(false);
  }

  void edit() {
    if (openWindow) return;
    model
        .openEdit(view.getWindow(), searchBoxController.getSelectedObject())
        .addCloseEventListener(
            e -> {
              search();
              openWindow = false;
              view.setAddAvailable(true);
            });
    view.setAddAvailable(false);
    putItems();
    openWindow = true;
  }

  void add() {
    if (openWindow) return;
    model
        .openAdd(view.getWindow(), searchBoxController.getSelectedObject())
        .addCloseEventListener(
            e -> {
              search();
              openWindow = false;
              view.setAddAvailable(true);
            });
    putItems();
    view.setAddAvailable(false);
    openWindow = true;
  }

  void delete() {
    if (view.commitDelete()) {
      model.remove(searchBoxController.getSelectedObject());
    }
    search();
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
  public PermissionKey[] getRequiredKeys() {
    return new PermissionKey[0];
  }

  public SearchBoxView<T> getSearchBoxView() {
    return searchBoxController.getView();
  }

  public void setSearch(String s) {
    searchBoxController.setSearch(s);
  }

  public void search() {
    T t = searchBoxController.getSelectedObject();
    searchBoxController.refreshLoadSolutions();
    if (t != null) {
      searchBoxController.setSelectedObject(t);
    }
  }
}
