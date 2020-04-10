package kernbeisser.Windows;

import kernbeisser.Enums.Key;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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

    default Window openAsWindow(Window parent, Function<Controller<V,M>,Window> windowFactory){
        Window out = windowFactory.apply(this);
        out.setContent(getView().getContent());
        out.setSize(getView().getSize());
        parent.openWindow(out);
        return out;
    }

    default void initView(){
        for (Method declaredMethod : getView().getClass().getDeclaredMethods()) {
            if(declaredMethod.getName().equals("initialize")){
                try {
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
