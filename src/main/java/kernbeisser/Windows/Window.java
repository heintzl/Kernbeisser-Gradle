package kernbeisser.Windows;


import javax.swing.*;

public abstract class Window extends JFrame {
    public Window(Window currentWindow){
        addWindowListener((WindowCloseEvent) e -> {
            currentWindow.open();
        });
        close();
    }
    protected abstract void open();
    protected abstract void close();

}
