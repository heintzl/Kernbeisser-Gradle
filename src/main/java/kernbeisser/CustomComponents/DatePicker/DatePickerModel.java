package kernbeisser.CustomComponents.DatePicker;

import java.time.Instant;
import kernbeisser.Windows.MVC.IModel;
import lombok.Data;

@Data
public class DatePickerModel implements IModel<DatePickerController> {
  private Instant selectedDate;
}
