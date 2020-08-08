package kernbeisser.CustomComponents.DatePicker;

import java.time.LocalDate;
import kernbeisser.Windows.MVC.Model;

public class DatePickerModel implements Model<DatePickerController> {
  private LocalDate selectedDate;

  public LocalDate getSelectedDate() {
    return selectedDate;
  }

  public void setSelectedDate(LocalDate selectedDate) {
    this.selectedDate = selectedDate;
  }
}
