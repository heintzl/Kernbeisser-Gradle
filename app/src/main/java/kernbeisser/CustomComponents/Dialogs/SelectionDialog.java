package kernbeisser.CustomComponents.Dialogs;

import java.awt.Component;
import java.util.Collection;
import java.util.Optional;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

public class SelectionDialog {

  private static String lastChoice = null;

  public static <T> Optional<T> select(Component parent, String title, Collection<T> values) {
    JComboBox<T> comboBox = new JComboBox<>();
    switch (values.size()) {
      case 0:
        return Optional.empty();
      case 1:
        return Optional.ofNullable(values.iterator().next());
      default:
        for (T value : values) {
          comboBox.addItem(value);
          if (value.toString().equals(lastChoice)) {
            comboBox.setSelectedItem(value);
          }
        }
    }
    JOptionPane.showMessageDialog(
        parent, new Object[] {new JLabel(title), comboBox}, title, JOptionPane.INFORMATION_MESSAGE);
    int index = comboBox.getSelectedIndex();
    if (index == -1) {
      return Optional.empty();
    } else {
      T t = comboBox.getItemAt(index);
      lastChoice = t.toString();
      return Optional.of(t);
    }
  }
}
