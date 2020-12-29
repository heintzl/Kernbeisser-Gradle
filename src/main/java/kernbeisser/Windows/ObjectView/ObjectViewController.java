package kernbeisser.Windows.ObjectView;

import kernbeisser.CustomComponents.ObjectTable.Column;
import kernbeisser.CustomComponents.SearchBox.SearchBoxController;
import kernbeisser.CustomComponents.SearchBox.SearchBoxView;
import kernbeisser.Enums.Mode;
import kernbeisser.Enums.PermissionKey;
import kernbeisser.Windows.MVC.Controller;
import kernbeisser.Windows.MVC.Linked;
import kernbeisser.Windows.MaskLoader;
import kernbeisser.Windows.Searchable;
import lombok.var;
import org.jetbrains.annotations.NotNull;

public class ObjectViewController<T> extends Controller<ObjectViewView<T>, ObjectViewModel<T>> {

  @Linked private final SearchBoxController<T> searchBoxController;

  @Linked private String title;
  private boolean openWindow = false;
  private boolean addAvailable,removeAvailable,editAvailable;

  public ObjectViewController(
      String title,
      MaskLoader<T> loader,
      Searchable<T> items,
      boolean copyAdd,
      Column<T>... columns) {
    super(new ObjectViewModel<>(loader, items, copyAdd));
    this.title = title;
    searchBoxController = new SearchBoxController<T>(items, columns);
    addAvailable = getModel().isAvailable(Mode.ADD);
    editAvailable = getModel().isAvailable(Mode.EDIT);
    removeAvailable = getModel().isAvailable(Mode.REMOVE);
  }

  void select() {
    checkSelectedObject();
  }

  void checkSelectedObject() {
    var view = getView();
    view.setAddAvailable(!openWindow && addAvailable);
    if (openWindow || searchBoxController.getSelectedObject() == null) {
      view.setEditAvailable(false);
      view.setRemoveAvailable(false);
    } else {
      view.setEditAvailable(editAvailable);
      view.setRemoveAvailable(removeAvailable);
    }
  }

  void edit() {
    var view = getView();
    if (openWindow) return;
    model.openEdit(
        view.traceViewContainer(),
        searchBoxController.getSelectedObject(),
        () -> {
          search();
          openWindow = false;
          checkSelectedObject();
        });
    view.setAddAvailable(false);
    openWindow = true;
    checkSelectedObject();
  }

  void add() {
    var view = getView();
    if (openWindow) return;
    model.openAdd(
        view.traceViewContainer(),
        searchBoxController.getSelectedObject(),
        () -> {
          search();
          openWindow = false;
          checkSelectedObject();
        });
    view.setAddAvailable(false);
    openWindow = true;
    checkSelectedObject();
  }

  void remove() {
    var view = getView();
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
  public void fillView(ObjectViewView<T> tObjectViewView) {
    searchBoxController.addSelectionListener(e -> select());
    searchBoxController.addDoubleClickListener(e -> edit());
    searchBoxController.addLostSelectionListener(this::checkSelectedObject);
    getView().setAddAvailable(addAvailable);
    checkSelectedObject();
  }





  public SearchBoxView<T> getSearchBoxView() {
    return searchBoxController.getView();
  }

  public void setSearch(String s) {
    searchBoxController.setSearch(s);
  }

  public void search() {
    searchBoxController.invokeSearch();
  }
}
