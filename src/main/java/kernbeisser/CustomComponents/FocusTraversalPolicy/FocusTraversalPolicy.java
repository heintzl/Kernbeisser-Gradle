package kernbeisser.CustomComponents.FocusTraversalPolicy;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class FocusTraversalPolicy extends java.awt.FocusTraversalPolicy {

    private final List<Component> components;

    public FocusTraversalPolicy(List<Component> components){
        this.components = components;
    }

    @Override
    public Component getComponentAfter(Container aContainer, Component aComponent) {
        return components.get(components.indexOf(aComponent)+1);
    }

    @Override
    public Component getComponentBefore(Container aContainer, Component aComponent) {
        return components.get(components.indexOf(aComponent)-1);
    }

    @Override
    public Component getFirstComponent(Container aContainer) {
        return components.get(0);
    }

    @Override
    public Component getLastComponent(Container aContainer) {
        return components.get(components.size()-1);
    }

    @Override
    public Component getDefaultComponent(Container aContainer) {
        return components.get(0);
    }
}
