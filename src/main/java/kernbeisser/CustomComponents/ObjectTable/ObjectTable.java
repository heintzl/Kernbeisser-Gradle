package kernbeisser.CustomComponents.ObjectTable;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.*;
import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import javax.swing.*;
import javax.swing.RowFilter;
import javax.swing.table.*;
import jiconfont.icons.font_awesome.FontAwesome;
import jiconfont.swing.IconFontSwing;
import kernbeisser.Exeptions.PermissionKeyRequiredException;
import kernbeisser.Useful.DocumentChangeListener;
import kernbeisser.Useful.Tools;
import lombok.var;
import org.jetbrains.annotations.NotNull;

public class ObjectTable<T> extends JTable implements Iterable<T> {
  private static final Object NO_ACCESS_VALUE = "**********";

  private final Map<ObjectSelectionListener<T>, Boolean> selectionListeners = new HashMap<>();

  private final List<T> objects = new ArrayList<>();

  private List<Column<T>> columns = new ArrayList<>();

  private DefaultTableModel model = (DefaultTableModel) super.dataModel;

  private final kernbeisser.CustomComponents.ObjectTable.RowFilter<T> DEFAULT_ROW_FILTER =
      e -> true;

  private static final RowFilter<Object, Integer> DEFAULT_SWING_ROW_FILTER =
      new RowFilter<Object, Integer>() {
        @Override
        public boolean include(Entry<?, ? extends Integer> entry) {
          return true;
        }
      };

  private kernbeisser.CustomComponents.ObjectTable.RowFilter<T> rowFilter = DEFAULT_ROW_FILTER;

  private RowFilter<Object, Integer> swingRowFilter = DEFAULT_SWING_ROW_FILTER;

  public ObjectTable(Collection<Column<T>> columns) {
    this(Collections.emptyList(), columns);
  }

  private final Map<Column<T>, JTextField> standardColumnFilters = new HashMap<>();

