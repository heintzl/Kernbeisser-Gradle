package kernbeisser.Windows;

import kernbeisser.Windows.MVC.Controller;

public interface ViewContainer {
  void loadController(Controller<?, ?> controller);
  // returns null if no controller is loaded
  Controller<?, ?> getLoaded();

  void requestClose();
}
