package kernbeisser.CustomComponents.AccessChecking;

import java.util.Collection;
import javax.swing.*;
import kernbeisser.Exeptions.AccessDeniedException;
import kernbeisser.Exeptions.CannotParseException;
import kernbeisser.Useful.Tools;

public class AccessCheckingComboBox<P, V> extends JComboBox<V> implements Bounded<P, V> {
  private boolean inputChanged = false;

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

  public void setItems(Collection<V> values) {
    removeAllItems();
    values.forEach(super::addItem);
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

  @Override
  public void writeInto(P p) throws CannotParseException {
    try {
      int selectedIndex = getSelectedIndex();
      if (selectedIndex > -1) {
        setter.set(p, getItemAt(selectedIndex));
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
      setSelectedItem(null);
    }
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
