package kernbeisser.Windows;

import jiconfont.IconCode;
import kernbeisser.Enums.Key;
import kernbeisser.Windows.TabbedPanel.Tab;
import kernbeisser.Windows.TabbedPanel.TabbedPaneController;
import kernbeisser.Windows.TabbedPanel.TabbedPaneModel;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.function.BiFunction;
import java.util.function.Function;

public interface Controller<V extends View<? extends Controller<? extends V,? extends M>>,M extends Model<? extends Controller<? extends V,? extends M>>>  {
    @NotNull V getView();
    @NotNull M getModel();

    default @NotNull V getInitializedView(){
        initView();
        return getView();
    }

    void fillUI();

    Key[] getRequiredKeys();

    default boolean commitClose(){return true;}


    default void open(){}

    default void initView(){
        try {
            Method method = getView().getClass().getDeclaredMethod("initialize", Controller.class);
            method.setAccessible(true);
            method.invoke(getView(), this);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.getCause().printStackTrace();
        } catch (NoSuchMethodException e) {
            System.err.println("failed to initialize view cannot find initialize("+this.getClass()+")");
            for (Method method : getView().getClass().getDeclaredMethods()) {
                System.err.println(method);
            }
        }
        fillUI();
    }

    default Controller<V,M> withInitializedView(){
        initView();
        return this;
    }

    default <W extends Window> W openAsWindow(Window parent, Function<Controller<V,M>,W> windowFactory) {
        return openAsWindow(parent, windowFactory, true);
    }

    default <W extends Window> W openAsWindow(Window parent, Function<Controller<V,M>,W> windowFactory, boolean closeOld){
        W createWindow = windowFactory.apply(this);
        createWindow.getController().initView();
        createWindow.setContent(this);
        createWindow.setSize(getView().getSize());
        parent.openWindow(createWindow,closeOld);
        return createWindow;
    }


    default <W extends Window> W openAsWindow(Window parent, BiFunction<Controller<V,M>,Window,W> windowFactory){
        return openAsWindow(parent,(controller)-> windowFactory.apply(this, parent),false);
    }


    default Tab asTab(String title){
        initView();
        return new Tab() {
            @Override
            public IconCode getIcon() {
                return getController().getView().getTabIcon();
            }

            @Override
            public Controller<?,?> getController() {
                return Controller.this;
            }

            @Override
            public String getTitle() {
                return title;
            }

            @Override
            public boolean commitClose() {
                return Controller.this.commitClose();
            }
        };
    }

    default Window openTab(String title,TabbedPaneController tabbedPaneController){
        tabbedPaneController.addTab(asTab(title));
        return tabbedPaneController.getView().getWindow();
    }

    default void removeSelf(TabbedPaneController tabbedPaneController){
        tabbedPaneController.closeTab(this.asTab(""));
    }

    default void removeSelf(){
        removeSelf(TabbedPaneModel.DEFAULT_TABBED_PANE);
    }

    default Window openTab(String title){
        return openTab(title, TabbedPaneModel.DEFAULT_TABBED_PANE);
    }
}
