package kernbeisser.CustomComponents.ObjectTable;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.*;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import javax.swing.*;
import javax.swing.RowFilter;
import javax.swing.table.*;
import jiconfont.icons.font_awesome.FontAwesome;
import jiconfont.swing.IconFontSwing;
import kernbeisser.Useful.Constants;
import kernbeisser.Useful.DocumentChangeListener;
import org.jetbrains.annotations.NotNull;

public class ObjectTable<T> extends JTable implements Iterable<T> {

  private final Map<ObjectSelectionListener<T>, Boolean> selectionListeners = new HashMap<>();

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

  private final Map<Column<T>, JTextField> standardColumnFilters = new HashMap<>();

  private Map<Column<T>, JPopupMenu> standardFilterPopups;

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

  @Override
  public ObjectTableModel<T> getModel() {
    return (ObjectTableModel<T>) super.getModel();
  }

  public List<Column<T>> getColumns() {
    return Collections.unmodifiableList(getModel().getColumns());
  }

  public List<T> getObjects() {
    return Collections.unmodifiableList(getModel().getObjects());
  }

  @Override
  public void setModel(@NotNull TableModel dataModel) {
    if (!(dataModel instanceof ObjectTableModel))
      throw new IllegalArgumentException(
          "cannot set " + dataModel + " as a TableModel for an ObjectTable");
    super.setModel(dataModel);
  }

  @Override
  public void addColumn(@NotNull TableColumn aColumn) {
    if (aColumn.getHeaderValue() == null) {
      int modelColumn = aColumn.getModelIndex();
      String columnName = getModel().getColumnName(modelColumn);
      aColumn.setHeaderValue(columnName);
      Column<T> column = getModel().getColumns().get(modelColumn);
      column.adjust(aColumn);
      aColumn.setCellRenderer(column.getRenderer());
      if (column.usesStandardFilter())
        getStandardFilterPopups().put(column, createPopupMenu(column));
    }
    getColumnModel().addColumn(aColumn);
  }

  private Map<Column<T>, JPopupMenu> getStandardFilterPopups() {
    if (standardFilterPopups == null) {
      standardFilterPopups = new HashMap<>();
    }
    return standardFilterPopups;
  }

