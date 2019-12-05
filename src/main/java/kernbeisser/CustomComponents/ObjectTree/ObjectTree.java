package kernbeisser.CustomComponents.ObjectTree;

import kernbeisser.DBEntitys.PriceList;
import kernbeisser.DBEntitys.User;

import javax.swing.*;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultTreeModel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class ObjectTree <T> extends JTree {
    private Collection<T> startValues;
    private ChildFactory<T> childFactory;
    private String name;
    private ArrayList<NodeSelectionListener<T>> selectionListeners = new ArrayList<>();

    public ObjectTree(ChildFactory<T> factory, String name, Collection<T> startValues){
        this.startValues=startValues;
        this.childFactory=factory;
        this.name=name;
        refresh();
        addTreeSelectionListener(e -> {
            ObjectNode<T> node = ((ObjectNode<T>)getLastSelectedPathComponent());
            if(node.getValue()==null)return;
            for (NodeSelectionListener<T> listener : selectionListeners) {
                listener.select(node.getValue());
            }
        });
    }

    public void addSelectionListener(NodeSelectionListener<T> listener){
        selectionListeners.add(listener);
    }

    public void refresh(){
        setModel(create(startValues));
    }

    private DefaultTreeModel create(Collection<T> x){
        if(x.size() == 0)return null;
        ObjectNode<Void> main = new ObjectNode<>(null,name);
        for (T t : x) {
            main.add(extract(t));
        }
        return new DefaultTreeModel(main);
    }

    private ObjectNode<T> extract(T x){
        ObjectNode<T> out = new ObjectNode<>(x,childFactory.getName(x));
        for (T t : childFactory.produce(x)) {
            out.add(extract(t));
        }
        return out;
    }
}
