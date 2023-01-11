package kernbeisser.CustomComponents.Dialogs;

import com.github.lgooddatepicker.components.DatePicker;
import java.awt.*;
import java.time.LocalDate;
import javax.swing.*;

public class DateSelectorDialog {

  public static LocalDate getDate(
      Component component, String title, String caption, LocalDate defaultDate) {
    JPanel datePickerPanel = new JPanel();
    datePickerPanel.setLayout(new BoxLayout(datePickerPanel, BoxLayout.Y_AXIS));
    JLabel infoText = new JLabel(caption);
    infoText.setAlignmentX(JLabel.LEFT_ALIGNMENT);
    DatePicker datePicker = new DatePicker();
    datePicker.setAlignmentX(JLabel.LEFT_ALIGNMENT);
    datePicker.setDate(defaultDate);
    datePickerPanel.add(infoText);
    datePickerPanel.add(datePicker);

    if (JOptionPane.showConfirmDialog(
            component, datePickerPanel, title, JOptionPane.OK_CANCEL_OPTION)
        == JOptionPane.CANCEL_OPTION) {
      return null;
    }
    return datePicker.getDate();
  }
}
