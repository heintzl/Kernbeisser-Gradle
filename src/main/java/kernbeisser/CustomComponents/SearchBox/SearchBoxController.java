package kernbeisser.CustomComponents.SearchBox;

import java.util.Arrays;
import java.util.function.Consumer;
import kernbeisser.CustomComponents.ObjectTable.Column;
import kernbeisser.Enums.PermissionKey;
import kernbeisser.Useful.Tools;
import kernbeisser.Windows.MVC.IController;
import kernbeisser.Windows.Searchable;
import org.jetbrains.annotations.NotNull;

public class SearchBoxController<T> implements IController<SearchBoxView<T>, SearchBoxModel<T>> {
  private SearchBoxView<T> view;
  private final SearchBoxModel<T> model;

  @SafeVarargs
  public SearchBoxController(Searchable<T> searchFunction, Column<T>... columns) {
    this.model = new SearchBoxModel<>(searchFunction, columns);
  }

  public T getSelectedObject() {
    return view.getSelectedObject();
  }

  public void search() {
    Object lastId = getSelectedObject() != null ? Tools.getId(getSelectedObject()) : null;
    view.setObjects(model.getValues(view.getSearch()));
    if (!view.setSelectedObjectId(lastId)) {
      runLostSelectionListener();
    }
  }

  void select() {
    if (view.getSelectedObject() == null) {
      return;
    }
    if (model.getLastSelectedObject() != null
        && view.getSelectedObject().equals(model.getLastSelectedObject())) {
      runDoubleClickListener(view.getSelectedObject());
    }
    runSelectionListener(view.getSelectedObject());
    model.setLastSelectedObject(view.getSelectedObject());
  }

  private void runDoubleClickListener(T t) {
    for (Consumer<T> consumer : model.getDoubleClickListener()) {
      consumer.accept(t);
    }
  }

  private void runSelectionListener(T t) {
    for (Consumer<T> consumer : model.getSelectionListener()) {
      consumer.accept(t);
    }
  }

  private void runLostSelectionListener() {
    for (Runnable runnable : model.getLostSelectionListener()) {
      runnable.run();
    }
  }

  @Override
  public @NotNull SearchBoxModel<T> getModel() {
    return model;
  }

  @Override
  public void fillUI() {
    view.setColumns(Arrays.asList(model.getColumns()));
    search();
  }

  public void addDoubleClickListener(Consumer<T> action) {
    model.getDoubleClickListener().add(action);
  }

  public void addSelectionListener(Consumer<T> action) {
    model.getSelectionListener().add(action);
  }

  public void addLostSelectionListener(Runnable r) {
    model.getLostSelectionListener().add(r);
  }

  @Override
  public PermissionKey[] getRequiredKeys() {
    return new PermissionKey[0];
  }

  public void refreshLoadSolutions() {
    search();
  }

  public void setSearch(String s) {
    view.setSearch(s);
  }

  public void setSelectedObject(T t) {
    view.setSelectedObject(t);
  }
}
