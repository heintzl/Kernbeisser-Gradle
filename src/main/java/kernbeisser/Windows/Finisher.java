package kernbeisser.Windows;

import kernbeisser.Finishable;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Finisher is a Class for custom close operations
 */
public class Finisher extends WindowAdapter {
    private Finishable window;
    private JFrame jFrame;
    Finisher(Finishable window, JFrame jFrame){
        this.window=window;
        this.jFrame = jFrame;
    }

    /**
     * if the window is closing it will run the custom finish
     * event
     * @see Finishable
     */
    @Override
    public void windowClosing(WindowEvent e) {
        window.finish(jFrame);
    }
}
