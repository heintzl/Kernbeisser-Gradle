package kernbeisser.Windows;

import java.util.Collection;

public interface Searchable <T>{
    Collection<T> search(String s,int max);
}
