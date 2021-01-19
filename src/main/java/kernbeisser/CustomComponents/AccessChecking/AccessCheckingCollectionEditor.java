package kernbeisser.CustomComponents.AccessChecking;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.util.Collection;
import javax.swing.*;
import kernbeisser.CustomComponents.ObjectTable.Column;
import kernbeisser.Exeptions.CannotParseException;
import kernbeisser.Exeptions.PermissionKeyRequiredException;
import kernbeisser.Useful.Tools;
import kernbeisser.Windows.CollectionView.CollectionController;
import kernbeisser.Windows.MVC.IView;
import kernbeisser.Windows.ViewContainers.SubWindow;

public class AccessCheckingCollectionEditor<P, C extends Collection<V>, V> extends JButton
    implements ObjectFormComponent<P>, BoundedReadProperty<P,C>, BoundedWriteProperty<P,C> {
  private final Getter<P, C> getter;
  private final Setter<P, C> setter;

  private boolean editable;

  private C data;

  private final Column<V>[] columns;

  private final Collection<V> values;

  public AccessCheckingCollectionEditor(
      Getter<P, C> getter, Setter<P, C> setter, Collection<V> values, Column<V>... columns) {
    this.values = values;
    this.getter = getter;
    this.setter = setter;
    this.columns = columns;
    addActionListener(this::trigger);
  }

  void trigger(ActionEvent event) {
    new CollectionController<V>(data, values, editable, columns)
        .openIn(new SubWindow(IView.traceViewContainer(getParent())));
  }


  @Override
  public void setReadable(boolean b) {
    setEnabled(false);
  }

  @Override
  public void setPropertyEditable(boolean v) {
    editable = v;
  }

  @Override
  public void setInvalidInput() {
    setForeground(Color.RED);
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
  public void set(P p, C t) throws PermissionKeyRequiredException {
    setter.set(p,t);
  }

  @Override
  public C getData() throws CannotParseException {
    return data;
  }
}
