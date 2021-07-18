package kernbeisser.CustomComponents.ObjectTable.Adjustors;

import javax.swing.table.DefaultTableCellRenderer;

public interface SimpleCellAdjustor<V> extends TableCellAdjustor<V> {

  void customizeSimple(DefaultTableCellRenderer comp, V v);

  @Override
  default void customizeFor(
      DefaultTableCellRenderer component,
      V v,
      boolean isSelected,
      boolean hasFocus,
      int row,
      int column) {
    customizeSimple(component, v);
  }
}
