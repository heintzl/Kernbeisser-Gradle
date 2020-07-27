package kernbeisser.CustomComponents.SearchBox;

import java.util.Arrays;
import java.util.function.Consumer;
import kernbeisser.CustomComponents.ObjectTable.Column;
import kernbeisser.Enums.PermissionKey;
import kernbeisser.Windows.Controller;
import kernbeisser.Windows.Searchable;
import org.jetbrains.annotations.NotNull;

public class SearchBoxController<T> implements Controller<SearchBoxView<T>, SearchBoxModel<T>> {

  private final SearchBoxView<T> view;
  private final SearchBoxModel<T> model;

  @SafeVarargs
  public SearchBoxController(Searchable<T> searchFunction, Column<T>... columns) {
    this.model = new SearchBoxModel<>(searchFunction);
    this.view = new SearchBoxView<>(this);
    view.setColumns(Arrays.asList(columns));
    search();
  }

  public T getSelectedObject() {
    return view.getSelectedObject();
  }

  public void search() {
    view.setObjects(model.getValues(view.getSearch()));
    if (model.getLastSelectedObject() == null) {
      view.setSelectedObject(model.getLastSelectedObject());
    }
    runLostSelectionListener();
  }

  void select() {
    if (view.getSelectedObject() == null) {
      return;
    }
  }

  public void search(){
        view.setObjects(model.getValues(view.getSearch()));
        if(model.getLastSelectedObject()!=null){
            view.setSelectedObject(model.getLastSelectedObject());
        }
        runLostSelectionListener();
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
  public @NotNull SearchBoxView<T> getView() {
    return view;
  }

  @Override
  public @NotNull SearchBoxModel<T> getModel() {
    return model;
  }

  @Override
  public void fillUI() {}

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
}
