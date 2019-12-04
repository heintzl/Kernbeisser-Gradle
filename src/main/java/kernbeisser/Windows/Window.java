package kernbeisser.Windows;


import kernbeisser.Useful.Images;

import javax.swing.*;
import java.awt.*;

public abstract class Window extends JFrame {
    private final Window current;
    private static final Image STANDARD_IMAGE;
    static {
        STANDARD_IMAGE = Images.getImage("Icon.png");
    }
    public Window(Window currentWindow){
        setIconImage(STANDARD_IMAGE);
        this.current=currentWindow;
        addWindowListener((WindowCloseEvent)e -> {
            back();
        });
        if(currentWindow!=null)
        currentWindow.close();
        this.open();
    }

    public final void back(){
        if(current==null){
            finish(this);
            kill();
            System.exit(0);
        } else {
            current.open();
            finish(this);
            kill();
        }
    }
    protected void open(){
        setVisible(true);
    }
    protected void close(){
        setVisible(false);
    }
    /** Custom action after the window become cosed**/
    public void finish(Window window){}
    /**the function to dispose the window can be overridden if another close option is necessary**/
    protected void kill(){
        close();
        dispose();
    }

}
