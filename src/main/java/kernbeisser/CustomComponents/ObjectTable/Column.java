package kernbeisser.CustomComponents.ObjectTable;

import java.awt.event.ActionEvent;
import java.util.function.Consumer;
import java.util.function.Function;
import javax.swing.*;
import kernbeisser.CustomComponents.AccessChecking.Getter;
import kernbeisser.Enums.PermissionKey;
import kernbeisser.Exeptions.AccessDeniedException;
import kernbeisser.Security.MasterPermissionSet;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public interface Column<T> {
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
  @Contract(value = "_, _, _ -> new", pure = true)
  static <T> Column<T> createButton(
      String name, @NotNull Function<T, String> value, Consumer<T> action) {
    return new Column<T>() {
      @Override
      public String getName() {
        return name;
      }

      @Override
      public Object getValue(T t) {
        JButton button =
            new JButton(
                new AbstractAction() {
                  @Override
                  public void actionPerformed(ActionEvent e) {
                    action.accept(t);
                  }
                });
        button.setText(value.apply(t));
        return button;
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

  @NotNull
  @Contract(value = "_, _, _ -> new", pure = true)
  static <T> Column<T> create(String s, Function<T, Object> v, PermissionKey... keys) {
    return new Column<T>() {
      final boolean read = MasterPermissionSet.hasPermissions(keys);

      @Override
      public String getName() {
        return s;
      }

      @Override
      public Object getValue(T t) {
        return read ? v.apply(t) : "***********";
      }
    };
  }

  String getName();

  Object getValue(T t) throws AccessDeniedException;

  default void onAction(T t) {}

  default boolean isEditable(T t) {
    return false;
  }
}
