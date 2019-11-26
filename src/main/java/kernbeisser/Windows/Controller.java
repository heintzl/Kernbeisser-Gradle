package kernbeisser.Windows;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public interface Controller extends ActionListener {
    @Override
    default void actionPerformed(ActionEvent e) {
        refresh();
    }
    void refresh();
    View  getView();
    Model getModel();
}
