package kernbeisser.Windows.TabbedPane;

import java.util.ArrayList;
import java.util.List;
import kernbeisser.Windows.MVC.Controller;
import kernbeisser.Windows.MVC.IModel;
import kernbeisser.Windows.ViewContainers.JFrameWindow;
import lombok.Getter;
import lombok.experimental.Delegate;

public class TabbedPaneModel implements IModel<TabbedPaneController> {

  private static TabbedPaneController mainPanel;


  public static TabbedPaneController getMainPanel() {
    if (mainPanel != null) {
      return mainPanel;
    }
    mainPanel = createWithWindow();
    return mainPanel;
  }

  @Delegate @Getter private final List<TabViewContainer> tabs = new ArrayList<>();

  private static TabbedPaneController createWithWindow() {
    TabbedPaneController controller = new TabbedPaneController();
    controller.openIn(new JFrameWindow());
    return controller;
  }

  public static boolean resetMainPanel() {
    if (mainPanel.requestClose()) {
      mainPanel.notifyClosed();
      mainPanel = createWithWindow();
      return true;
    }
    return false;
  }

  void removeController(Controller<?, ?> controller) {
    tabs.removeIf(e -> e.getLoaded() != null && e.getLoaded().equals(controller));
  }
}
