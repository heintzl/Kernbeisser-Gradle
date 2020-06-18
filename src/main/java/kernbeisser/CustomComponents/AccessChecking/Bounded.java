package kernbeisser.CustomComponents.AccessChecking;

import kernbeisser.Exeptions.AccessDeniedException;
import kernbeisser.Exeptions.CannotParseException;
import kernbeisser.Useful.Tools;

import java.lang.invoke.LambdaMetafactory;

public interface Bounded <T,V>{

    void setObjectData(T data);

    void writeInto(T p) throws CannotParseException;

    void markWrongInput();

    Getter<T,V> getGetter();

    Setter<T,V> getSetter();

    void setReadable(boolean b);

    void setWriteable(boolean b);

    boolean validInput();

    default boolean canWrite(T v){
        Setter<T,V> setter = getSetter();
        assert setter != null;
        try {
            //throws
            try {
                setter.set(v,null);
                return true;
            }catch (NullPointerException ignored){
                Tools.invokeWithDefault(e -> { setter.set(v, (V) e); });
                return true;
            }
        } catch (AccessDeniedException e) {
            return false;
        }catch (NullPointerException e){
            System.out.println(Tools.error);
            e.printStackTrace();
            return true;
        }
    }



    default boolean canRead(T v){
        try {
            getGetter().get(v);
            return true;
        } catch (AccessDeniedException e) {
            return false;
        }
    }
}
