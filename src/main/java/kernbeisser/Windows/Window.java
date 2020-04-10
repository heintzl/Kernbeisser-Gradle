package kernbeisser.Windows;

import kernbeisser.Useful.Images;
import kernbeisser.Windows.LogIn.LogInModel;

import javax.swing.*;
import java.awt.*;

public interface Window {
    Image STANDARD_IMAGE = Images.getImage("Icon.png");
    void addCloseEventListener(WindowCloseEvent runnable);
    void simulateCloseEvent();
    void setIcon(Image image);
    void open();
    void close();
    void kill();
    void setSize(Dimension dimension);
    boolean noAccess();

    Controller<?,?> getController();
    default boolean commitClose(){return true;};

    default void back(){
        simulateCloseEvent();
    }

    default Window openWindow(Window window){
        if (this.commitClose()&&(LogInModel.getLoggedIn()==null||LogInModel.getLoggedIn().hasPermission(window.getController().getRequiredKeys())||noAccess())) {
            window.getController().initView();
            window.setIcon(STANDARD_IMAGE);
            window.open();
            window.addCloseEventListener(e -> {
                window.close();
                window.kill();
                this.open();
                if(getController()!=null)
                this.getController().open();
            });
            this.close();
        }
        return window;
    }
    static final Window NEW_WINDOW = new Window() {
        @Override
        public void addCloseEventListener(WindowCloseEvent runnable) {

        }

        @Override
        public void simulateCloseEvent() {
        }

        @Override
        public void setIcon(Image image) {
        }

        @Override
        public void open() {
        }

        @Override
        public void close() {
        }

        @Override
        public void kill() {
        }

        @Override
        public boolean noAccess() {
            return false;
        }

        @Override
        public Controller<?,?> getController() {
            return null;
        }

        @Override
        public void setContent(JComponent content) { }
        @Override
        public void setSize(Dimension dimension) { }
    };

    void setContent(JComponent content);
}
