package kernbeisser.Windows;

import kernbeisser.Enums.Mode;

public interface MaskLoader <T>{
    void accept(Window current, T t, Mode m);
}
