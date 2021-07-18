package kernbeisser.CustomComponents.ObjectTable.Adjustors;

import java.awt.Color;
import javax.swing.table.DefaultTableCellRenderer;

public class StripedCellAdjustor<T> implements TableCellAdjustor<T> {

  public static final Color STRIPED_BACKGROUND_COLOR_A = new Color(240, 240, 240);
  public static final Color STRIPED_BACKGROUND_COLOR_B =
      new DefaultTableCellRenderer().getBackground();

  private final Color a;
  private final Color b;

  public StripedCellAdjustor() {
    this(STRIPED_BACKGROUND_COLOR_A, STRIPED_BACKGROUND_COLOR_B);
  }

  public StripedCellAdjustor(Color a, Color b) {
    this.a = a;
    this.b = b;
  }

  @Override
  public void customizeFor(
      DefaultTableCellRenderer renderer,
      T o,
      boolean isSelected,
      boolean hasFocus,
      int row,
      int column) {
    renderer.setBackground(row % 2 == 0 ? a : b);
  }
}
