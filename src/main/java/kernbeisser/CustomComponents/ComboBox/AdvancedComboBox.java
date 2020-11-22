package kernbeisser.CustomComponents.ComboBox;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
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
  }

  public void setItems(Collection<T> items) {
    setItems(new ArrayList<>(items));
    removeAllItems();
  }

  public T getSelected() {
    if (getSelectedIndex() == -1) {
      throw new NullPointerException("no selection");
    }
    return getItemAt(getSelectedIndex());
  }
}
