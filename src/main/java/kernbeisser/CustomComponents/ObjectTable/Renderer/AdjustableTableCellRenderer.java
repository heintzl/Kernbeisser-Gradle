package kernbeisser.CustomComponents.ObjectTable.Renderer;

import java.awt.Component;
import java.util.ArrayList;
import java.util.Collection;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import kernbeisser.CustomComponents.ObjectTable.Adjustors.TableCellAdjustor;
import kernbeisser.CustomComponents.ObjectTable.Property;

public class AdjustableTableCellRenderer<T> extends DefaultTableCellRenderer {

  private final Collection<TableCellAdjustor<T>> customizers = new ArrayList<>();

  @Override
  public Component getTableCellRendererComponent(
      JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
    if(value == null)return super.getTableCellRendererComponent(table, null, isSelected, hasFocus, row, column);
    Property<T> property = (Property<T>) value;
    customizers.forEach(
        e -> e.customizeFor(this, property.getParent(), isSelected, hasFocus, row, column));
    return super.getTableCellRendererComponent(
        table, property.getValue(), isSelected, hasFocus, row, column);
  }

  public void addTableCellAdjustor(TableCellAdjustor<T> customizer) {
    customizers.add(customizer);
  }
}
