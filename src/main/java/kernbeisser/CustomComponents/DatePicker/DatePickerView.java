package kernbeisser.CustomComponents.DatePicker;

import kernbeisser.Windows.Controller;
import kernbeisser.Windows.JFrameWindow;
import kernbeisser.Windows.Window;
import kernbeisser.Windows.View;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.Locale;

public class DatePickerView implements View<DatePickerController> {
    private JButton chooseButton;
    private JPanel main;
    private JComboBox<String> monthSelection;
    private JTable monthDays;

    private final DatePickerController controller;

    public DatePickerView(DatePickerController datePickerController) {
        this.controller = datePickerController;
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
    }

    void setSelectionButtonText(String s){
        chooseButton.setEnabled(true);
        chooseButton.setText(s + " Auswählen");
    }

    int getSelectedMonth(){
        return monthSelection.getSelectedIndex();
    }


    @Override
    public void initialize(DatePickerController controller) {
        monthSelection.addActionListener(e -> controller.loadMonth());
        chooseButton.addActionListener(e -> controller.commit());
        monthDays.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                controller.select();
            }
        });
        monthDays.setSelectionBackground(new Color(0x949595));
        monthDays.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        monthDays.setColumnSelectionAllowed(true);
        monthDays.setRowSelectionAllowed(true);
    }

    @Override
    public @NotNull JComponent getContent() {
        return main;
    }



}
