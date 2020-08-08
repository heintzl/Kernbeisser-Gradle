package kernbeisser.Windows.TabbedPanel;

import jiconfont.IconCode;
import kernbeisser.Windows.MVC.Controller;

public interface Tab {
  IconCode getIcon();

  Controller<?, ?> getController();

  String getTitle();

  default boolean commitClose() {
    return true;
  }
}
