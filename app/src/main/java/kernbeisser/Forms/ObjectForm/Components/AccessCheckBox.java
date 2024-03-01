package kernbeisser.Forms.ObjectForm.Components;

import java.awt.*;
import javax.swing.*;
import kernbeisser.Forms.ObjectForm.ObjectFormComponents.ObjectFormComponent;
import kernbeisser.Forms.ObjectForm.Properties.BoundedReadProperty;
import kernbeisser.Forms.ObjectForm.Properties.BoundedWriteProperty;
import kernbeisser.Forms.ObjectForm.Properties.PredictableModifiable;
import kernbeisser.Security.Utils.Getter;
import kernbeisser.Security.Utils.Setter;
import rs.groump.Access;
import rs.groump.AccessDeniedException;

public class AccessCheckBox<P> extends JCheckBox
    implements ObjectFormComponent<P>,
        BoundedReadProperty<P, Boolean>,
        BoundedWriteProperty<P, Boolean>,
        PredictableModifiable<P> {

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
  public void setPropertyModifiable(boolean v) {
    setEnabled(v);
  }

  @Override
  public void setInvalidInput() {
    setForeground(Color.RED);
  }

  @Override
  public Boolean get(P p) throws AccessDeniedException {
    return getter.get(p);
  }

  @Override
  public boolean isPropertyModifiable(P parent) {
    return Access.getAccessManager().hasAccess(parent, Access.peekPermissions(setter));
  }

  @Override
  public void setData(Boolean aBoolean) {
    setSelected(aBoolean);
  }

  @Override
  public void set(P p, Boolean t) throws AccessDeniedException {
    if (inputChanged) {
      setter.set(p, t);
    }
  }

  @Override
  public Boolean getData() {
    return isSelected();
  }
}
