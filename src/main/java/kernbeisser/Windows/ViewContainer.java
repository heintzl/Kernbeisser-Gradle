package kernbeisser.Windows;

import kernbeisser.Windows.MVC.Controller;
import org.jetbrains.annotations.NotNull;

public interface ViewContainer {
  void loadController(@NotNull Controller<?, ?> controller);
  // returns null if no controller is loaded
  Controller<?, ?> getLoaded();

  void requestClose();

  // kills the view Container without asking the view for closing and notifying it when it's closed
  void kill();
}
