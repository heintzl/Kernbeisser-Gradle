package kernbeisser.CustomComponents;

import kernbeisser.Windows.View;
import lombok.Getter;

import javax.swing.*;

public class ViewMainPanel extends JPanel {
    @Getter
    private final View view;

    public ViewMainPanel(JComponent mainPanel) {
        super.add(mainPanel);
 //       this.view = view;
    }
}
