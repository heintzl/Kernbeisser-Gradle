package kernbeisser.CustomComponents.SearchBox;

import java.awt.*;
import java.util.*;
import java.util.function.Consumer;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.swing.*;
import kernbeisser.CustomComponents.ObjectTable.Column;
import kernbeisser.CustomComponents.ObjectTable.ObjectSelectionListener;
import kernbeisser.DBConnection.DBConnection;
import kernbeisser.Useful.Tools;
import kernbeisser.Windows.MVC.Controller;
import kernbeisser.Windows.Searchable;
import lombok.Cleanup;
import lombok.var;
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

  public Optional<T> getSelectedObject() {
    return getView().getSelectedObject().map(this::tryToRefresh);
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

  public void addExtraComponents(Collection<JComponent> components) {
    JPanel panel = getView().getExtraOptionsPanel();
    for (JComponent c : components) {
      panel.add(c);
    }
  }

  public void modifyNamedComponent(String name, Consumer<Component> modifier) {
    var searchOptions = getView().getExtraOptionsPanel();
    for (var component : searchOptions.getComponents()) {
      Optional.ofNullable(component.getName())
          .ifPresent(
              s -> {
                if (s.equals(name)) {
                  modifier.accept(component);
                }
              });
    }
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