  private final Map<Column<T>, JPopupMenu> standardFilterPopups = new HashMap<>();

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
            int row;
            Column<T> column;
            if (getSelectedRow() == -1 && e.getButton() == MouseEvent.BUTTON1) {
              return;
            }
            Point mousePosition = e.getPoint();
            JTable t = (JTable) e.getSource();
            row = t.rowAtPoint(mousePosition);
            column =
                ObjectTable.this.columns.get(
                    convertColumnIndexToModel(t.columnAtPoint(mousePosition)));
            getFromRow(row)
                .ifPresent(
                    selection -> {
                      column.onAction(e, selection);
                      invokeSelectionListeners(selection, true);
                    });
            addStandardFilterListener(e, column);
          }
        });
    getTableHeader()
        .addMouseListener(
            new MouseAdapter() {
              @Override
              public void mouseReleased(MouseEvent e) {
                Column<T> column =
                    ObjectTable.this.columns.get(
                        convertColumnIndexToModel(
                            ((JTableHeader) e.getSource()).columnAtPoint(e.getPoint())));
                addStandardFilterListener(e, column);
              }
            });
    addKeyListener(
        new KeyAdapter() {
          @Override
          public void keyReleased(KeyEvent e) {
            getSelectedObject().ifPresent(selection -> invokeSelectionListeners(selection, false));
          }
        });
    setRowSorter(createRowSorter());
  }

  private void addStandardFilterListener(MouseEvent e, Column<T> column) {
    if (column.usesStandardFilter()) {
      if (SwingUtilities.isRightMouseButton(e)) {
        JPopupMenu popup = standardFilterPopups.get(column);
        if (standardColumnFilters.containsKey(column)) {
          String initialText = standardColumnFilters.get(column).getText();
          ((JTextField) ((JPanel) popup.getComponent(0)).getComponent(1)).setText(initialText);
        }
        popup.show(e.getComponent(), e.getX(), e.getY());
      }
    }
  }

  private JPopupMenu createPopupMenu(Column<T> column) {
    var popup = new JPopupMenu("Spaltenfilter");
    var textSize = new Dimension(200, 25);

    var textFilterPanel = new JPanel(new FlowLayout());
    textFilterPanel.add(new JLabel(IconFontSwing.buildIcon(FontAwesome.FILTER, 15, Color.BLUE)));
    var filterText = new JTextField();
    filterText.setPreferredSize(textSize);
    filterText
        .getDocument()
        .addDocumentListener(
            (DocumentChangeListener) e -> applyStandardFilter(popup, column, filterText));
    filterText.addActionListener(e -> popup.setVisible(false));
    textFilterPanel.add(filterText);

    var removeFilterPanel = new JPanel(new FlowLayout());
    removeFilterPanel.add(
        new JLabel(IconFontSwing.buildIcon(FontAwesome.TRASH, 15, Color.DARK_GRAY)));
    var removeFilter = new JLabel("Spaltenfilter Entfernen");
    removeFilter.setPreferredSize(textSize);
    removeFilterPanel.add(removeFilter);
    removeFilterPanel.addMouseListener(
        new MouseAdapter() {
          @Override
          public void mouseClicked(MouseEvent e) {
            applyStandardFilter(popup, column, null);
          }
        });

    var removeAllFiltersPanel = new JPanel(new FlowLayout());
    removeAllFiltersPanel.add(
        new JLabel(IconFontSwing.buildIcon(FontAwesome.TIMES, 15, Color.RED.darker())));
    var removeAllFilters = new JLabel("Alle Filter Entfernen");
    removeAllFilters.setPreferredSize(textSize);
    removeAllFiltersPanel.add(removeAllFilters);
    removeAllFiltersPanel.addMouseListener(
        new MouseAdapter() {
          @Override
          public void mouseClicked(MouseEvent e) {
            removeStandardFilter(popup);
          }
        });

    popup.add(textFilterPanel);
    popup.add(removeFilterPanel);
    popup.add(removeAllFiltersPanel);
    return popup;
  }

  private void removeStandardFilter(JPopupMenu p) {
    standardColumnFilters.clear();
    refreshModel();
    p.setVisible(false);
  }

  private void applyStandardFilter(JPopupMenu p, Column<T> c, JTextField text) {
    if (text == null) {
      standardColumnFilters.remove(c);
      p.setVisible(false);
    } else {
      standardColumnFilters.put(c, text);
    }
    refreshModel();
  }

  private void refreshModel() {
    model = (DefaultTableModel) createModel(columns, objects);
    setRowSorter(null);
    setModel(model);
    columns.forEach(
        c -> {
          if (c.usesStandardFilter()) standardFilterPopups.put(c, createPopupMenu(c));
        });
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
          if (e == null) throw new NullPointerException("cannot create model with null parameters");
        });
    Object[][] values = Tools.transformToArray(objects, Object[].class, this::collectColumns);
    String[] names = Tools.transformToArray(columns, String.class, Column::getName);
    return new ObjectTableModel(values, names);
  }

  public Optional<T> getFromRow(int index) {
    if (index < 0) return Optional.empty();
    return Optional.of(objects.get(super.convertRowIndexToModel(index)));
  }

  private void invokeSelectionListeners(T t, boolean isMouseClk) {
    selectionListeners.forEach(
        (listener, dblClk) -> {
          if (!dblClk || isMouseClk) listener.selected(t);
        });
  }

  private void insertColumn(Column<T> column, int index) {
    columns.add(index, column);
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
    insertColumn(column, columns.size());
  }

  public void addColumnAtIndex(int index, Column<T> column) {
    insertColumn(column, index);
  }

  public boolean contains(T t) {
    for (T compare : this) {
      if (compare.equals(t)) return true;
    }
    return false;
  }

  public Optional<T> getSelectedObject() {
    return getFromRow(getSelectedRow());
  }

  public T get(Predicate<T> function) {
    for (T object : this) {
      if (function.test(object)) {
        return object;
      }
    }
    return null;
  }

  public void addSelectionListener(ObjectSelectionListener<T> listener) {
    selectionListeners.put(listener, false);
  }

  public void addDoubleClickListener(ObjectSelectionListener<T> listener) {
    selectionListeners.put(
        new ObjectSelectionListener<T>() {
          private T last;
          private long lastClick = System.nanoTime();

          @Override
          public void selected(T t) {
            if (t.equals(last) && Math.abs(System.nanoTime() - lastClick) < 5e+8) {
              listener.selected(t);
            } else last = t;
            lastClick = System.nanoTime();
          }
        },
        true);
  }

  public void addAll(Collection<T> in) {
    in.forEach(this::add);
  }

  public void add(T in) {
    if (in == null) {
      throw new NullPointerException("Cannot add null object to ObjectTable");
    }
    objects.add(in);
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
    this.objects.clear();
    refreshModel();
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

  @NotNull
  @Override
  public Iterator<T> iterator() {
    return objects.iterator();
  }

  public void setRowFilter(kernbeisser.CustomComponents.ObjectTable.RowFilter<T> rowFilter) {
    this.rowFilter = rowFilter == null ? DEFAULT_ROW_FILTER : rowFilter;
    ((TableRowSorter<?>) getRowSorter()).sort();
  }

  public void setSwingRowFilter(RowFilter<Object, Integer> rowFilter) {
    this.swingRowFilter = rowFilter == null ? DEFAULT_SWING_ROW_FILTER : rowFilter;
    ((TableRowSorter<?>) getRowSorter()).sort();
  }

  private TableRowSorter<?> createRowSorter() {
    TableRowSorter<?> out = new TableRowSorter<>(model);
    out.setRowFilter(
        new RowFilter<Object, Integer>() {
          @Override
          public synchronized boolean include(Entry<?, ? extends Integer> entry) {
            try {
              T object = objects.get(entry.getIdentifier());
              return rowFilter.isDisplayed(object)
                  && isInStandardFilter(object)
                  && swingRowFilter.include(entry);
            } catch (IndexOutOfBoundsException e) {
              System.out.println(entry.getIdentifier());
              return false;
            }
          }
        });
    for (int i = 0; i < columns.size(); i++) {
      out.setComparator(i, columns.get(i).sorter());
    }
    return out;
  }

  private boolean isInStandardFilter(T t) {
    boolean result = true;
    for (Map.Entry<Column<T>, JTextField> filter : standardColumnFilters.entrySet()) {
      String text = (String) filter.getKey().getValue(t);
      result =
          Pattern.compile(filter.getValue().getText(), Pattern.CASE_INSENSITIVE)
              .matcher(text)
              .find();
      if (!result) break;
    }
    return result;
  }

  public Collection<T> getSelectedObjects() {
    int[] rows = getSelectedRows();
    ArrayList<T> out = new ArrayList<>(rows.length);
    for (int row : rows) {
      getFromRow(row).ifPresent(out::add);
    }
    return out;
  }

  public T[] getSelectedObjects(T[] pattern) {
    int[] rows = getSelectedRows();
    T[] out = Arrays.copyOf(pattern, rows.length);
    for (int i = 0; i < rows.length; i++) {
      out[i] = getFromRow(rows[i]).orElse(null);
    }
    return out;
  }

  public void removeIf(Predicate<T> predicate) {
    objects.removeIf(predicate);
    refreshModel();
  }

  public void removeAll(Collection<T> collection) {
    objects.removeAll(collection);
    refreshModel();
  }

  public List<T> getObjects() {
    return Collections.unmodifiableList(objects);
  }

  public Collection<T> getFilteredObjects() {
    Collection<T> out = new ArrayList<>();
    for (int i = 0; i < this.getRowCount(); i++) {
      this.getFromRow(i).ifPresent(out::add);
    }
    return out;
  }
}
