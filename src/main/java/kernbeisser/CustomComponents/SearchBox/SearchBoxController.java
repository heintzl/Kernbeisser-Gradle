package kernbeisser.CustomComponents.SearchBox;

import java.util.Arrays;
import java.util.function.Consumer;
import javax.persistence.EntityManager;
import kernbeisser.CustomComponents.ObjectTable.Column;
import kernbeisser.DBConnection.DBConnection;
import kernbeisser.Enums.PermissionKey;
import kernbeisser.Useful.Tools;
import kernbeisser.Windows.MVC.Controller;
import kernbeisser.Windows.Searchable;
import lombok.Cleanup;
import org.jetbrains.annotations.NotNull;

public class SearchBoxController<T> extends Controller<SearchBoxView<T>, SearchBoxModel<T>> {

  @SafeVarargs
  public SearchBoxController(Searchable<T> searchFunction, Column<T>... columns) {
    super(new SearchBoxModel<>(searchFunction, columns));
  }

  public T tryToRefresh(T t) {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    try {
      return (T) em.find(t.getClass(), Tools.getId(t));
    } catch (Exception e) {
      return t;
    }
  }

  public T getSelectedObject() {
    return tryToRefresh(getView().getSelectedObject());
  }

  public void search() {
    Object lastId = getSelectedObject() != null ? Tools.getId(getSelectedObject()) : null;
    getView().setObjects(model.getValues(getView().getSearch()));
    if (lastId != null && !getView().setSelectedObjectId(lastId)) {
      runLostSelectionListener();
    }
  }

  void select() {
    if (getView().getSelectedObject() == null) {
      return;
    }
    if (model.getLastSelectedObject() != null
        && getView().getSelectedObject().equals(model.getLastSelectedObject())) {
      runDoubleClickListener(getSelectedObject());
    }
    runSelectionListener(getSelectedObject());
    model.setLastSelectedObject(getView().getSelectedObject());
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
  public void fillView(SearchBoxView<T> tSearchBoxView) {
    tSearchBoxView.setColumns(Arrays.asList(model.getColumns()));
    search();
  }

  @Override
  public PermissionKey[] getRequiredKeys() {
    return new PermissionKey[0];
  }

  public void refreshLoadSolutions() {
    search();
  }

  public void setSearch(String s) {
    getView().setSearch(s);
  }

  public void setSelectedObject(T t) {
    getView().setSelectedObject(t);
  }
}
