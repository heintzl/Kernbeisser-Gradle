package kernbeisser.Windows;


import kernbeisser.Useful.Images;

import javax.swing.*;
import java.awt.*;

public class Window extends JFrame {
    private final Window current;
    private static Window lastOpened;
    private static final Image STANDARD_IMAGE;
    static {
        STANDARD_IMAGE = Images.getImage("Icon.png");
    }
    public Window(Window currentWindow){
        setIconImage(STANDARD_IMAGE);
        pack();
        if(currentWindow!=null)
        setSize(currentWindow.getSize());
        setLocationRelativeTo(null);
        this.current=currentWindow;
        addWindowListener((WindowCloseEvent)e -> {
            back();
        });
        if(currentWindow!=null)
        currentWindow.close();
        this.open();
        lastOpened=this;
    }

    public static Window getLastOpened() {
        return lastOpened;
    }
    public final void back(){
        if(current==null){
            finish();
            kill();
        } else {
            current.open();
            finish();
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
    public void finish(){}
    /**the function to dispose the window can be overridden if another close option is necessary**/
    protected void kill(){
        close();
        dispose();
    }

}
