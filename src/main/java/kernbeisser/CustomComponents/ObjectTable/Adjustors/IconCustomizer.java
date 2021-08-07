package kernbeisser.CustomComponents.ObjectTable.Adjustors;

import java.util.function.Function;
import javax.swing.Icon;
import javax.swing.table.DefaultTableCellRenderer;

public class IconCustomizer<V> implements TableCellAdjustor<V> {
  private final Function<V, Icon> iconFactory;

  public IconCustomizer(Function<V, Icon> iconFactory) {
    this.iconFactory = iconFactory;
  }

  @Override
  public void customizeFor(
      DefaultTableCellRenderer component,
      V v,
      boolean isSelected,
      boolean hasFocus,
      int row,
      int column) {
    component.setIcon(iconFactory.apply(v));
  }
}
