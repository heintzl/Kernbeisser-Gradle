package kernbeisser.Windows.TabbedPanel;

import java.awt.*;
import javax.swing.*;
import kernbeisser.Windows.View;
import org.jetbrains.annotations.NotNull;

public class TabbedPaneView implements View<TabbedPaneController> {
  private JTabbedPane tabbedPane;
  private JPanel main;

  @Override
  public void initialize(TabbedPaneController controller) {}

  @Override
  public @NotNull JComponent getContent() {
    return main;
  }

  void addTab(Component tabHeader, Component tabContent, int index) {
    tabbedPane.addTab("", tabContent);
    tabbedPane.setTabComponentAt(index, tabHeader);
  }

  void removeTab(int index) {
    tabbedPane.removeTabAt(index);
  }

  public int getCurrentTabIndex() {
    return tabbedPane.getSelectedIndex();
  }

  public void setSelected(int index) {
    tabbedPane.setSelectedIndex(index);
  }
}
