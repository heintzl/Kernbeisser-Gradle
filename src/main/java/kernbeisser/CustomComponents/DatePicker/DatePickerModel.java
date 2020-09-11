package kernbeisser.CustomComponents.DatePicker;

import java.time.LocalDate;
import kernbeisser.Windows.MVC.IModel;

public class DatePickerModel implements IModel<DatePickerController> {
  private LocalDate selectedDate;

  public LocalDate getSelectedDate() {
    return selectedDate;
  }

  public void setSelectedDate(LocalDate selectedDate) {
    this.selectedDate = selectedDate;
  }
}
