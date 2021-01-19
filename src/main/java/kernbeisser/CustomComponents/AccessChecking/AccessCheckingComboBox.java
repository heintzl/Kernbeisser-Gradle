package kernbeisser.CustomComponents.AccessChecking;

import java.awt.Color;
import javax.swing.*;
import kernbeisser.CustomComponents.ComboBox.AdvancedComboBox;
import kernbeisser.Exeptions.CannotParseException;
import kernbeisser.Exeptions.PermissionKeyRequiredException;
import kernbeisser.Security.Proxy;
import kernbeisser.Useful.Tools;
import org.jetbrains.annotations.Nullable;

public class AccessCheckingComboBox<P, V> extends AdvancedComboBox<V> implements
    ObjectFormComponent<P>,
    BoundedWriteProperty<P,V>, BoundedReadProperty<P,V>, Predictable<P> {
  private boolean inputChanged = false;

  @lombok.Setter private boolean allowNull;

  private static final Object NO_READ_PERMISSION = "<Keine Leseberechtigung>";

  private final Setter<P, V> setter;
  private final Getter<P, V> getter;

  public AccessCheckingComboBox(Getter<P, V> getter, Setter<P, V> setter) {
    this.getter = getter;
    this.setter = setter;
    addActionListener(e -> inputChanged = true);
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
  public void setSelectedItem(Object anObject) {
    inputChanged = true;
    super.setSelectedItem(anObject);
  }

  @Override
  public void setReadable(boolean b) {
    if (b) {
      super.removeItem(NO_READ_PERMISSION);
    } else {
      super.removeItem(NO_READ_PERMISSION);
      super.setSelectedItem(NO_READ_PERMISSION);
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
  public void setPropertyEditable(boolean v) {
    super.setEnabled(v);
  }

  @Override
  public void setInvalidInput() {
    setForeground(Color.RED);
  }

  @Override
  public V get(P p) throws PermissionKeyRequiredException {
    return getter.get(p);
  }

  @Override
  public boolean isPropertyReadable(P parent) {
    return Proxy.hasPermission(getter,parent);
  }

  @Override
  public boolean isPropertyWriteable(P parent) {
    return Proxy.hasPermission(setter,parent);
  }

  @Override
  public void setData(V v) {
    super.setSelectedItem(v);
  }

  @Override
  public void set(P p, V t) throws PermissionKeyRequiredException {
    if(inputChanged) setter.set(p,t);
  }

  @Override
  public V getData() throws CannotParseException {
    try {
      return getSelected();
    }catch (NullPointerException e){
      if(allowNull)return null;
      else throw new CannotParseException("field requires not null property");
    }
  }
}
