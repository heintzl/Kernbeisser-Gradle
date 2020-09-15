package kernbeisser.CustomComponents.ObjectTable;

import java.util.function.Consumer;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import kernbeisser.CustomComponents.AccessChecking.Getter;
import kernbeisser.Exeptions.AccessDeniedException;
import org.jetbrains.annotations.NotNull;

public interface Column<T> {
  public static final TableCellRenderer DEFAULT_RENDERER = new DefaultTableCellRenderer();

  static <T> Column<T> create(String s, Getter<T, Object> v) {
    return new Column<T>() {
      private boolean read = true;

      @Override
      public String getName() {
        return s;
      }

      @Override
      public Object getValue(T t) {
        if (!read) {
          return "***********";
        }
        try {
          return v.get(t);
        } catch (AccessDeniedException e) {
          read = false;
          return getValue(t);
        }
      }
    };
  }

  @NotNull
  static <T> Column<T> create(String s, Getter<T, Object> v, Consumer<T> action) {
    return new Column<T>() {
      private boolean read = true;

      @Override
      public String getName() {
        return s;
      }

      @Override
      public Object getValue(T t) {
        if (!read) {
          return "***********";
        }
        try {
          return v.get(t);
        } catch (AccessDeniedException e) {
          read = false;
          return getValue(t);
        }
      }

      @Override
      public void onAction(T t) {
        action.accept(t);
      }
    };
  }

  String getName();

  Object getValue(T t) throws AccessDeniedException;

  default void onAction(T t) {}

  default TableCellRenderer getRenderer() {
    return DEFAULT_RENDERER;
  }
}
