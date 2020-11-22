package kernbeisser.CustomComponents.ComboBox;

import java.awt.Component;
import java.util.function.Function;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;
import lombok.Getter;

public class AdvancedComboBoxRenderer<T> extends DefaultListCellRenderer {

  @Getter private final Function<T, String> stringFormer;

  public AdvancedComboBoxRenderer(Function<T, String> stringFormer) {
    this.stringFormer = stringFormer;
  }

  @Override
  public Component getListCellRendererComponent(
      JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
    try {
      return super.getListCellRendererComponent(
          list,
          value == null ? null : stringFormer.apply((T) value),
          index,
          isSelected,
          cellHasFocus);
    } catch (ClassCastException e) {
      return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
    }
  }
}
