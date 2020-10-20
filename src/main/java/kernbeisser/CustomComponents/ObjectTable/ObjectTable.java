package kernbeisser.CustomComponents.ObjectTable;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.*;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import javax.swing.*;
import javax.swing.RowFilter;
import javax.swing.table.*;
import kernbeisser.Exeptions.PermissionKeyRequiredException;
import kernbeisser.Useful.Tools;
import org.jetbrains.annotations.NotNull;

public class ObjectTable<T> extends JTable implements Iterable<T> {
  private static final Object NO_ACCESS_VALUE = "**********";

  private final ArrayList<ObjectSelectionListener<T>> selectionListeners = new ArrayList<>();
  private final ArrayList<ObjectSelectionListener<T>> doubleClickListeners = new ArrayList<>();

  private final List<T> objects = new ArrayList<>();
  private T lastSelected = null;

  private List<Column<T>> columns = new ArrayList<>();

  private DefaultTableModel model = (DefaultTableModel) super.dataModel;

  private kernbeisser.CustomComponents.ObjectTable.RowFilter<T> rowFilter = e -> true;

  public ObjectTable(Collection<Column<T>> columns) {
    this(Collections.emptyList(), columns);
  }

  @SafeVarargs
  public ObjectTable(Collection<T> fill, Column<T>... columns) {
    this(fill, Arrays.asList(columns));
  }

  @SafeVarargs
  public ObjectTable(Column<T>... columns) {
    this(Arrays.asList(columns));
  }

  ObjectTable(Collection<T> fill, Collection<Column<T>> columns) {
    this.columns.addAll(columns);
    this.objects.addAll(fill);
    refreshModel();
    addMouseListener(
        new MouseAdapter() {
          @Override
          public void mouseReleased(MouseEvent e) {
            if (getSelectedRow() == -1) {
              return;
            }
            T selection = getSelectedObject();
            ObjectTable.this
                .columns
                .get(convertColumnIndexToModel(getSelectedColumn()))
                .onAction(selection);
            invokeSelectionListeners(selection);
            if (lastSelected != null && lastSelected.equals(selection)) {
              invokeDoubleClickSelectionListeners(selection);
            }
            lastSelected = selection;
          }
        });
    setRowSorter(createRowSorter());
  }

  private void refreshModel() {
    model = (DefaultTableModel) createModel(columns, objects);
    setRowSorter(null);
    setModel(model);
    for (int i = 0; i < columns.size(); i++) {
      Column<T> column = columns.get(i);
      TableColumn tableColumn = getColumnModel().getColumn(convertColumnIndexToModel(i));
      tableColumn.setCellRenderer(column.getRenderer());
      column.adjust(tableColumn);
    }
    setRowSorter(createRowSorter());
  }

  private TableModel createModel(Collection<Column<T>> columns, Collection<T> objects) {
    objects.forEach(
        e -> {
          if (e == null)
            throw new NullPointerException("cannot create model with null paramenters");
        });
    Object[][] values = Tools.transformToArray(objects, Object[].class, this::collectColumns);
    String[] names = Tools.transformToArray(columns, String.class, Column::getName);
    return new ObjectTableModel(values, names);
  }

  public T getFromRow(int index) {
    if (index < 0) return null;
    return objects.get(super.convertRowIndexToModel(index));
  }

  private void invokeSelectionListeners(T t) {
    for (ObjectSelectionListener<T> listener : selectionListeners) {
      listener.selected(t);
    }
  }

  private void invokeDoubleClickSelectionListeners(T t) {
    for (ObjectSelectionListener<T> listener : doubleClickListeners) {
      listener.selected(t);
    }
  }

  private void insertColumn(Column<T> column) {
    columns.add(column);
    refreshModel();
  }

  public void setColumns(List<Column<T>> columns) {
    this.columns = columns;
    refreshModel();
  }

  public void setColumns(Collection<Column<T>> columns) {
    setColumns(new ArrayList<>(columns));
  }

  @SafeVarargs
  public final void setColumns(Column<T>... columns) {
    setColumns(Arrays.asList(columns));
  }

  public void addColumn(Column<T> column) {
    insertColumn(column);
  }

  public boolean contains(T t) {
    for (T compare : this) {
      if (compare.equals(t)) return true;
    }
    return false;
  }

  public T getSelectedObject() {
    return getFromRow(getSelectedRow());
  }

  public T get(Function<T, Boolean> function) {
    for (T object : this) {
      if (function.apply(object)) {
        return object;
      }
    }
    return null;
  }

  public void addSelectionListener(ObjectSelectionListener<T> listener) {
    selectionListeners.add(listener);
  }

  public void addAll(Collection<T> in) {
    in.forEach(this::add);
  }

  public void add(T in) {
    if (in == null) {
      throw new NullPointerException("Cannot add null object to ObjectTable");
    }
    model.addRow(collectColumns(in));
  }

  public void remove(T t) {
    int index = indexOf(t);
    if (index != -1) {
      model.removeRow(index);
      objects.remove(index);
    }
  }

  private int indexOf(T t) {
    int c = 0;
    for (T compare : this) {
      if (compare.equals(t)) return c;
      else c++;
    }
    return -1;
  }

  public Iterable<T> getItems() {
    return this;
  }

  public void clear() {
    this.model.getDataVector().clear();
  }

  private Object[] collectColumns(T value) {
    Object[] out = new Object[columns.size()];
    for (int i = 0; i < columns.size(); i++) {
      try {
        out[i] = columns.get(i).getValue(value);
      } catch (PermissionKeyRequiredException e) {
        out[i] = NO_ACCESS_VALUE;
      }
    }
    return out;
  }

  public void setObjects(Collection<T> collection) {
    this.objects.clear();
    this.objects.addAll(collection);
    refreshModel();
  }

  public void replace(T value, T newValue) {
    int index = objects.indexOf(value);
    objects.set(index, newValue);
    model.removeRow(index);
    model.insertRow(index, collectColumns(newValue));
    repaint();
  }

  public void setSelectedObject(T value) {
    getSelectionModel().setLeadSelectionIndex(indexOf(value));
  }

  @NotNull
  @Override
  public Iterator<T> iterator() {
    return objects.iterator();
  }

  public void setRowFilter(kernbeisser.CustomComponents.ObjectTable.RowFilter<T> rowFilter) {
    this.rowFilter = rowFilter;
    ((TableRowSorter<?>) getRowSorter()).sort();
  }

  private TableRowSorter<?> createRowSorter() {
    TableRowSorter<?> out = new TableRowSorter<>(model);
    out.setRowFilter(
        new RowFilter<Object, Integer>() {
          @Override
          public synchronized boolean include(Entry<?, ? extends Integer> entry) {
            try {
              return rowFilter.isDisplayed(objects.get(entry.getIdentifier()));
            } catch (IndexOutOfBoundsException e) {
              System.out.println(entry.getIdentifier());
              return false;
            }
          }
        });
    return out;
  }

  public Collection<T> getSelectedObjects() {
    int[] rows = getSelectedRows();
    ArrayList<T> out = new ArrayList<>(rows.length);
    for (int row : rows) {
      out.add(getFromRow(row));
    }
    return out;
  }

  public T[] getSelectedObjects(T[] pattern) {
    int[] rows = getSelectedRows();
    T[] out = Arrays.copyOf(pattern, rows.length);
    for (int i = 0; i < rows.length; i++) {
      out[i] = getFromRow(rows[i]);
    }
    return out;
  }

  public void removeIf(Predicate<T> predicate) {
    objects.removeIf(predicate);
    refreshModel();
  }
}
