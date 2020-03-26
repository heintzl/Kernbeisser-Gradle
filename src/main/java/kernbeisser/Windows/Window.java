package kernbeisser.Windows;


import kernbeisser.Enums.Key;
import kernbeisser.Useful.Images;
import kernbeisser.Windows.LogIn.LogInModel;

import javax.swing.*;
import java.awt.*;

public class Window extends JFrame {
    private static final Image STANDARD_IMAGE;
    private static Window lastOpened;
    private boolean access = true;

    static {
        STANDARD_IMAGE = Images.getImage("Icon.png");
    }

    private final Window current;

    public Window(Window currentWindow, Key... required) {
        this.current = currentWindow;
        if (required.length != 0 && !LogInModel.getLoggedIn().hasPermission(required)) {
            access = false;
            JOptionPane.showMessageDialog(currentWindow, "Sie haben keine Berechtigung dieses Fenster zu öffnen");
            windowInitialized();
            back();
            return;
        }
        setIconImage(STANDARD_IMAGE);
        lastOpened = this;
    }

    public final void windowInitialized(){
        if(!access)return;
        if(getSize().height == 0 && getSize().width == 0) {
            pack();
            if (current != null) {
                setSize(current.getSize());
            }
        }
        setLocationRelativeTo(null);
        addWindowListener((WindowCloseEvent) e -> {
            back();
        });
        if (current != null) {
            current.close();
        }
        this.open();
    }

    public static Window getLastOpened() {
        return lastOpened;
    }

    public final void back() {
        if (current == null) {
            finish();
            kill();
        } else {
            current.open();
            finish();
            kill();
        }
    }

    protected void open() {
        setVisible(true);
    }

    protected void close() {
        setVisible(false);
    }

    /**
     * Custom action after the window become cosed
     **/
    public void finish() {
    }

    /**
     * the function to dispose the window can be overridden if another close option is necessary
     **/
    protected void kill() {
        close();
        dispose();
    }

}
