package kernbeisser.CustomComponents.SearchBox;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Consumer;

import kernbeisser.CustomComponents.ObjectTable.Column;
import kernbeisser.Enums.Setting;
import kernbeisser.Windows.MVC.Model;
import kernbeisser.Windows.Searchable;

public class SearchBoxModel<T> implements Model<SearchBoxController<T>> {
  private final Searchable<T> searchable;
  private final Column<T>[] columns;

  private T lastSelectedObject = null;

  private final ArrayList<Consumer<T>> selectionListener = new ArrayList<>();
  private final ArrayList<Consumer<T>> doubleClickListener = new ArrayList<>();
  private final ArrayList<Runnable> lostSelectionListener = new ArrayList<>();

  SearchBoxModel(Searchable<T> searchable, Column<T>[] columns) {
    this.searchable = searchable;
    this.columns = columns;
  }

  Collection<T> getValues(String s) {
    return searchable.search(s, Setting.DEFAULT_MAX_SEARCH.getIntValue());
  }

  public ArrayList<Runnable> getLostSelectionListener() {
    return lostSelectionListener;
  }

  public ArrayList<Consumer<T>> getDoubleClickListener() {
    return doubleClickListener;
  }

  public ArrayList<Consumer<T>> getSelectionListener() {
    return selectionListener;
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
