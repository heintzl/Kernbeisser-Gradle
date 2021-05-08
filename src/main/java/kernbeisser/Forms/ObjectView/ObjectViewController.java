package kernbeisser.Forms.ObjectView;

import java.awt.*;
import java.util.Collection;
import java.util.function.Consumer;
import javax.swing.*;
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
import lombok.Getter;
import lombok.var;
import org.jetbrains.annotations.NotNull;

public class ObjectViewController<T> extends Controller<ObjectViewView<T>, ObjectViewModel<T>> {

  @Linked @Getter private SearchBoxController<T> searchBoxController;

  @Linked private String title;

  public ObjectViewController(
      String title,
      FormController<?, ?, T> controller,
      Searchable<T> items,
      boolean copyAdd,
      Column<T>... columns) {
    this(title, controller, copyAdd);
    model.setItemSupplier(items);
    searchBoxController = new SearchBoxController<T>(items, columns);
  }

  public ObjectViewController(String title, FormController<?, ?, T> controller, boolean copyAdd) {
    super(new ObjectViewModel<>(controller, copyAdd));
    this.title = title;
  }

  public void setSearchBoxController(Searchable<T> items, Column<T>... columns) {
    model.setItemSupplier(items);
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
      setExtraButtonsAvailable(false);
    } else {
      view.setEditAvailable(model.isEditAvailable());
      view.setRemoveAvailable(model.isRemoveAvailable());
      setExtraButtonsAvailable(true);
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

  public void setExtraButtonsAvailable(boolean available) {
    for (Component c : getView().getExtraButtonPanel().getComponents()) {
      if (c instanceof JButton) {
        c.setEnabled(available);
      }
    }
  }

  public void addButton(JButton button, Consumer<T> buttonAction) {
    ObjectViewView view = getView();
    button.addActionListener(
        e -> buttonAction.accept(searchBoxController.getSelectedObject().orElse(null)));
    button.setFont(view.getButtonFont());
    button.setEnabled(false);
    view.getExtraButtonPanel().add(button);
  }

  public void addComponents(Collection<JComponent> components) {
    searchBoxController.addExtraComponents(components);
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
