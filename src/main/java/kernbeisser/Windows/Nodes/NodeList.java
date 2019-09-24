package kernbeisser.Windows.Nodes;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

public class NodeList <T extends Nodeable> extends JScrollPane implements NodeContainer<T>{
    Collection<Node<T>> nodes;
    private int columns = 1;
    ArrayList<NodeSelectionListener<T>> nodeSelectionListeners = new ArrayList<>();
    Node<T> selectedNode;
    public NodeList(Collection<Node<T>> nodes,int columns){
        this.columns=columns;
        this.nodes=nodes;
        refresh();
    }
    public NodeList(Collection<Node<T>> nodes){
        this(nodes,1);
    }
    public NodeList(){
        this(new HashSet<Node<T>>());
    }

    @Override
    public void refresh(){
        JPanel nodeGraphics = new JPanel();
        int value = getVerticalScrollBar().getValue();
        nodeGraphics.setLayout(new GridLayout(0,columns));
        for (Node<T> node : nodes) {
            JPanel g = node.getGraphic();
            g.setFocusable(true);
            if(g.getMouseListeners().length<1)
            g.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseReleased(MouseEvent e) {
                    for (NodeSelectionListener<T> listener : nodeSelectionListeners) {
                        listener.nodeSelected(node.get().toNode());
                    }
                }
            });
            nodeGraphics.add(node.getGraphic());
        }
        setViewportView(nodeGraphics);
        getVerticalScrollBar().setValue(Math.min(getVerticalScrollBar().getMaximum(),value));
    }
    @Override
    public Collection<Node<T>> getNodes() {
        return nodes;
    }

    @Override
    public Node<T> getSelectedNode() {
        return selectedNode;
    }

    @Override
    public ArrayList<NodeSelectionListener<T>> getNodeSelectionListener() {
        return nodeSelectionListeners;
    }
}
