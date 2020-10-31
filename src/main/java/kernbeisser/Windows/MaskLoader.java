package kernbeisser.Windows;

import kernbeisser.Enums.Mode;
import kernbeisser.Windows.MVC.Controller;

public interface MaskLoader<T> {
  Controller<?, ?> accept(T t, Mode m);
}
