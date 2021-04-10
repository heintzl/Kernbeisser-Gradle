package kernbeisser.Forms.ObjectForm.Components;

import java.awt.event.ActionEvent;
import java.util.Collection;
import java.util.Optional;
import javax.swing.*;
import kernbeisser.CustomComponents.ObjectTable.Column;
import kernbeisser.Exeptions.PermissionKeyRequiredException;
import kernbeisser.Forms.ObjectForm.ObjectFormComponents.ObjectFormComponent;
import kernbeisser.Forms.ObjectForm.Properties.BoundedReadProperty;
import kernbeisser.Forms.ObjectForm.Properties.Predictable;
import kernbeisser.Security.Access.Access;
import kernbeisser.Security.Utils.Getter;
import kernbeisser.Windows.CollectionView.CollectionController;
import kernbeisser.Windows.MVC.IView;
import kernbeisser.Windows.ViewContainers.SubWindow;

// is only read because it performs operations on the object but the object stays the same
public class AccessCheckingCollectionEditor<P, C extends Collection<V>, V> extends JButton
    implements ObjectFormComponent<P>, BoundedReadProperty<P, C>, Predictable<P> {
  private final Getter<P, C> getter;

  private C data;

  private final Column<V>[] columns;

  private final Source<V> values;

  @SafeVarargs
  public AccessCheckingCollectionEditor(
      Getter<P, C> getter, Source<V> values, Column<V>... columns) {
    this.values = values;
    this.getter = getter;
    this.columns = columns;
    addActionListener(this::trigger);
  }

  void trigger(ActionEvent event) {
    new CollectionController<V>(data, values, columns)
        .openIn(new SubWindow(IView.traceViewContainer(getParent())));
  }

  @Override
  public void setReadable(boolean b) {
    setEnabled(b);
  }

  @Override
  public C get(P p) throws PermissionKeyRequiredException {
    return getter.get(p);
  }

  @Override
  public void setData(C vs) {
    data = vs;
  }

  @Override
  public boolean isPropertyReadable(P parent) {
    return Access.hasPermission(getter, parent);
  }

  @Override
  public boolean isPropertyWriteable(P parent) {
    return Optional.ofNullable(data)
        .map(e -> e.getClass().getSimpleName().startsWith("Unmodifiable"))
        .orElse(true);
  }
}
