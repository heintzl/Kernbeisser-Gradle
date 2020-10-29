package kernbeisser.Windows.TabbedPane;

import java.awt.Color;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import jiconfont.swing.IconFontSwing;
import kernbeisser.Windows.MVC.Controller;
import kernbeisser.Windows.MVC.ControllerReference;
import kernbeisser.Windows.MVC.IView;
import kernbeisser.Windows.MVC.Linked;
import kernbeisser.Windows.ViewContainer;
import org.jetbrains.annotations.NotNull;

public class TabbedPaneView implements IView<kernbeisser.Windows.TabbedPane.TabbedPaneController> {
  private JTabbedPane tabbedPane;
  private JPanel main;

  @Linked private TabbedPaneController controller;

  @Override
  public void initialize(TabbedPaneController controller) {}

  @Override
  public @NotNull JComponent getContent() {
    return main;
  }

  void removeTab(int index) {
    tabbedPane.removeTabAt(index);
  }

  public ViewContainer prepareViewContainer() {
    return new ViewContainer() {
      Controller<?, ?> loaded;

      @Override
      public void loadController(Controller<?, ?> controller) {
        IView<?> view = controller.getView();
        loaded = controller;
        int indexInTable;
        try {
          indexInTable = indexOf(controller);
        } catch (UnsupportedOperationException e) {
          tabbedPane.addTab("", view.getContent());
          indexInTable = indexOf(controller);
        }
        tabbedPane.setTabComponentAt(
            indexInTable,
            new DefaultTab(
                    IconFontSwing.buildIcon(view.getTabIcon(), 20, new Color(0xFF00CCFF)),
                    view.getTitle(),
                    this::close,
                    () -> tabbedPane.setSelectedIndex(indexOf(getLoaded())))
                .getMain());
        tabbedPane.setSelectedIndex(indexInTable);
      }

      private void close() {
        TabbedPaneView.this.controller.closeViewContainer(this, indexOf(loaded));
      }

      @Override
      public Controller<?, ?> getLoaded() {
        return loaded;
      }

      @Override
      public void requestClose() {
        close();
      }
    };
  }

  int indexOf(Controller<?, ?> controller) {
    for (int i = 0; i < tabbedPane.getTabCount(); i++) {
      if (ControllerReference.isOn(tabbedPane.getComponentAt(i), controller)) return i;
    }
    throw new UnsupportedOperationException("cannot find index for controller");
  }

  public void setSelected(int index) {
    tabbedPane.setSelectedIndex(index);
  }
}
