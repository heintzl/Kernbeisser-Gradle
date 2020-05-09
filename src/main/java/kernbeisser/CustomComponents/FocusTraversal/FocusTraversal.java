package kernbeisser.CustomComponents.FocusTraversal;

import java.awt.*;
import java.util.Vector;

public class FocusTraversal
        extends FocusTraversalPolicy
{
    Vector<Component> order;

    public FocusTraversal(Vector<Component> order) {
        this.order = new Vector<Component>(order.size());
        this.order.addAll(order);
    }

    public Component getComponentAfter(Container focusCycleRoot,
                                       Component component)
    {
        int idx;
        do {
            idx = (order.indexOf(component) + 1) % order.size();
            component = order.get(idx);
        } while(! (component.isEnabled() && component.isVisible()) && idx <= order.size());
        return component;
    }

    public Component getComponentBefore(Container focusCycleRoot,
                                        Component component)
    {
        int idx;
        do {
            idx = order.indexOf(component) - 1;
            if (idx < 0) {
                idx = order.size() - 1;
            }
            component = order.get(idx);
        } while(! (component.isEnabled() && component.isVisible()) && idx >= 0);
        return component;
    }

    public Component getDefaultComponent(Container focusCycleRoot) {
        return order.get(0);
    }

    public Component getLastComponent(Container focusCycleRoot) {
        return order.lastElement();
    }

    public Component getFirstComponent(Container focusCycleRoot) {
        return order.get(0);
    }
}