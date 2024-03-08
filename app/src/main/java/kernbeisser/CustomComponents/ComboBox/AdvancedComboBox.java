package kernbeisser.CustomComponents.ComboBox;

import java.util.*;
import java.util.function.Function;
import javax.swing.ComboBoxModel;
import javax.swing.JComboBox;
import lombok.Getter;

public class AdvancedComboBox<T> extends JComboBox<T> {

  @Getter private final AdvancedComboBoxRenderer<T> renderer;
  @Getter private final AdvancedComboBoxKeySelectionManager<T> selectionManager;

  public AdvancedComboBox() {
    this(Object::toString);
  }

  public AdvancedComboBox(Function<T, String> stringFormer) {
    super(new AdvancedComboBoxModel<>());
    this.renderer = new AdvancedComboBoxRenderer<>(stringFormer);
    this.selectionManager =
        new AdvancedComboBoxKeySelectionManager<>(
            (a, b) ->
                renderer
                    .getStringFormer()
                    .apply(a)
                    .toUpperCase(Locale.ROOT)
                    .startsWith(b.toUpperCase(Locale.ROOT)));
    setRenderer(renderer);
    setKeySelectionManager(selectionManager);
  }

  @Override
  public AdvancedComboBoxModel<T> getModel() {
    return (AdvancedComboBoxModel<T>) super.getModel();
  }

  public void setItems(List<T> items) {
    getModel().setValues(items);
    if (getSelectedIndex() == -1 && items.size() > 0) {
      setSelectedIndex(0);
    }
  }

  public void setItems(Collection<T> items) {
    removeAllItems();
    setItems(new ArrayList<>(items));
  }

  public Optional<T> getSelected() {
    return Optional.ofNullable((T) getModel().getSelectedItem());
  }

  @Override
  public void setModel(ComboBoxModel<T> aModel) {
    if (!(aModel instanceof AdvancedComboBoxModel)) {
      throw new UnsupportedOperationException("AdvancedComboBoxModelRequired");
    }
    super.setModel(aModel);
  }

  public void addSelectionListener(SelectionListener<T> selectionListener) {
    addActionListener(e -> getSelected().ifPresent(selectionListener::select));
  }

  public interface SelectionListener<T> {
    void select(T t);
  }
}
