package kernbeisser.Windows.TabbedPane;

import java.util.ArrayList;
import java.util.List;
import kernbeisser.Windows.MVC.Controller;
import kernbeisser.Windows.MVC.IModel;
import kernbeisser.Windows.ViewContainer;
import kernbeisser.Windows.ViewContainers.JFrameWindow;
import lombok.Getter;
import lombok.experimental.Delegate;

public class TabbedPaneModel implements IModel<TabbedPaneController> {
  public static TabbedPaneController MAIN_PANEL = createWithWindow();

  @Delegate @Getter private final List<ViewContainer> tabs = new ArrayList<>();

  private static TabbedPaneController createWithWindow() {
    TabbedPaneController controller = new TabbedPaneController();
    controller.openIn(new JFrameWindow());
    return controller;
  }

  public static boolean resetMainPanel() {
    if (MAIN_PANEL.requestClose()) {
      MAIN_PANEL.notifyClosed();
      MAIN_PANEL = createWithWindow();
      return true;
    }
    return false;
  }

  void removeController(Controller<?, ?> controller) {
    tabs.removeIf(e -> e.getLoaded() != null && e.getLoaded().equals(controller));
  }
}
