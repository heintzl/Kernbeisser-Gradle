package kernbeisser.Windows.MVC.ComponentController;

import javax.swing.JComponent;
import jiconfont.IconCode;
import jiconfont.icons.font_awesome.FontAwesome;
import kernbeisser.Windows.MVC.Controller;
import lombok.Getter;

public class ComponentController extends Controller<ComponentView, ComponentModel> {
  @Getter private final JComponent data;
  @Getter private final String title;
  @Getter private final IconCode icon;

  public ComponentController(JComponent component) {
    this(component, "");
  }

  public ComponentController(JComponent component, String title) {
    this(component, title, FontAwesome.WINDOW_MAXIMIZE);
  }

  public ComponentController(JComponent component, String title, IconCode icon) {
    super(new ComponentModel());
    data = component;
    this.title = title;
    this.icon = icon;
  }

  @Override
  public void fillView(ComponentView componentView) {}
}
