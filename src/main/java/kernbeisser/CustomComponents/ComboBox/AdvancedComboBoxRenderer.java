package kernbeisser.CustomComponents.ComboBox;

import java.awt.Color;
import java.awt.Component;
import java.util.function.Function;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

public class AdvancedComboBoxRenderer<T> extends JLabel implements ListCellRenderer<T> {

  @Getter private final Function<@NotNull T, String> stringFormer;
  @Setter @Getter private Function<T, Icon> iconFunction = e -> null;
  @Setter private boolean passNull;
  @Setter private String noSelectionText = "Nichts ausgewählt!";
  @Setter private Icon noSelectionIcon = null;

  public AdvancedComboBoxRenderer(Function<T, String> stringFormer) {
    this.stringFormer = stringFormer;
  }

  @Override
  public Component getListCellRendererComponent(
      JList<? extends T> list, T value, int index, boolean isSelected, boolean cellHasFocus) {
    setIcon(value == null || passNull ? noSelectionIcon : iconFunction.apply(value));
    if (isSelected) {
      setBackground(list.getSelectionBackground());
      setForeground(new Color(0x1D9EE3));
    } else {
      setBackground(list.getBackground());
      setForeground(list.getForeground());
    }
    setText(value == null || passNull ? noSelectionText : stringFormer.apply(value));
    return this;
  }
}
