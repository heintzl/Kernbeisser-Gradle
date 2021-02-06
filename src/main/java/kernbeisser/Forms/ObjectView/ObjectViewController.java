package kernbeisser.Forms.ObjectView;

import kernbeisser.CustomComponents.ObjectTable.Column;
import kernbeisser.CustomComponents.SearchBox.SearchBoxController;
import kernbeisser.CustomComponents.SearchBox.SearchBoxView;
import kernbeisser.Enums.Mode;
import kernbeisser.Forms.FormController;
import kernbeisser.Forms.FormEditor.FormEditorController;
import kernbeisser.Windows.MVC.Controller;
import kernbeisser.Windows.MVC.Linked;
import kernbeisser.Windows.Searchable;
import kernbeisser.Windows.ViewContainers.SubWindow;
import lombok.var;
import org.jetbrains.annotations.NotNull;

public class ObjectViewController<T> extends Controller<ObjectViewView<T>, ObjectViewModel<T>> {

  @Linked private final SearchBoxController<T> searchBoxController;

  @Linked private String title;
  private boolean openWindow = false;

  public ObjectViewController(
      String title,
      FormController<?, ?, T> controller,
      Searchable<T> items,
      boolean copyAdd,
      Column<T>... columns) {
    super(new ObjectViewModel<>(controller, items, copyAdd));
    this.title = title;
    searchBoxController = new SearchBoxController<T>(items, columns);
  }

  void select() {
    refreshButtonStates();
  }

  void refreshButtonStates() {
    var view = getView();
    view.setAddAvailable(!openWindow && model.isAddAvailable());
    if (openWindow || searchBoxController.getSelectedObject() == null) {
      view.setEditAvailable(false);
      view.setRemoveAvailable(false);
    } else {
      view.setEditAvailable(model.isEditAvailable());
      view.setRemoveAvailable(model.isRemoveAvailable());
    }
  }

  void edit() {
    openForm(Mode.EDIT);
  }

  void openForm(Mode mode) {
    openForm(searchBoxController.getSelectedObject(), mode);
  }

  public void openForm(T selection, Mode mode) {
    if (openWindow) return;
    if (model.getCurrentMode() == Mode.REMOVE) {
      model.getForm().remove(searchBoxController.getSelectedObject());
      return;
    }
    model.setCurrentMode(mode);
    if (mode == Mode.EDIT || (mode == Mode.ADD && model.isCopyValuesToAdd()))
      model.getForm().getObjectContainer().setSource(selection);
    else model.getForm().getObjectContainer().setSource(model.getForm().defaultFactory().get());
    FormEditorController<T> formEditorController =
        new FormEditorController<T>(model.getForm(), model::submit);
    formEditorController.setMode(mode);
    formEditorController
        .withCloseEvent(() -> {
          openWindow = false;
          searchBoxController.invokeSearch();
        })
        .openIn(new SubWindow(getView().traceViewContainer()));
    openWindow = true;
    refreshButtonStates();
  }

  void add() {
    openForm(Mode.ADD);
  }

  void remove() {
    openForm(Mode.REMOVE);
  }

  @Override
  public @NotNull ObjectViewModel<T> getModel() {
    return model;
  }

  @Override
  public void fillView(ObjectViewView<T> tObjectViewView) {
    searchBoxController.addSelectionListener(e -> select());
    searchBoxController.addDoubleClickListener(e -> edit());
    searchBoxController.addLostSelectionListener(this::refreshButtonStates);
    refreshButtonStates();
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
