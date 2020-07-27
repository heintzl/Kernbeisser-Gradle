package kernbeisser.CustomComponents.TextFields;

import java.time.DateTimeException;
import java.time.LocalDate;
import kernbeisser.Exeptions.IncorrectInput;

public class DateParseField extends FilterField<LocalDate> {
  public DateParseField() {
    super(DateParseField::fromString, true);
  }

  public void setValue(LocalDate date) {
    setText(date.getDayOfMonth() + "." + date.getMonth().getValue() + "." + date.getYear());
  }

  private static LocalDate fromString(String s) throws IncorrectInput {
    String[] parts = s.split("[/_,*+|\\- .]");
    if (parts.length != 3) {
      throw new IncorrectInput("Date has more than three parts:" + s);
    }
    try {
      return LocalDate.of(
          Integer.parseInt(parts[2]), Integer.parseInt(parts[1]), Integer.parseInt(parts[0]));
    } catch (NumberFormatException | DateTimeException e) {
      throw new IncorrectInput("Date is not in following format: [dd.mm.yyyy]");
    }
  }

  @Override
  @Deprecated
  // Can return null because allowWrongInput is on please use getUncheckedValue
  public LocalDate getSafeValue() {
    return super.getSafeValue();
  }
}
