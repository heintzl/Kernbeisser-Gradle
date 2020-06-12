package kernbeisser.Windows.Menu;

import kernbeisser.Windows.View;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class MenuView implements View<MenuController> {
    private JPanel main;
    private JButton button1;

    @Override
    public void initialize(MenuController controller) {

    }

    @Override
    public @NotNull JComponent getContent() {
        return main;
    }
}
