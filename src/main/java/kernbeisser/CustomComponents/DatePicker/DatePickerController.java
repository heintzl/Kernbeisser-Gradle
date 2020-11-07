package kernbeisser.CustomComponents.DatePicker;

import java.time.Instant;
import java.time.LocalDate;
import java.time.Month;
import java.time.Year;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.format.TextStyle;
import java.util.Locale;
import java.util.function.Consumer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import kernbeisser.Enums.PermissionKey;
import kernbeisser.Windows.MVC.Controller;
import kernbeisser.Windows.ViewContainer;
import kernbeisser.Windows.ViewContainers.SubWindow;
import org.jetbrains.annotations.NotNull;

public class DatePickerController extends Controller<DatePickerView, DatePickerModel> {

  public DatePickerController() {
    super(new DatePickerModel());
  }

  void loadMonth() {
    getView().setMonths(createMonth(getView().getSelectedMonth()));
  }

  private TableModel createMonth(int monthIndex) {
    Month m = Month.of(monthIndex + 1);
    YearMonth month = Year.now().atMonth(m);
    int firstDayOfWeek = month.atDay(1).getDayOfWeek().getValue();
    Object[][] days = new Object[((m.maxLength() - 3 + firstDayOfWeek) / 7) + 1][7];
    for (int i = 1; i < m.maxLength(); i++) {
      days[(firstDayOfWeek + i - 2) / 7][month.atDay(i).getDayOfWeek().getValue() - 1] = i;
    }

    return new DefaultTableModel(days, new Object[] {"Mo", "Di", "Mi", "Do", "Fr", "Sa", "So"}) {
      @Override
      public boolean isCellEditable(int row, int column) {
        return false;
      }
    };
  }

  void select() {
    if (getView().getSelectedDay() == -1) {
      return;
    }
    LocalDate date =
        Year.now().atMonth(getView().getSelectedMonth() + 1).atDay(getView().getSelectedDay());
    if (date.equals(model.getSelectedDate())) {
      commit();
    } else {
      model.setSelectedDate(Instant.from(date.atStartOfDay(ZoneId.systemDefault())));
    }
    getView()
        .setSelectionButtonText(
            date.getDayOfWeek().getDisplayName(TextStyle.FULL_STANDALONE, Locale.GERMANY)
                + " "
                + date.getDayOfMonth()
                + " "
                + date.getMonth().getDisplayName(TextStyle.FULL, Locale.GERMANY));
  }

  public Instant getSelectedValue() {
    return model.getSelectedDate();
  }

  void commit() {
    getView().back();
    finish();
  }

  public void finish() {}

  @Override
  public @NotNull DatePickerModel getModel() {
    return model;
  }

  @Override
  public void fillView(DatePickerView datePickerView) {
    datePickerView.setMoths(Month.values());
    loadMonth();
  }

  @Override
  public PermissionKey[] getRequiredKeys() {
    return new PermissionKey[0];
  }

  public static void requestDate(ViewContainer current, Consumer<Instant> select) {
    new DatePickerController() {
      @Override
      public void finish() {
        select.accept(getModel().getSelectedDate());
      }
    }.openIn(new SubWindow(current));
  }
}
