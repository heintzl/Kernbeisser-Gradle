package kernbeisser.Windows;

import kernbeisser.CustomComponents.DatePicker.DatePickerView;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public interface Controller extends ActionListener {
    @Override
    default void actionPerformed(ActionEvent e) {
        refresh();
    }

    default void refresh() {
    }

    View getView();

    Model getModel();
}
