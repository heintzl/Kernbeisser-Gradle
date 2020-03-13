package kernbeisser.CustomComponents.DatePicker;

import kernbeisser.Main;
import kernbeisser.Windows.Controller;
import kernbeisser.Windows.Model;
import kernbeisser.Windows.Window;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.time.LocalDate;
import java.time.Month;
import java.time.Year;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.Locale;

public class DatePickerController implements Controller {
    private DatePickerView view;
    private DatePickerModel model;

    public static void main(String[] args)
            throws ClassNotFoundException, UnsupportedLookAndFeelException, InstantiationException,
                   IllegalAccessException {
        Main.buildEnvironment();
        new DatePickerController(null);
    }

    DatePickerController(Window current){
        view = new DatePickerView(current,this);
        model = new DatePickerModel();
        loadMonth();
        view.repaint();
    }

    void loadMonth(){
        view.setMonths(createMonth(view.getSelectedMonth()));
    }

    private TableModel createMonth(int monthIndex){
        Month m = Month.of(monthIndex+1);
        YearMonth month = Year.now().atMonth(m);
        int firstDayOfWeek = month.atDay(1).getDayOfWeek().getValue();
        Object[][] days = new Object[((m.maxLength()-3+firstDayOfWeek) / 7)+1][7];
        for (int i = 1; i < m.maxLength(); i++) {
            days[(firstDayOfWeek+i-2)/7][month.atDay(i).getDayOfWeek().getValue()-1] = i;
        }

        return new DefaultTableModel(days, new Object[]{"Mo", "Di", "Mi", "Do", "Fr", "Sa", "So"}){
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }

    void select() {
        if(view.getSelectedDay()==-1)return;
        model.setSelectedDate(Year.now().atMonth(view.getSelectedMonth()+1).atDay(view.getSelectedDay()));
        view.setSelectionButtonText(model.getSelectedDate().getDayOfWeek().getDisplayName(TextStyle.FULL_STANDALONE,
                                                                                          Locale.GERMANY)+" "+model.getSelectedDate().getDayOfMonth()+" "+model.getSelectedDate().getMonth().getDisplayName(TextStyle.FULL, Locale.GERMANY));
    }

    public LocalDate getSelectedValue(){
        return model.getSelectedDate();
    }

    void commit(){
        view.back();
        finish();
    }

    public void finish(){}

    @Override
    public DatePickerView getView() {
        return view;
    }

    @Override
    public Model getModel() {
        return model;
    }

    public static LocalDate requestDate(Window current){
        current.setEnabled(false);
        DatePickerController controller = new DatePickerController(current);
        Object lock = new Object();
        return controller.getSelectedValue();
    }
}
