package kernbeisser.CustomComponents.ComboBox;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import javax.swing.JComboBox;
import lombok.Getter;

public class AdvancedComboBox<T> extends JComboBox<T> {

  @Getter private final AdvancedComboBoxRenderer<T> renderer;

  public AdvancedComboBox() {
    this(Object::toString);
  }

  public AdvancedComboBox(Function<T, String> stringFormer) {
    super(new AdvancedComboBoxModel<>());
    this.renderer = new AdvancedComboBoxRenderer<>(stringFormer);
    setRenderer(renderer);
  }

  private void checkModel() {
    if (!(getModel() instanceof AdvancedComboBoxModel)) {
      throw new UnsupportedOperationException("AdvancedComboBoxModelRequired");
    }
  }

  public void setItems(List<T> items) {
    checkModel();
    ((AdvancedComboBoxModel<T>) getModel()).setValues(items);
    if (getSelectedIndex() == -1 && items.size() > 0) {
      setSelectedIndex(0);
    }
  }

  public void setItems(Collection<T> items) {
    removeAllItems();
    setItems(new ArrayList<>(items));
  }

  public Optional<T> getSelected() {
    if (getSelectedIndex() == -1) {
      return Optional.empty();
    }
    return Optional.of(getItemAt(getSelectedIndex()));
  }
}
