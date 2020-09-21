package kernbeisser.CustomComponents.ObjectTable;

import java.awt.*;
import javax.swing.*;
import javax.swing.table.TableCellRenderer;

public class StripedRenderer implements TableCellRenderer {
  public final TableCellRenderer dark;
  public final TableCellRenderer normal;

  public StripedRenderer(TableCellRenderer normal, TableCellRenderer dark) {
    this.dark = dark;
    this.normal = normal;
  }

  @Override
  public Component getTableCellRendererComponent(
      JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
    return (row % 2 == 0 ? normal : dark)
        .getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
  }
}
