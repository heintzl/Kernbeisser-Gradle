package kernbeisser.Windows.TabbedPanel;

import jiconfont.IconCode;
import kernbeisser.Windows.MVC.IController;

public interface Tab {
  IconCode getIcon();

  IController<?, ?> getController();

  String getTitle();

  default boolean commitClose() {
    return true;
  }
}
