package kernbeisser.Windows;

import kernbeisser.Enums.Mode;

public interface MaskLoader<T> {
  Controller<?, ?> accept(T t, Mode m);
}
