package kernbeisser.CustomComponents.ObjectTable.Adjustors;

import javax.swing.table.DefaultTableCellRenderer;

public interface TableCellAdjustor<V> {

  void customizeFor(
      DefaultTableCellRenderer component,
      V v,
      boolean isSelected,
      boolean hasFocus,
      int row,
      int column);
}
