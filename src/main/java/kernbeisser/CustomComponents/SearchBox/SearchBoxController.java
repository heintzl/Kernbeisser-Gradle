package kernbeisser.CustomComponents.SearchBox;

import java.util.Arrays;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import kernbeisser.CustomComponents.ObjectTable.Column;
import kernbeisser.CustomComponents.ObjectTable.ObjectSelectionListener;
import kernbeisser.DBConnection.DBConnection;
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
    @Cleanup(value = "commit")
    EntityTransaction et = em.getTransaction();
    et.begin();
    try {
      return (T) em.find(t.getClass(), Tools.getId(t));
    } catch (Exception e) {
      return t;
    }
  }

  public T getSelectedObject() {
    return tryToRefresh(getView().getSelectedObject());
  }

  public void invokeSearch() {
    getView().setObjects(model.getSearchResults(getView().getSearch()));
    runLostSelectionListener();
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

  public void addDoubleClickListener(ObjectSelectionListener<T> action) {
    getView().addDoubleClickListener(action);
  }

  public void addSelectionListener(ObjectSelectionListener<T> action) {
    getView().addSelectionListener(action);
  }

  public void addLostSelectionListener(Runnable r) {
    model.getLostSelectionListener().add(r);
  }

  @Override
  public void fillView(SearchBoxView<T> tSearchBoxView) {
    tSearchBoxView.setColumns(Arrays.asList(model.getColumns()));
    invokeSearch();
  }

  public void setSearch(String s) {
    getView().setSearch(s);
  }
}
