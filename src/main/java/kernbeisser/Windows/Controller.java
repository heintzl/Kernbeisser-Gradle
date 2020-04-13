package kernbeisser.Windows;

import kernbeisser.Enums.Key;
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

    default void open(){}

    default Window openAsWindow(Window parent, Function<Controller<V,M>,Window> windowFactory) {
        return openAsWindow(parent, windowFactory, true);
    }

    default Window openAsWindow(Window parent, Function<Controller<V,M>,Window> windowFactory, boolean closeOld){
        Window createWindow = windowFactory.apply(this);
        createWindow.getController().initView();
        createWindow.setContent(this);
        createWindow.setSize(getView().getSize());
        parent.openWindow(createWindow,closeOld);
        return createWindow;
    }


    default Window openAsWindow(Window parent, BiFunction<Controller<V,M>,Window,Window> windowFactory){
        return openAsWindow(parent,(controller)-> windowFactory.apply(this, parent),false);
    }

    default void initView(){
        try {
            Method method = getView().getClass().getDeclaredMethod("initialize", Controller.class);
            method.setAccessible(true);
            method.invoke(getView(),this);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }
        fillUI();
    }
}
