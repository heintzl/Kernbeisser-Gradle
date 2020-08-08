package kernbeisser.Windows.ObjectView;

import kernbeisser.CustomComponents.ObjectTable.Column;
import kernbeisser.CustomComponents.SearchBox.SearchBoxController;
import kernbeisser.CustomComponents.SearchBox.SearchBoxView;
import kernbeisser.Enums.PermissionKey;
import kernbeisser.Windows.MVC.Controller;
import kernbeisser.Windows.MVC.Linked;
import kernbeisser.Windows.MaskLoader;
import kernbeisser.Windows.Searchable;
import org.jetbrains.annotations.NotNull;

public class ObjectViewController<T> implements Controller<ObjectViewView<T>, ObjectViewModel<T>> {
  private final ObjectViewModel<T> model;
  private ObjectViewView<T> view;

  @Linked
  private final SearchBoxController<T> searchBoxController;

  private boolean openWindow = false;

  public ObjectViewController(
      MaskLoader<T> loader, Searchable<T> items, boolean copyAdd, Column<T>... columns) {
    searchBoxController = new SearchBoxController<T>(items, columns);
    model = new ObjectViewModel<>(loader, items, copyAdd);
  }

  void select() {
    checkSelectedObject();
  }

  void checkSelectedObject(){
    view.setAddAvailable(!openWindow);
    if (openWindow || searchBoxController.getSelectedObject() == null) {
      view.setEditAvailable(false);
      view.setRemoveAvailable(false);
    } else {
      view.setEditAvailable(true);
      view.setRemoveAvailable(true);
    }
  }

  void edit() {
    if (openWindow) return;
    model
        .openEdit(view.getWindow(), searchBoxController.getSelectedObject())
        .addCloseEventListener(
            e -> {
              search();
              openWindow = false;
              checkSelectedObject();
            });
    view.setAddAvailable(false);
    openWindow = true;
    checkSelectedObject();
  }

  void add() {
    if (openWindow) return;
    model
        .openAdd(view.getWindow(), searchBoxController.getSelectedObject())
        .addCloseEventListener(
            e -> {
              search();
              openWindow = false;
              checkSelectedObject();
            });
    view.setAddAvailable(false);
    openWindow = true;
    checkSelectedObject();
  }

  void delete() {
    if (view.commitDelete()) {
      model.remove(searchBoxController.getSelectedObject());
    }
    search();
    refresh();
  }

  public void refresh() {
    checkSelectedObject();
  }

  @Override
  public @NotNull ObjectViewModel<T> getModel() {
    return model;
  }

  @Override
  public void fillUI() {
    searchBoxController.addSelectionListener(e -> select());
    searchBoxController.addDoubleClickListener(e -> edit());
    searchBoxController.addLostSelectionListener(this::checkSelectedObject);
    checkSelectedObject();
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
