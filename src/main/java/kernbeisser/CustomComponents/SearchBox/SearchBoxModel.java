package kernbeisser.CustomComponents.SearchBox;

import java.util.ArrayList;
import java.util.Collection;
import kernbeisser.CustomComponents.ObjectTable.Column;
import kernbeisser.Enums.Setting;
import kernbeisser.Windows.MVC.IModel;
import kernbeisser.Windows.Searchable;

public class SearchBoxModel<T> implements IModel<SearchBoxController<T>> {
  private final Searchable<T> searchable;
  private final Column<T>[] columns;

  private T lastSelectedObject = null;

  private final ArrayList<Runnable> lostSelectionListener = new ArrayList<>();

  SearchBoxModel(Searchable<T> searchable, Column<T>[] columns) {
    this.searchable = searchable;
    this.columns = columns;
  }

  Collection<T> getSearchResults(String s) {
    return searchable.search(s, Setting.DEFAULT_MAX_SEARCH.getIntValue());
  }

  public ArrayList<Runnable> getLostSelectionListener() {
    return lostSelectionListener;
  }

  public T getLastSelectedObject() {
    return lastSelectedObject;
  }

  public void setLastSelectedObject(T lastSelectedObject) {
    this.lastSelectedObject = lastSelectedObject;
  }

  public Column<T>[] getColumns() {
    return columns;
  }
}
