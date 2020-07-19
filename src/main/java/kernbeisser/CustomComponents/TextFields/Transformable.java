package kernbeisser.CustomComponents.TextFields;

import kernbeisser.Exeptions.IncorrectInput;
import org.jetbrains.annotations.NotNull;

public interface Transformable<T> {
    T toObject(String s) throws IncorrectInput;

    default String toString(@NotNull T t) {
        return t.toString();
    }
}
