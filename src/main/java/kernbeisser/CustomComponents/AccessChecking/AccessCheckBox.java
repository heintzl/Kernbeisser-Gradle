package kernbeisser.CustomComponents.AccessChecking;

import java.awt.Color;
import javax.swing.*;
import kernbeisser.Exeptions.PermissionKeyRequiredException;
import kernbeisser.Security.Proxy;

public class AccessCheckBox<P> extends JCheckBox
    implements ObjectFormComponent<P>,
        BoundedReadProperty<P, Boolean>,
        BoundedWriteProperty<P, Boolean>,
        Predictable<P> {

  private boolean inputChanged = false;

  private final Setter<P, Boolean> setter;
  private final Getter<P, Boolean> getter;

  public AccessCheckBox(Getter<P, Boolean> getter, Setter<P, Boolean> setter) {
    this.getter = getter;
    this.setter = setter;
    addActionListener(e -> inputChanged = true);
  }

  @Override
  public void setReadable(boolean b) {
    if (!b) {
      setText(getText() + "[Unbekannt]");
    } else {
      setText(getText().replace("[Unbekannt]", ""));
    }
  }

  @Override
  public void setPropertyEditable(boolean v) {
    setEnabled(v);
  }

  @Override
  public void setInvalidInput() {
    setForeground(Color.RED);
  }

  @Override
  public Boolean get(P p) throws PermissionKeyRequiredException {
    return getter.get(p);
  }

  @Override
  public boolean isPropertyReadable(P parent) {
    return Proxy.hasPermission(getter, parent);
  }

  @Override
  public boolean isPropertyWriteable(P parent) {
    return Proxy.hasPermission(setter, parent);
  }

  @Override
  public void setData(Boolean aBoolean) {
    setSelected(aBoolean);
  }

  @Override
  public void set(P p, Boolean t) throws PermissionKeyRequiredException {
    if (inputChanged) {
      setter.set(p, t);
    }
  }

  @Override
  public Boolean getData() {
    return isSelected();
  }
}
