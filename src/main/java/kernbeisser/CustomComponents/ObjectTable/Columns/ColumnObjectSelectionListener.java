package kernbeisser.CustomComponents.ObjectTable.Columns;

import java.awt.event.MouseEvent;

public interface ColumnObjectSelectionListener<T> {
  public void onAction(MouseEvent e, T t);
}
