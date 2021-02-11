package kernbeisser.Windows.TabbedPane;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;
import kernbeisser.Windows.MVC.Controller;
import kernbeisser.Windows.MVC.ControllerReference;
import kernbeisser.Windows.ViewContainer;
import lombok.var;
import org.apache.commons.collections4.CollectionUtils;

public class TabbedPaneController extends Controller<TabbedPaneView, TabbedPaneModel> {

  public TabbedPaneController() {
    super(new TabbedPaneModel());
  }

  @Override
  public void fillView(TabbedPaneView tabbedPaneView) {}

  public void closeViewContainer(ViewContainer container, int index, ViewContainer newFocus) {
    var view = getView();
    if (container.getLoaded() == null) view.removeTab(index);
    else {
      if (container.getLoaded().requestClose()) {
        container.getLoaded().notifyClosed();
        view.removeTab(index);
        getModel().remove(container);
      }
    }
    if (newFocus != null) {
      getView().setSelected(model.indexOf(newFocus));
    }
  }

  @Override
  public Collection<Controller<?, ?>> getSubControllers() {
    return CollectionUtils.union(
        getModel().getTabs().stream()
            .map(ViewContainer::getLoaded)
            .collect(Collectors.toCollection(ArrayList::new)),
        super.getSubControllers());
  }

  public TabViewContainer createTabViewContainer() {
    TabViewContainer container = new TabViewContainer(this);
    getModel().add(container);
    return container;
  }

  public void kill(Controller<?, ?> controller) {
    var view = getView();
    view.removeTab(indexOf(controller));
    getModel().removeController(controller);
  }

  int indexOf(Controller<?, ?> controller) {
    for (int i = 0; i < getView().getTabbedPane().getTabCount(); i++) {
      if (ControllerReference.isOn(getView().getTabbedPane().getComponentAt(i), controller))
        return i;
    }
    throw new UnsupportedOperationException("cannot find index for controller");
  }

  public TabViewContainer getSelectedTabViewContainer() {
    int selection = getView().getTabbedPane().getSelectedIndex();
    return selection != -1 ? model.get(selection) : null;
  }

  public int indexOf(Class<Controller<?, ?>> controllerClass) {
    for (int i = 0; i < model.size(); i++) {
      try {
        if (Objects.requireNonNull(model.get(i)).getLoaded().getClass().equals(controllerClass))
          return i;
      } catch (NullPointerException ignored) {
      }
    }
    return -1;
  }

  public boolean isInstanceOpen(Class<?> c) {
    return model.stream().parallel().anyMatch(e -> e.getLoaded().getClass().equals(c));
  }

  public void setSelectedIndex(int index) {
    getView().setSelected(index);
  }
}
