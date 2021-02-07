package kernbeisser.Windows.TabbedPane;

import javax.swing.*;
import kernbeisser.Windows.MVC.IView;
import kernbeisser.Windows.MVC.Linked;
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
    if (index != 0) setSelected(0);
  }

  public JTabbedPane getTabbedPane() {
    return tabbedPane;
  }

  public void setSelected(int index) {
    tabbedPane.setSelectedIndex(index);
  }
}
