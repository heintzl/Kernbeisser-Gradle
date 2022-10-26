package kernbeisser.Forms.ObjectForm.Components;

import java.awt.event.ActionEvent;
import java.util.Collection;
import java.util.Optional;
import javax.swing.*;
import kernbeisser.CustomComponents.ObjectTable.Column;
import kernbeisser.Exeptions.PermissionKeyRequiredException;
import kernbeisser.Forms.ObjectForm.ObjectFormComponents.ObjectFormComponent;
import kernbeisser.Forms.ObjectForm.Properties.BoundedReadProperty;
import kernbeisser.Forms.ObjectForm.Properties.PredictableModifiable;
import kernbeisser.Security.Utils.Getter;
import kernbeisser.Windows.CloseEvent;
import kernbeisser.Windows.CollectionView.CollectionController;
import kernbeisser.Windows.MVC.IView;
import kernbeisser.Windows.ViewContainers.SubWindow;

// is only read because it performs operations on the object but the object stays the same
public class AccessCheckingCollectionEditor<P, C extends Collection<V>, V> extends JButton
    implements ObjectFormComponent<P>, BoundedReadProperty<P, C>, PredictableModifiable<P> {
  private final Getter<P, C> getter;

  private C data;

  private boolean enabled = true;

  private final Column<V>[] columns;

  private final Source<V> values;

  private CloseEvent closeEvent;

  private CollectionController<V> collectionController;

  private int searchBoxScope = 0;

  @SafeVarargs
  public AccessCheckingCollectionEditor(
      Getter<P, C> getter, Source<V> values, Column<V>... columns) {
    this.values = values;
    this.getter = getter;
    this.columns = columns;
    addActionListener(this::trigger);
  }

  void trigger(ActionEvent event) {
    collectionController = new CollectionController<>(data, values, columns);
    if (closeEvent != null) {
      this.collectionController.getView().addSearchbox(searchBoxScope);
      collectionController.addCloseEvent(closeEvent);
    }
    collectionController.openIn(new SubWindow(IView.traceViewContainer(getParent())));
  }

  public AccessCheckingCollectionEditor<P, C, V> withCloseEvent(CloseEvent closeEvent) {
    this.closeEvent = closeEvent;
    return this;
  }

  public AccessCheckingCollectionEditor<P, C, V> withSearchbox(int scope) {
    try {
      collectionController.getView().addSearchbox(scope);
      collectionController.getView().clearSearchBox();
    } catch (NullPointerException e) {
      searchBoxScope = scope;
    }
    return this;
  }

  @Override
  public void setReadable(boolean b) {
    super.setEnabled(b && this.enabled);
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
  public void setEnabled(boolean enabled) {
    super.setEnabled(enabled);
    this.enabled = enabled;
  }

  public C getData() {
    return data;
  }

  @Override
  public boolean isPropertyModifiable(P parent) {
    return Optional.ofNullable(data)
        .map(e -> e.getClass().getSimpleName().startsWith("Unmodifiable"))
        .orElse(true);
  }
}
