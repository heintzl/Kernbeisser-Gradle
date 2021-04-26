package kernbeisser.Forms.ObjectForm.Components;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.util.Optional;
import kernbeisser.CustomComponents.ComboBox.AdvancedComboBox;
import kernbeisser.Exeptions.PermissionKeyRequiredException;
import kernbeisser.Forms.ObjectForm.Exceptions.CannotParseException;
import kernbeisser.Forms.ObjectForm.ObjectFormComponents.ObjectFormComponent;
import kernbeisser.Forms.ObjectForm.Properties.BoundedReadProperty;
import kernbeisser.Forms.ObjectForm.Properties.BoundedWriteProperty;
import kernbeisser.Forms.ObjectForm.Properties.Predictable;
import kernbeisser.Security.Access.Access;
import kernbeisser.Security.Utils.Getter;
import kernbeisser.Security.Utils.Setter;
import org.jetbrains.annotations.Nullable;

public class AccessCheckingComboBox<P, V> extends AdvancedComboBox<V>
    implements ObjectFormComponent<P>,
        BoundedWriteProperty<P, V>,
        BoundedReadProperty<P, V>,
        Predictable<P> {
  private boolean inputChanged = false;

  private final Color foregroundDefault = getForeground();
  private final Color backgroundDefault = getBackground();

  @lombok.Setter private boolean allowNull;

  private static final Object NO_READ_PERMISSION = "<Keine Leseberechtigung>";

  private final Source<V> source;

  private final Setter<P, V> setter;
  private final Getter<P, V> getter;

  public AccessCheckingComboBox(Getter<P, V> getter, Setter<P, V> setter, Source<V> source) {
    this.getter = getter;
    this.setter = setter;
    this.source = source;
    addActionListener(this::inputChanged);
  }

  private void inputChanged(ActionEvent itemEvent) {
    inputChanged = true;
    removeInvalidInputMark();
  }

  private void removeInvalidInputMark() {
    setForeground(foregroundDefault);
    setBackground(backgroundDefault);
  }

  private void pullSource() {
    setItems(source.query());
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
    if (getSelected().isPresent()) {
      setForeground(Color.RED);
    } else {
      setBackground(new Color(0xFF9999));
    }
  }

  @Override
  public V get(P p) throws PermissionKeyRequiredException {
    return getter.get(p);
  }

  @Override
  public boolean isPropertyReadable(P parent) {
    return Access.hasPermission(getter, parent);
  }

  @Override
  public boolean isPropertyWriteable(P parent) {
    return Access.hasPermission(setter, parent);
  }

  @Override
  public void setData(V v) {
    pullSource();
    super.setSelectedItem(v);
  }

  @Override
  public void set(P p, V t) throws PermissionKeyRequiredException {
    if (inputChanged) setter.set(p, t);
  }

  @Override
  public V getData() throws CannotParseException {
    Optional<V> optionalV = getSelected();
    if (allowNull) return (optionalV).orElse(null);
    else return optionalV.orElseThrow(CannotParseException::new);
  }
}
