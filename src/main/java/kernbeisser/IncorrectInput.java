package kernbeisser;

import javax.swing.*;

public class IncorrectInput extends Exception {
    private JComponent component;

    public IncorrectInput(JComponent component, Class<?> type) {
        super("the Text from the component " + component.getClass().getName() + " is not able to parse to " + type);
        this.component = component;
    }

    public JComponent getComponent() {
        return component;
    }
}
