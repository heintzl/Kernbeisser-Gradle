package kernbeisser.CustomComponents.ObjectTable;

import java.awt.*;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;

public class StripedRenderer extends DefaultTableCellRenderer {
  private final Color a;
  private final Color b;

  public StripedRenderer() {
    this(Column.STRIPED_BACKGROUND_COLOR_A, Column.STRIPED_BACKGROUND_COLOR_B);
  }

  public StripedRenderer(Color a, Color b) {
    this.a = a;
    this.b = b;
  }

  @Override
  public Component getTableCellRendererComponent(
      JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
    setBackground(row % 2 == 0 ? a : b);
    return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
  }
}
