package kernbeisser.Windows;


import javax.swing.*;

public abstract class Window extends JFrame {
    public Window(Window currentWindow){
        addWindowListener((WindowCloseEvent) e -> {
            currentWindow.open();
        });
        close();
    }
    public abstract void open();
    public abstract void close();

}
