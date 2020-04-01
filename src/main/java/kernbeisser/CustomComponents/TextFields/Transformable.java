package kernbeisser.CustomComponents.TextFields;

import kernbeisser.Exeptions.IncorrectInput;

public interface Transformable <T> {
    T toObject(String s) throws IncorrectInput;
    default String toString(T t){
        return t.toString();
    }
}
