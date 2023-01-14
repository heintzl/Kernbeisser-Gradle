package kernbeisser.CustomComponents.ObjectTable;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Comparator;
import java.util.Objects;
import java.util.regex.Pattern;
import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import kernbeisser.Exeptions.PermissionKeyRequiredException;

public interface Column<T> {
  int DEFAULT_ALIGNMENT = SwingConstants.LEFT;
  int DEFAULT_ICON_WIDTH = 25;
  Comparator<Object> DEFAULT_SORTER = Comparator.comparing(Objects::toString);
  Comparator<Object> NUMBER_SORTER =
      new Comparator<Object>() {
        private final Pattern numberFilter = Pattern.compile("[^\\d,.-]");

        @Override
        public int compare(Object o1, Object o2) {
          double a;
          double b;
          try {
            a =
                Double.parseDouble(
                    numberFilter.matcher(o1.toString()).replaceAll("").replace(",", "."));
          } catch (NumberFormatException e) {
            return -1;
          }
          try {
            b =
                Double.parseDouble(
                    numberFilter.matcher(o2.toString()).replaceAll("").replace(",", "."));
          } catch (NumberFormatException e) {
            return 1;
          }
          return Double.compare(a, b);
        }
      };

  static Comparator<Object> DATE_SORTER(DateTimeFormatter formatter) {
    return new Comparator<Object>() {

      @Override
      public int compare(Object o1, Object o2) {
        LocalDate a;
        LocalDate b;

        try {
          a = LocalDate.parse((CharSequence) o1, formatter);
        } catch (DateTimeParseException e) {
          return -1;
        }
        try {
          b = LocalDate.parse((CharSequence) o2, formatter);
        } catch (DateTimeParseException e) {
          return 1;
        }
        return a.compareTo(b);
      }
    };
  }

  String getName();

  Object getValue(T t) throws PermissionKeyRequiredException;

  default void onAction(MouseEvent e, T t) {}

  default boolean usesStandardFilter() {
    return false;
  }

  TableCellRenderer getRenderer();

  default void adjust(TableColumn column) {}

  default Comparator<Object> sorter() {
    return DEFAULT_SORTER;
  }
}