  ObjectTable(Collection<T> fill, Collection<Column<T>> columns) {
    super(new ObjectTableModel<T>(new ArrayList<>(columns), new ArrayList<>(fill)));
    setAutoCreateRowSorter(false);
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
            column = getColumns().get(convertColumnIndexToModel(t.columnAtPoint(mousePosition)));
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
                    getColumns()
                        .get(
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
        JPopupMenu popup = getStandardFilterPopups().get(column);
        if (standardColumnFilters.containsKey(column)) {
          String initialText = standardColumnFilters.get(column).getText();
          ((JTextField) ((JPanel) popup.getComponent(0)).getComponent(1)).setText(initialText);
        }
        popup.show(e.getComponent(), e.getX(), e.getY());
      }
    }
  }

  private JPopupMenu createPopupMenu(Column<T> column) {
    JPopupMenu popup = new JPopupMenu("Spaltenfilter");
    Dimension textSize = new Dimension(200, 25);

    JPanel textFilterPanel = new JPanel(new FlowLayout());
    textFilterPanel.add(new JLabel(IconFontSwing.buildIcon(FontAwesome.FILTER, 15, Color.BLUE)));
    JTextField filterText = new JTextField();
    filterText.setPreferredSize(textSize);
    filterText
        .getDocument()
        .addDocumentListener(
            (DocumentChangeListener) e -> applyStandardFilter(popup, column, filterText));
    filterText.addActionListener(e -> popup.setVisible(false));
    textFilterPanel.add(filterText);

    JPanel removeFilterPanel = new JPanel(new FlowLayout());
    removeFilterPanel.add(
        new JLabel(IconFontSwing.buildIcon(FontAwesome.TRASH, 15, Color.DARK_GRAY)));
    JLabel removeFilter = new JLabel("Spaltenfilter Entfernen");
    removeFilter.setPreferredSize(textSize);
    removeFilterPanel.add(removeFilter);
    removeFilterPanel.addMouseListener(
        new MouseAdapter() {
          @Override
          public void mouseClicked(MouseEvent e) {
            applyStandardFilter(popup, column, null);
          }
        });

    JPanel removeAllFiltersPanel = new JPanel(new FlowLayout());
    removeAllFiltersPanel.add(
        new JLabel(IconFontSwing.buildIcon(FontAwesome.TIMES, 15, Color.RED.darker())));
    JLabel removeAllFilters = new JLabel("Alle Filter Entfernen");
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

  public void selectRow(int row) {
    setRowSelectionInterval(row, row);
    JViewport viewport = (JViewport) getParent();
    Rectangle cellRectangle = getCellRect(getSelectedRow(), 0, true);
    Rectangle visibleRectangle = viewport.getVisibleRect();
    SwingUtilities.invokeLater(
        () ->
            scrollRectToVisible(
                new Rectangle(
                    cellRectangle.x,
                    cellRectangle.y,
                    (int) visibleRectangle.getWidth(),
                    (int) visibleRectangle.getHeight())));
  }

  private void removeStandardFilter(JPopupMenu p) {
    standardColumnFilters.clear();
    getStandardFilterPopups()
        .values()
        .forEach(e -> ((JTextField) ((JPanel) e.getComponent(0)).getComponent(1)).setText(""));
    sort();
    p.setVisible(false);
  }

  public void sort() {
    ((TableRowSorter<?>) getRowSorter()).sort();
  }

  public void setSortKeys(RowSorter.SortKey... sortKey) {
    getRowSorter().setSortKeys(Arrays.asList(sortKey));
  }

  private void applyStandardFilter(JPopupMenu menu, Column<T> c, JTextField text) {
    if (text == null) {
      standardColumnFilters.remove(c);
      ((JTextField) ((JPanel) getStandardFilterPopups().get(c).getComponent(0)).getComponent(1))
          .setText("");
      menu.setVisible(false);
    } else {
      standardColumnFilters.put(c, text);
    }
    ((TableRowSorter<?>) getRowSorter()).sort();
  }

  public Optional<T> getFromRow(int index) {
    if (index < 0) return Optional.empty();
    return Optional.of(getObjects().get(super.convertRowIndexToModel(index)));
  }

  private void invokeSelectionListeners(T t, boolean isMouseClk) {
    selectionListeners.forEach(
        (listener, dblClk) -> {
          if (!dblClk || isMouseClk) listener.selected(t);
        });
  }

  private void insertColumn(Column<T> column, int index) {
    ArrayList<Column<T>> beforeCopy = new ArrayList<>(getModel().getColumns());
    beforeCopy.add(index, column);
    getModel().setColumns(beforeCopy);
  }

  public void setColumns(List<Column<T>> columns) {
    getModel().setColumns(columns);
  }

  public void setColumns(Collection<Column<T>> columns) {
    setColumns(new ArrayList<>(columns));
  }

  @SafeVarargs
  public final void setColumns(Column<T>... columns) {
    setColumns(new ArrayList<>(Arrays.asList(columns)));
  }

  public void addColumn(Column<T> column) {
    getModel().addColumn(column);
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
            if (t.equals(last)
                && Math.abs(System.nanoTime() - lastClick) < Constants.SYSTEM_DBLCLK_INTERVAL) {
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

  public void add(int index, T in) {
    if (in == null) {
      throw new NullPointerException("Cannot add null object to ObjectTable");
    }
    ObjectTableModel<T> model = getModel();
    model.addObject(index, in);
  }

  public void add(T in) {
    if (in == null) {
      throw new NullPointerException("Cannot add null object to ObjectTable");
    }
    ObjectTableModel<T> model = getModel();
    model.addObject(model.getObjects().size(), in);
  }

  public void remove(T t) {
    int index = indexOf(t);
    if (index != -1) {
      ObjectTableModel<T> model = getModel();
      model.removeObject(index);
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
    getModel().setObjects(new ArrayList<>());
  }

  public void setObjects(Collection<T> collection) {
    getModel().setObjects(new ArrayList<>(collection));
  }

  public void replace(T value, T newValue) {
    int index = getObjects().indexOf(value);
    if (index == -1) throw new IllegalArgumentException("Object isn't contained in the table");
    getModel().replaceObject(index, newValue);
    repaint();
  }

  @NotNull
  @Override
  public Iterator<T> iterator() {
    return getObjects().iterator();
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
    TableRowSorter<?> out =
        new TableRowSorter<ObjectTableModel<T>>(getModel()) {
          @Override
          public Comparator<?> getComparator(int column) {
            return getColumns().get(column).sorter();
          }
        };
    out.setRowFilter(
        new RowFilter<Object, Integer>() {
          @Override
          public synchronized boolean include(Entry<?, ? extends Integer> entry) {
            try {
              T object = getObjects().get(entry.getIdentifier());
              return rowFilter.isDisplayed(object)
                  && (standardColumnFilters.isEmpty() || isInStandardFilter(object))
                  && swingRowFilter.include(entry);
            } catch (IndexOutOfBoundsException e) {
              System.out.println(entry.getIdentifier());
              return false;
            }
          }
        });
    return out;
  }

  private boolean isInStandardFilter(T t) {
    for (Map.Entry<Column<T>, JTextField> filter : standardColumnFilters.entrySet()) {
      Object content = filter.getKey().getValue(t);
      if (content == null) return false;
      try {
        boolean match =
            Pattern.compile(filter.getValue().getText(), Pattern.CASE_INSENSITIVE)
                .matcher(content.toString())
                .find();
        if (!match) return false;
      } catch (PatternSyntaxException e) {
        return false;
      }
    }
    return true;
  }

  public Collection<T> getSelectedObjects() {
    int[] rows = getSelectedRows();
    ArrayList<T> out = new ArrayList<>(rows.length);
    for (int row : rows) {
      getFromRow(row).ifPresent(out::add);
    }
    return out;
  }

  public void removeIf(Predicate<T> predicate) {
    getModel().removeAllObjectsIf(predicate);
  }

  public void removeAll(Collection<T> collection) {
    getModel().removeAllObjects(collection);
  }

  public Collection<T> getFilteredObjects() {
    Collection<T> out = new ArrayList<>();
    for (int i = 0; i < this.getRowCount(); i++) {
      this.getFromRow(i).ifPresent(out::add);
    }
    return out;
  }

  public void addSelectionChangeListener(Consumer<Collection<T>> modelConsumer) {
    getSelectionModel()
        .addListSelectionListener(
            e -> {
              modelConsumer.accept(getSelectedObjects());
            });
  }

  public void selectionComponent(Component component) {
    component.setEnabled(!getSelectionModel().isSelectionEmpty());
    getSelectionModel()
        .addListSelectionListener(
            e -> component.setEnabled(!getSelectionModel().isSelectionEmpty()));
  }

  public void selectionComponents(Component... components) {
    for (Component component : components)
      component.setEnabled(!getSelectionModel().isSelectionEmpty());
    getSelectionModel()
        .addListSelectionListener(
            e -> {
              for (Component component : components)
                component.setEnabled(!getSelectionModel().isSelectionEmpty());
            });
  }

  public ObjectTable<T> allowCaching() {
    return allowCaching(true);
  }

  public ObjectTable<T> allowCaching(boolean allowCaching) {
    getModel().setAllowCaching(allowCaching);
    return this;
  }
}
