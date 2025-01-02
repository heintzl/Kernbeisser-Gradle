package kernbeisser.CustomComponents.ObjectTable.Adjustors;

import java.util.function.Function;
import javax.swing.Icon;
import javax.swing.table.DefaultTableCellRenderer;

public class IconCustomizer<V> implements TableCellAdjustor<V> {
  private final Function<V, Icon> iconFactory;
  private static final int TEXT_INVISIBLE = 1000;
  private static final int TEXT_VISIBLE = 5;
  private int textGap = TEXT_INVISIBLE;

  public IconCustomizer(Function<V, Icon> iconFactory) {
    this(iconFactory, false);
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
    component.setIconTextGap(textGap);
  }

  public IconCustomizer(Function<V, Icon> iconFactory, boolean textVisible) {
    this.iconFactory = iconFactory;
    if (textVisible) {
      this.textGap = TEXT_VISIBLE;
    }
    ;
  }
}
