package kernbeisser.CustomComponents;

import kernbeisser.Windows.View;

import javax.swing.*;
import java.awt.*;

public class ViewMainPanel extends JPanel {

    private final View<?> view;

    public ViewMainPanel(JComponent mainPanel, View<?> view) {
        super(new GridLayout(1, 1));
        super.add(mainPanel);
        this.view = view;
    }

    public View<?> getView() {
        return view;
    }

}
