package kernbeisser.Windows;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Finisher is a Class for custom close operations
 */
public class Finisher extends WindowAdapter {
    private Finishable window;
    public Finisher(Finishable window){
        this.window=window;
    }

    /**
     * if the window is closing it will run the custom finish
     * event
     * @see Finishable
     */
    @Override
    public void windowClosing(WindowEvent e) {
        window.finish();
    }
}
