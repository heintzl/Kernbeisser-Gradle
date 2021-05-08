package kernbeisser.Forms.ObjectView;

import kernbeisser.CustomComponents.ObjectTable.Column;
import kernbeisser.CustomComponents.SearchBox.SearchBoxController;
import kernbeisser.CustomComponents.SearchBox.SearchBoxView;
import kernbeisser.Enums.Mode;
import kernbeisser.Exeptions.NoSelectionException;
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
    view.setAddAvailable(model.isAddAvailable());
    if (searchBoxController.getSelectedObject() == null) {
      view.setEditAvailable(false);
      view.setRemoveAvailable(false);
    } else {
      view.setEditAvailable(model.isEditAvailable());
      view.setRemoveAvailable(model.isRemoveAvailable());
    }
  }

  void edit() {
    try {
      openForm(Mode.EDIT);
    } catch (NoSelectionException e) {
      getView().messageSelectObjectFirst();
    }
  }

  void openForm(Mode mode) throws NoSelectionException {
    openForm(searchBoxController.getSelectedObject().orElseThrow(NoSelectionException::new), mode);
  }

  public void openForm(T selection, Mode mode) {
    if (mode == Mode.REMOVE) {
      model.getForm().remove(selection);
      searchBoxController.invokeSearch();
      return;
    }
    model.setCurrentMode(mode);
    if (mode == Mode.EDIT || (mode == Mode.ADD && model.isCopyValuesToAdd() && selection != null))
      model.getForm().getObjectContainer().setSource(selection);
    else model.getForm().getObjectContainer().setSource(model.getForm().defaultFactory().get());
    FormEditorController<T> formEditorController =
        new FormEditorController<T>(model.getForm(), model::submit);
    formEditorController.setMode(mode);
    formEditorController
        .withCloseEvent(searchBoxController::invokeSearch)
        .openIn(new SubWindow(getView().traceViewContainer()));
    refreshButtonStates();
  }

  void add() {
    try {
      openForm(Mode.ADD);
    } catch (NoSelectionException e) {
      getView().messageSelectObjectFirst();
    }
  }

  void remove() {
    try {
      openForm(Mode.REMOVE);
    } catch (NoSelectionException e) {
      getView().messageSelectObjectFirst();
    }
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
