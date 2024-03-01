package kernbeisser.CustomComponents.ObjectTable;

import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;
import javax.swing.table.AbstractTableModel;
import lombok.Getter;
import lombok.NonNull;
import org.jetbrains.annotations.NotNull;
import rs.groump.AccessDeniedException;

public class ObjectTableModel<T> extends AbstractTableModel {

  private static final Object NO_ACCESS_VALUE = "**********";

  @NonNull @Getter private List<Column<T>> columns;

  @NonNull @Getter private List<T> objects;

  public ObjectTableModel(@NotNull List<Column<T>> columns, @NonNull List<T> objects) {
    this.columns = columns;
    this.objects = objects;
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
      return new Property<>(parent, columns.get(columnIndex).getValue(parent));
    } catch (AccessDeniedException e) {
      return new Property<>(parent, NO_ACCESS_VALUE);
    }
  }

  public void setColumns(List<Column<T>> columns) {
    this.columns = columns;
    fireTableStructureChanged();
  }

  public void setObjects(List<T> objects) {
    this.objects = objects;
    fireTableDataChanged();
  }

  @Override
  public String getColumnName(int column) {
    return columns.get(column).getName();
  }

  public void addColumn(Column<T> column) {
    this.columns.add(column);
    fireTableStructureChanged();
  }

  public void addObject(int index, T in) {
    this.objects.add(index, in);
    fireTableRowsInserted(this.objects.size() - 1, this.objects.size() - 1);
  }

  public void addObjects(Collection<T> in) {
    this.objects.addAll(in);
    fireTableRowsInserted(objects.size() - 1 - in.size(), objects.size() - 1);
  }

  public void removeObject(int index) {
    this.objects.remove(index);
    fireTableRowsDeleted(index, index);
  }

  public void replaceObject(int index, T value) {
    this.objects.remove(index);
    this.objects.add(index, value);
    fireTableRowsUpdated(index, index);
  }

  public void removeAllObjects(Collection<T> collection) {
    this.objects.removeAll(collection);
    fireTableDataChanged();
  }

  public void removeAllObjectsIf(Predicate<T> predicate) {
    this.objects.removeIf(predicate);
    fireTableDataChanged();
  }
}
