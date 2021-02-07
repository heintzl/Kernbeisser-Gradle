package kernbeisser.Windows.TabbedPane;

import java.awt.Color;
import java.lang.ref.WeakReference;
import javax.swing.SwingUtilities;
import jiconfont.swing.IconFontSwing;
import kernbeisser.Windows.MVC.Controller;
import kernbeisser.Windows.MVC.IView;
import kernbeisser.Windows.ViewContainer;

public class TabViewContainer implements ViewContainer {

  private final TabbedPaneController parent;

  private Controller<?, ?> loaded;

  private WeakReference<TabViewContainer> before;

  public TabViewContainer(TabbedPaneController parent) {
    this.parent = parent;
  }

  @Override
  public void loadController(Controller<?, ?> controller) {
    this.loaded = controller;
    before = new WeakReference<>(parent.getSelectedTabViewContainer());
    TabbedPaneView tabbedPaneView = parent.getView();
    IView<?> view = controller.getView();
    loaded = controller;
    int indexInTable;
    try {
      indexInTable = parent.indexOf(controller);
    } catch (UnsupportedOperationException e) {
      tabbedPaneView.getTabbedPane().addTab("", view.getContent());
      indexInTable = parent.indexOf(controller);
    }
    tabbedPaneView
        .getTabbedPane()
        .setTabComponentAt(
            indexInTable,
            new DefaultTab(
                    IconFontSwing.buildIcon(view.getTabIcon(), 20, new Color(0xFF00CCFF)),
                    view.getTitle(),
                    this::requestClose,
                    () ->
                        tabbedPaneView
                            .getTabbedPane()
                            .setSelectedIndex(parent.indexOf(getLoaded())))
                .getMain());
    tabbedPaneView.getTabbedPane().setSelectedIndex(indexInTable);
    SwingUtilities.invokeLater(() -> controller.getView().getFocusOnInitialize().requestFocus());
  }

  @Override
  public Controller<?, ?> getLoaded() {
    return loaded;
  }

  @Override
  public void requestClose() {
    parent.closeViewContainer(this, parent.indexOf(loaded), before.get());
  }
}
