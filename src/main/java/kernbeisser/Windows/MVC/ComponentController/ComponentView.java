package kernbeisser.Windows.MVC.ComponentController;

import javax.swing.JComponent;
import jiconfont.IconCode;
import jiconfont.icons.font_awesome.FontAwesome;
import kernbeisser.Windows.MVC.IView;
import kernbeisser.Windows.MVC.Linked;
import org.jetbrains.annotations.NotNull;

public class ComponentView implements IView<ComponentController> {

  @Linked private ComponentController controller;

  @Override
  public void initialize(ComponentController controller) {}

  @Override
  public @NotNull JComponent getContent() {
    return controller.getData();
  }

  @Override
  public String getTitle() {
    return controller.getTitle();
  }

  @Override
  public IconCode getTabIcon() {
    return controller != null ? controller.getIcon() : FontAwesome.WINDOW_MAXIMIZE;
  }
}
