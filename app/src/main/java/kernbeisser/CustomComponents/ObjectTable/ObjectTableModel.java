package kernbeisser.CustomComponents.ObjectTable;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;
import javax.swing.table.AbstractTableModel;
import lombok.Getter;
import lombok.NonNull;
import org.jetbrains.annotations.NotNull;
import rs.groump.AccessDeniedException;

public class ObjectTableModel<T> extends AbstractTableModel {

  private static final int CACHE_INIT_SIZE = 500;

  private static final double cacheGrowingFactor = 2;
  private static final Object NO_ACCESS_VALUE = "**********";

  private boolean allowCaching = false;

  private Property<T>[][] propertyCache;

  @NonNull @Getter private List<Column<T>> columns;

  @NonNull @Getter private List<T> objects;

  public ObjectTableModel(@NotNull List<Column<T>> columns, @NonNull List<T> objects) {
    this.columns = columns;
    this.objects = objects;
    if (allowCaching) {
      propertyCache = new Property[CACHE_INIT_SIZE][];
    }
  }

  @Override
  public int getRowCount() {
    return objects.size();
  }

  @Override
  public int getColumnCount() {
    return columns.size();
  }

  @Override
  public Object getValueAt(int rowIndex, int columnIndex) {
    T parent = objects.get(rowIndex);
    try {
      if (!allowCaching) {
        return new Property<>(parent, columns.get(columnIndex).getValue(parent));
      }
      return accessCache(parent, rowIndex, columnIndex);
    } catch (AccessDeniedException e) {
      return new Property<>(parent, NO_ACCESS_VALUE);
    }
  }

  private Property<T> accessCache(T parent, int rowIndex, int columnIndex) {
    try {
      Property<T>[] rowProperties = propertyCache[rowIndex];
      if (rowProperties != null && rowProperties.length == columns.size()) {
        return rowProperties[columnIndex];
      } else {
        var properties = collectProperties(parent);
        Property<T> returnProperty = properties[columnIndex];
        propertyCache[rowIndex] = properties;
        return returnProperty;
      }
    } catch (ArrayIndexOutOfBoundsException e) {
      growCache(rowIndex + 1);
      return accessCache(parent, rowIndex, columnIndex);
    }
  }

  private Property<T>[] collectProperties(T parent) {
    Property<T>[] properties = new Property[columns.size()];
    for (int i = 0; i < properties.length; i++) {
      properties[i] = new Property<>(parent, columns.get(i).getValue(parent));
    }
    return properties;
  }

  private void growCache(int min) {
    propertyCache =
        Arrays.copyOf(
            propertyCache, Math.max((int) (propertyCache.length * cacheGrowingFactor), min));
  }

  private void invalidateCache() {
    propertyCache = new Property[CACHE_INIT_SIZE][];
  }

  public void setColumns(List<Column<T>> columns) {
    invalidateCache();
    this.columns = columns;
    fireTableStructureChanged();
  }

  public void setObjects(List<T> objects) {
    invalidateCache();
    this.objects = objects;
    fireTableDataChanged();
  }

  @Override
  public String getColumnName(int column) {
    return columns.get(column).getName();
  }

  public void addColumn(Column<T> column) {
    invalidateCache();
    this.columns.add(column);
    fireTableStructureChanged();
  }

  public void removeColumnIf(Predicate<Column<T>> filter) {
    invalidateCache();
    this.columns.removeIf(filter);
    fireTableStructureChanged();
  }

  public void addObject(int index, T in) {
    invalidateCache();
    this.objects.add(index, in);
    fireTableRowsInserted(this.objects.size() - 1, this.objects.size() - 1);
  }

  public void addObjects(Collection<T> in) {
    invalidateCache();
    this.objects.addAll(in);
    fireTableRowsInserted(objects.size() - 1 - in.size(), objects.size() - 1);
  }

  public void removeObject(int index) {
    invalidateCache();
    this.objects.remove(index);
    fireTableRowsDeleted(index, index);
  }

  public void replaceObject(int index, T value) {
    invalidateCache();
    this.objects.remove(index);
    this.objects.add(index, value);
    fireTableRowsUpdated(index, index);
  }

  public void removeAllObjects(Collection<T> collection) {
    invalidateCache();
    this.objects.removeAll(collection);
    fireTableDataChanged();
  }

  public void removeAllObjectsIf(Predicate<T> predicate) {
    invalidateCache();
    this.objects.removeIf(predicate);
    fireTableDataChanged();
  }

  public void setAllowCaching(boolean allowCaching) {
    if (allowCaching == this.allowCaching) {
      return;
    }
    propertyCache = allowCaching ? new Property[CACHE_INIT_SIZE][] : null;
    this.allowCaching = allowCaching;
  }
}
