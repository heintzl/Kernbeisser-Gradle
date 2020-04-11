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
        Window out = windowFactory.apply(this);
        out.setContent(getView().getContent());
        out.setSize(getView().getSize());
        parent.openWindow(out,closeOld);
        return out;
    }


    default Window openAsWindow(Window parent, BiFunction<Controller<V,M>,Window,Window> windowFactory){
        return openAsWindow(parent,(controller)-> windowFactory.apply(this, parent),false);
    }

    default void initView(){
        for (Method declaredMethod : getView().getClass().getDeclaredMethods()) {
            if(declaredMethod.getName().equals("initialize")){
                try {
                    declaredMethod.setAccessible(true);
                    declaredMethod.invoke(getView(),this);
                    break;
                } catch (IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }
        fillUI();
        open();
    }
}
