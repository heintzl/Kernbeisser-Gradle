package kernbeisser.Windows.WindowImpl;

import kernbeisser.Windows.Controller;
import kernbeisser.Windows.Pay.PayModel;
import kernbeisser.Windows.Pay.PayView;
import kernbeisser.Windows.Window;
import kernbeisser.Windows.WindowCloseEvent;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;

public class SubWindow extends JDialog implements kernbeisser.Windows.Window {

    private final Controller<?,?> controller;

    public SubWindow(Controller<PayView,PayModel> controller, Window window) {
        super(window.getController().getView().getTopComponent());
        this.controller = controller;
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    @Override
    public void addCloseEventListener(WindowCloseEvent runnable) {
        addWindowListener(runnable);
    }

    @Override
    public void simulateCloseEvent() {
        this.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
    }

    @Override
    public void setIcon(Image image) {
        setIconImage(image);
    }

    @Override
    public void open() {
        setVisible(true);
    }

    @Override
    public void close() {
        setVisible(false);
    }

    @Override
    public void kill() {
        dispose();
    }

    @Override
    public boolean noAccess() {
        JOptionPane.showMessageDialog(this,"Sie haben keine Berechtigung dieses Fenster zu öffen!");
        return false;
    }

    @Override
    public Controller<?,?> getController() {
        return controller;
    }

    @Override
    public void setContent(JComponent content) {
        add(content);
    }
}
