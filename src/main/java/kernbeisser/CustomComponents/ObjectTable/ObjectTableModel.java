package kernbeisser.CustomComponents.ObjectTable;

import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;
import javax.swing.table.AbstractTableModel;
import kernbeisser.Exeptions.PermissionKeyRequiredException;
import lombok.Getter;
import lombok.NonNull;
import org.jetbrains.annotations.NotNull;


public class ObjectTableModel extends DefaultTableModel {

  private static final Object NO_ACCESS_VALUE = "**********";

  @NonNull @Getter private List<Column<T>> columns;

  @NonNull @Getter private List<T> objects;

  public ObjectTableModel(@NotNull List<Column<T>> columns, @NonNull List<T> objects) {
    this.columns = columns;
    this.objects = objects;
  }

  public ObjectTableModel(Vector columnNames, int rowCount) {
    super(columnNames, rowCount);
  }

  public ObjectTableModel(Object[] columnNames, int rowCount) {
    super(columnNames, rowCount);
  }

  @Override
  public Object getValueAt(int rowIndex, int columnIndex) {
    T parent = objects.get(rowIndex);
    try {
      return new Property<>(parent, columns.get(columnIndex).getValue(parent));
    } catch (PermissionKeyRequiredException e) {
      return new Property<>(parent, NO_ACCESS_VALUE);
    }

  }

  public ObjectTableModel(Object[][] data, Object[] columnNames) {
    super(data, columnNames);
  }

  @Override
  public boolean isCellEditable(int row, int column) {
    return false;
  }
}
