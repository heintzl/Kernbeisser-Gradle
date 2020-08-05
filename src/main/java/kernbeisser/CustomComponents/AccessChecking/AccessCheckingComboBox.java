package kernbeisser.CustomComponents.AccessChecking;

import javax.swing.*;
import kernbeisser.Exeptions.AccessDeniedException;
import kernbeisser.Exeptions.CannotParseException;
import kernbeisser.Useful.Tools;
import org.jetbrains.annotations.Nullable;

public class AccessCheckingComboBox<P, V> extends JComboBox<Object> implements Bounded<P, V> {
  private boolean inputChanged = false;

  private static final Object NO_READ_PERMISSION = "<Keine Leseberechtigung>";

  private final Setter<P, V> setter;
  private final Getter<P, V> getter;

  public AccessCheckingComboBox(Getter<P, V> getter, Setter<P, V> setter) {
    this.getter = getter;
    this.setter = setter;
    addActionListener(e -> inputChanged = true);
  }

  @Override
  public void inputChanged() {
    inputChanged = true;
  }

  @Override
  public boolean isInputChanged() {
    return inputChanged;
  }

  @Override
  public void setObjectData(P data) {
    try {
      setSelectedItem(getter.get(data));
    } catch (AccessDeniedException ignored) {

    }
  }

  @Nullable
  @Override
  public Object getSelectedItem() {
    Object selectedItem = super.getSelectedItem();
    return selectedItem != null
        ? selectedItem.equals(NO_READ_PERMISSION) ? null : selectedItem
        : null;
  }

  @Override
  public void writeInto(P p) throws CannotParseException {
    try {
      int selectedIndex = getSelectedIndex();
      if (selectedIndex > -1) {
        setter.set(p, (V) getSelectedItem());
      } else {
        throw new CannotParseException();
      }
    } catch (AccessDeniedException ignored) {
    }
  }

  @Override
  public Getter<P, V> getGetter() {
    return getter;
  }

  @Override
  public Setter<P, V> getSetter() {
    return setter;
  }

  @Override
  public void setReadable(boolean b) {
    if (b) {
      super.removeItem(NO_READ_PERMISSION);
      super.addItem(NO_READ_PERMISSION);
      setSelectedItem(NO_READ_PERMISSION);
    } else {
      super.removeItem(NO_READ_PERMISSION);
    }
  }

  public void addValue(V v) {
    super.addItem(v);
  }

  @SafeVarargs
  public final void setItems(V... v) {
    super.removeAllItems();
    for (V x : v) {
      addValue(x);
    }
  }

  public void setItems(Iterable<V> v) {
    super.removeAllItems();
    for (V x : v) {
      addValue(x);
    }
  }

  @Override
  @Deprecated
  public void addItem(Object item) {
    addValue((V) item);
    //throw new UnsupportedOperationException("unchecked use of addItem");
  }

  @Override
  public void setWriteable(boolean b) {
    setEnabled(b);
  }

  @Override
  public void markWrongInput() {
    Tools.showHint(this);
  }

  @Override
  public boolean validInput() {
    return getSelectedIndex() > -1;
  }
}
