package kernbeisser.Forms.ObjectView;

import java.awt.*;
import java.util.Collection;
import java.util.function.Consumer;
import javax.swing.*;
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
    openForm(Mode.EDIT);
  }

  void openForm(Mode mode) {
    openForm(searchBoxController.getSelectedObject(), mode);
  }

  public void openForm(T selection, Mode mode) {
    if (mode == Mode.REMOVE) {
      model.getForm().remove(searchBoxController.getSelectedObject());
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
    button.addActionListener(e -> buttonAction.accept(searchBoxController.getSelectedObject()));
    button.setFont(view.getButtonFont());
    button.setEnabled(false);
    view.getExtraButtonPanel().add(button);
  }

  public void addRadioButtons(Collection<JRadioButton> radios) {
    searchBoxController.addExtraRadioOptions(radios);
  }

  public void addCheckBoxes(Collection<JCheckBox> checkBoxes) {
    searchBoxController.addExtraCheckboxes(checkBoxes);
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
