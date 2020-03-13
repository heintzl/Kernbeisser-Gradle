package kernbeisser.CustomComponents.DatePicker;

import kernbeisser.Windows.View;
import kernbeisser.Windows.Window;

import javax.swing.*;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class DatePickerView extends Window implements View {
    private JButton chooseButton;
    private JPanel main;
    private JComboBox monthSelection;
    private JTable monthDays;

    public DatePickerView(Window current, DatePickerController datePickerController) {
        super(current);
        add(main);
        setSize(200,226);
        setLocationRelativeTo(null);
        monthSelection.addActionListener(e -> datePickerController.loadMonth());
        chooseButton.addActionListener(e -> datePickerController.commit());
        monthDays.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                datePickerController.select();
            }
        });
        monthDays.setSelectionBackground(new Color(0x949595));
        monthDays.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        monthDays.setColumnSelectionAllowed(true);
        monthDays.setRowSelectionAllowed(true);
    }

    int getSelectedDay(){
        Object o = monthDays.getValueAt(monthDays.getSelectedRow(),monthDays.getSelectedColumn());
        if(o!=null){
            return (int) o;
        }else return -1;
    }

    void setMonths(TableModel model){
        monthDays.setModel(model);
        repaint();
    }

    void setSelectionButtonText(String s){
        chooseButton.setEnabled(true);
        chooseButton.setText(s + " Auswählen");
    }

    int getSelectedMonth(){
        return monthSelection.getSelectedIndex();
    }


}
