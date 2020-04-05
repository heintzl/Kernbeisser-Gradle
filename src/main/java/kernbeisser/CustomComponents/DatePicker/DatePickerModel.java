package kernbeisser.CustomComponents.DatePicker;

import kernbeisser.Windows.Model;

import java.time.LocalDate;

public class DatePickerModel implements Model {
    private LocalDate selectedDate;

    public LocalDate getSelectedDate() {
        return selectedDate;
    }

    public void setSelectedDate(LocalDate selectedDate) {
        this.selectedDate = selectedDate;
    }
}
