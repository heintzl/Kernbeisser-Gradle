package kernbeisser.Windows;

import javax.swing.*;

public interface Finishable {
    /**
     * the method which runs if a window become closed if a Finisher is added as a Window Listener
     *
     * @see kernbeisser.Windows.Finisher
     */
    void finish();
}
