package kernbeisser.CustomComponents.DatePicker;

import kernbeisser.Windows.View;
import kernbeisser.Windows.Window;

import javax.swing.*;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.Locale;

public class DatePickerView extends Window implements View {
    private JButton chooseButton;
    private JPanel main;
    private JComboBox<String> monthSelection;
    private JTable monthDays;

    public DatePickerView(Window current, DatePickerController datePickerController) {
        super(current);
        add(main);
        setSize(200,226);
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
        windowInitialized();
    }

    void setMoths(Month[] months){
        monthSelection.removeAllItems();
        for (Month month : months) {
            this.monthSelection.addItem(month.getDisplayName(TextStyle.FULL, Locale.GERMANY));
        }
        monthSelection.setSelectedIndex(0);
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
