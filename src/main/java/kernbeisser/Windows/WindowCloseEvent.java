package kernbeisser.Windows;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

public interface WindowCloseEvent extends WindowListener {
    @Override
    default void windowOpened(WindowEvent ignored) {
    }

    @Override
    default void windowClosed(WindowEvent e) {
    }

    @Override
    default void windowIconified(WindowEvent ignored) {
    }

    @Override
    default void windowDeiconified(WindowEvent ignored) {
    }

    @Override
    default void windowActivated(WindowEvent ignored) {
    }

    @Override
    default void windowDeactivated(WindowEvent ignored) {
    }
}
