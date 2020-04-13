package kernbeisser.Windows;

import kernbeisser.Useful.Images;
import kernbeisser.Windows.LogIn.LogInModel;

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

    default Window openWindow(Window window, boolean closeWindow){
        return openWindow(this, window, closeWindow);
    }

    default void closeWindow(){
        close();
        kill();
    }

    static Window openWindow(Window parent, Window window, boolean closeWindow){
        if ((closeWindow||parent.commitClose())&&(LogInModel.getLoggedIn()==null || LogInModel.getLoggedIn().hasPermission(
                window.getController().getRequiredKeys()) || parent.noAccess())) {
            window.setIcon(STANDARD_IMAGE);
            window.open();
            window.addCloseEventListener(e -> {
                if (window.commitClose()){
                    window.closeWindow();
                    parent.open();
                }
            });
            if(closeWindow)
                parent.close();
        }
        return window;
    }

    static final Window NEW_VIEW_CONTAINER = new Window() {
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
        public void setContent(Controller<?,?> content) { }
        @Override
        public void setSize(Dimension dimension) { }
    };

    void setContent(Controller<?,?> content);
}
