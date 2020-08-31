package kernbeisser.CustomComponents.ObjectTable;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.*;
import java.util.List;
import java.util.function.Function;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import kernbeisser.Exeptions.AccessDeniedException;
import kernbeisser.Useful.Tools;
import org.apache.commons.collections4.EnumerationUtils;
import org.apache.commons.collections4.IterableUtils;
import org.jetbrains.annotations.NotNull;

public class ObjectTable<T> extends JTable implements Iterable<T> {
  private static final Object NO_ACCESS_VALUE = "**********";

  private final ArrayList<ObjectSelectionListener<T>> selectionListeners = new ArrayList<>();
  private final ArrayList<ObjectSelectionListener<T>> doubleClickListeners = new ArrayList<>();

  private T lastSelected = null;

  private List<Column<T>> columns = new ArrayList<>();
  private boolean complex = false;

  private DefaultTableModel model = (DefaultTableModel) super.dataModel;

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
    refreshModel(this.columns, fill);
    addMouseListener(
        new MouseAdapter() {
          @Override
          public void mouseReleased(MouseEvent e) {
            handleCellComponentEvents();
            if (getSelectedRow() == -1) {
              return;
            }
            T selection = getSelectedObject();
            ObjectTable.this.columns.get(getSelectedColumn()).onAction(selection);
            invokeSelectionListeners(selection);
            if (lastSelected != null && lastSelected.equals(selection)) {
              invokeDoubleClickSelectionListeners(selection);
            }
            lastSelected = selection;
          }
        });
    setAutoCreateRowSorter(true);
  }

  private void refreshModel(Collection<Column<T>> columns, Collection<T> objects) {
    model = (DefaultTableModel) createModel(columns, objects);
    setModel(model);
    if (complex)
      for (TableColumn tableColumn : EnumerationUtils.toList(getColumnModel().getColumns())) {
        tableColumn.setCellRenderer(
            (table, value, isSelected, hasFocus, row, column) -> {
              if (value instanceof Component) {
                return (Component) value;
              } else {
                return new JLabel(String.valueOf(value));
              }
            });
      }
  }

  private TableModel createModel(Collection<Column<T>> columns, Collection<T> objects) {
    Object[][] values = Tools.transformToArray(objects, Object[].class, this::collectColumns);
    String[] names = Tools.transformToArray(columns, String.class, Column::getName);
    return new DefaultTableModel(values, names) {
      @Override
      public boolean isCellEditable(int row, int column) {
        return false;
      }

      @Override
      public void setDataVector(Vector dataVector, Vector columnIdentifiers) {
        this.dataVector = nonNullVector(dataVector);
        this.columnIdentifiers = nonNullVector(columnIdentifiers);
        justifyRows(0, getRowCount());
        fireTableStructureChanged();
      }

      private Vector nonNullVector(Vector v) {
        return (v != null) ? v : new Vector();
      }

      private void justifyRows(int from, int to) {
        // Sometimes the DefaultTableModel is subclassed
        // instead of the AbstractTableModel by mistake.
        // Set the number of rows for the case when getRowCount
        // is overridden.
        dataVector.setSize(getRowCount());

        for (int i = from; i < to; i++) {
          if (dataVector.elementAt(i) == null) {
            dataVector.setElementAt(new Vector(), i);
          }
        }
      }
    };
  }

  public T getFromRow(int index) {
    if (index < 0) return null;
    return (T) model.getValueAt(convertRowIndexToModel(index), columns.size());
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

  public void setComplex(boolean v) {
    if (complex == v) return;
    complex = v;
    refreshModel(columns, IterableUtils.toList(this));
  }

  private void handleCellComponentEvents() {
    if (!complex) {
      return;
    }
    Object cell = getValueAt(getSelectedRow(), getSelectedColumn());
    if (!(cell instanceof Component)) {
      return;
    }
    Component com = (Component) cell;
    com.setFocusable(true);
    com.requestFocus();
    if (cell instanceof AbstractButton) {
      ((AbstractButton) cell).doClick();
    }
  }

  private void insertColumn(Column<T> column) {
    columns.add(column);
    refreshModel(columns, IterableUtils.toList(this));
  }

  public void setColumns(List<Column<T>> columns) {
    this.columns = columns;
    refreshModel(columns, IterableUtils.toList(this));
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
    if (index != -1) model.removeRow(index);
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
    Object[] out = new Object[columns.size() + 1];
    for (int i = 0; i < columns.size(); i++) {
      try {
        out[i] = columns.get(i).getValue(value);
      } catch (AccessDeniedException e) {
        out[i] = NO_ACCESS_VALUE;
      }
    }
    out[out.length - 1] = value;
    return out;
  }

  public void setObjects(Collection<T> collection) {
    refreshModel(columns, collection);
  }

  public void setSelectedObject(T value) {
    getSelectionModel().setLeadSelectionIndex(indexOf(value));
  }

  @NotNull
  @Override
  public Iterator<T> iterator() {
    return new Iterator<T>() {
      private int row = 0;

      @Override
      public boolean hasNext() {
        return row < getRowCount();
      }

      @Override
      public T next() {
        return getFromRow(row++);
      };
    };
  }
}
