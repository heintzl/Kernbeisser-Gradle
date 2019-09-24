package kernbeisser.Windows.Nodes;

import java.util.ArrayList;
import java.util.Collection;

public interface NodeContainer <T> {
    Collection<Node<T>> getNodes();
    default void addNode(Node<T> node){
        getNodes().add(node);
        refresh();
    }
    default void addAllNodes(Collection<Node<T>> nodes){
        getNodes().addAll(nodes);
        refresh();
    }
    default void removeNode(Node<T> node){
        System.out.println(getNodes().remove(node));
        refresh();
    }
    void refresh();
    Node<T> getSelectedNode();
    ArrayList<NodeSelectionListener<T>> getNodeSelectionListener();
    default void addNodeSelectionListener(NodeSelectionListener<T> lister){ getNodeSelectionListener().add(lister);
    }
}
