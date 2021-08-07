package kernbeisser.CustomComponents.ComboBox;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.swing.MutableComboBoxModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import lombok.Setter;

public class AdvancedComboBoxModel<T> implements MutableComboBoxModel<T> {

  private final Collection<ListDataListener> listeners = new ArrayList<>();
  private List<T> values = new ArrayList<>();
  private T selected;
  @Setter private boolean allowNullSelection;

  @Override
  public void setSelectedItem(Object anItem) {
    try {
      selected = (T) anItem;
    } catch (ClassCastException e) {
      selected = null;
    }
  }

  @Override
  public Object getSelectedItem() {
    return selected;
  }

  @Override
  public int getSize() {
    return values.size() + (allowNullSelection ? 1 : 0);
  }

  public void setValues(List<T> values) {
    this.values = values;
    triggerContentEvent(0, values.size());
  }

  private void triggerRemoveEvent(int ind, int ind0) {
    ListDataEvent event = new ListDataEvent(this, ListDataEvent.INTERVAL_REMOVED, ind, ind0);
    for (ListDataListener listener : listeners) {
      listener.intervalRemoved(event);
    }
  }

  private void triggerAddEvent(int ind, int ind0) {
    ListDataEvent event = new ListDataEvent(this, ListDataEvent.INTERVAL_ADDED, ind, ind0);
    for (ListDataListener listener : listeners) {
      listener.intervalAdded(event);
    }
  }

  private void triggerContentEvent(int ind, int ind0) {
    ListDataEvent event = new ListDataEvent(this, ListDataEvent.CONTENTS_CHANGED, ind, ind0);
    for (ListDataListener listener : listeners) {
      listener.contentsChanged(event);
    }
  }

  @Override
  public T getElementAt(int index) {
    return index == values.size() ? null : values.get(index);
  }

  @Override
  public void addListDataListener(ListDataListener l) {
    listeners.add(l);
  }

  @Override
  public void removeListDataListener(ListDataListener l) {
    listeners.remove(l);
  }

  @Override
  public void addElement(T item) {
    values.add(item);
    triggerAddEvent(values.size() - 1, values.size() - 1);
  }

  @Override
  public void removeElement(Object obj) {
    removeElementAt(values.indexOf(obj));
  }

  @Override
  public void insertElementAt(T item, int index) {
    if (index == -1) return;
    values.add(index, item);
    triggerAddEvent(index, index);
  }

  @Override
  public void removeElementAt(int index) {
    try {
      selected = values.get(index - 1);
      triggerRemoveEvent(index, index);
    } catch (ArrayIndexOutOfBoundsException e) {
      selected = values.stream().findAny().orElse(null);
    }
  }
}
