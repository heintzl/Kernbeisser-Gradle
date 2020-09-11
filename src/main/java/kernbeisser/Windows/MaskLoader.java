package kernbeisser.Windows;

import kernbeisser.Enums.Mode;
import kernbeisser.Windows.MVC.IController;

public interface MaskLoader<T> {
  IController<?, ?> accept(T t, Mode m);
}
