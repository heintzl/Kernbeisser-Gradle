package kernbeisser.CustomComponents;

import kernbeisser.Windows.View;

import javax.swing.*;

public class ViewMainPanel extends JPanel {

    private final View view;

    public ViewMainPanel(JComponent mainPanel, View view) {
        super.setLayout(new BoxLayout(this,BoxLayout.PAGE_AXIS));
        super.add(mainPanel);
        this.view = view;
    }

    public View getView() { return view; }

}
