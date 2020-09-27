package kernbeisser.CustomComponents.FocusTraversal;

import java.awt.*;
import java.util.Vector;

public class FocusTraversal extends FocusTraversalPolicy {
  Vector<Component> order;

  private boolean getTabability(Component component) {
    return (component.isEnabled() && component.isVisible());
  }

  public FocusTraversal(Vector<Component> order) {
    this.order = new Vector<Component>(order.size());
    this.order.addAll(order);
  }

  public Component getComponentAfter(Container focusCycleRoot, Component component) {
    int origin = order.indexOf(component);
    for (int idx = 1; idx < order.size(); idx++) {
      Component target = order.get((origin + idx) % order.size());
      if (getTabability(target)) {
        return target;
      }
    }
    return component;
  }

  public Component getComponentBefore(Container focusCycleRoot, Component component) {
    int idx;
    do {
      idx = order.indexOf(component) - 1;
      if (idx < 0) {
        idx = order.size() - 1;
      }
      component = order.get(idx);
    } while (!getTabability(component) && idx >= 0);
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
