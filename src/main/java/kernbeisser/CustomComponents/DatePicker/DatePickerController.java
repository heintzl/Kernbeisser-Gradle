package kernbeisser.CustomComponents.DatePicker;

import kernbeisser.Enums.PermissionKey;
import kernbeisser.Windows.Controller;
import kernbeisser.Windows.WindowImpl.JFrameWindow;
import kernbeisser.Windows.WindowImpl.SubWindow;
import org.jetbrains.annotations.NotNull;

import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.time.LocalDate;
import java.time.Month;
import java.time.Year;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.Locale;
import java.util.function.Consumer;

public class DatePickerController implements Controller<DatePickerView,DatePickerModel> {
    private final DatePickerView view;
    private final DatePickerModel model;

    public DatePickerController(){
        view = new DatePickerView(this);
        model = new DatePickerModel();
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
        LocalDate date = Year.now().atMonth(view.getSelectedMonth()+1).atDay(view.getSelectedDay());
        if(date.equals(model.getSelectedDate()))commit();
        else model.setSelectedDate(date);
        view.setSelectionButtonText(date.getDayOfWeek().getDisplayName(TextStyle.FULL_STANDALONE, Locale.GERMANY)+" "+date.getDayOfMonth()+" "+date.getMonth().getDisplayName(TextStyle.FULL, Locale.GERMANY));
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
    public @NotNull DatePickerView getView() {
        return view;
    }

    @Override
    public @NotNull DatePickerModel getModel() {
        return model;
    }

    @Override
    public void fillUI() {
        view.setMoths(Month.values());
        loadMonth();
    }

    @Override
    public PermissionKey[] getRequiredKeys() {
        return new PermissionKey[0];
    }

    public static void requestDate(JFrameWindow current, Consumer<LocalDate> select){
            new DatePickerController() {
                @Override
                public void finish() {
                    select.accept(getModel().getSelectedDate());
                }
            }.openAsWindow(current, SubWindow::new);
    }
}
