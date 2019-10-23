package kernbeisser.CustomComponents.Node;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Collection;

public interface Node<T>{
    T get();
    JPanel getGraphic();
    void set(T t);
    static <T extends Nodeable> Collection<Node<T>> toNodes(Iterable<T> n){
        ArrayList<Node<T>> out = new ArrayList<>();
        for (T nodeable : n) {
            out.add(nodeable.toNode());
        }
        return out;
    }
}
