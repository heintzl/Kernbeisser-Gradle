package kernbeisser.Windows.MVC.ComponentController;

import java.awt.GridLayout;
import javax.swing.JComponent;
import javax.swing.JPanel;
import jiconfont.IconCode;
import jiconfont.icons.font_awesome.FontAwesome;
import kernbeisser.Windows.MVC.IView;
import kernbeisser.Windows.MVC.Linked;
import org.jetbrains.annotations.NotNull;

public class ComponentView implements IView<ComponentController> {

  @Linked private JComponent data;
  @Linked private String title;
  @Linked private IconCode icon;

  private JPanel wrapperLayer;

  @Override
  public void initialize(ComponentController controller) {
    wrapperLayer = new JPanel();
    wrapperLayer.setLayout(new GridLayout(1, 1));
    wrapperLayer.add(data);
  }

  @Override
  public @NotNull JComponent getContent() {
    return wrapperLayer;
  }

  @Override
  public String getTitle() {
    return title;
  }

  @Override
  public IconCode getTabIcon() {
    return icon != null ? icon : FontAwesome.WINDOW_MAXIMIZE;
  }
}
