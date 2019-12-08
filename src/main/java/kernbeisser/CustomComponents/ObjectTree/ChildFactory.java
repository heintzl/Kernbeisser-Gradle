package kernbeisser.CustomComponents.ObjectTree;

import java.util.Collection;

public interface ChildFactory <T>{
    Collection<T> produce(T t);
    String getName(T t);
}
