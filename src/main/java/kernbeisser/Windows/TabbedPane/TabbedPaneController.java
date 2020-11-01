package kernbeisser.Windows.TabbedPane;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;
import kernbeisser.Windows.MVC.Controller;
import kernbeisser.Windows.ViewContainer;
import lombok.var;
import org.apache.commons.collections4.CollectionUtils;

public class TabbedPaneController extends Controller<TabbedPaneView, TabbedPaneModel> {

  public TabbedPaneController() {
    super(new TabbedPaneModel());
  }

  @Override
  public void fillView(TabbedPaneView tabbedPaneView) {}

  public void closeViewContainer(ViewContainer container, int index) {
    var view = getView();
    if (container.getLoaded() == null) view.removeTab(index);
    else {
      if (container.getLoaded().requestClose()) {
        container.getLoaded().notifyClosed();
        view.removeTab(index);
        getModel().remove(container);
      }
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

  public ViewContainer createTabViewContainer() {
    var view = getView();
    ViewContainer container = view.prepareViewContainer();
    getModel().add(container);
    return container;
  }

  public void kill(Controller<?, ?> controller) {
    var view = getView();
    view.removeTab(view.indexOf(controller));
    getModel().removeController(controller);
  }
}
