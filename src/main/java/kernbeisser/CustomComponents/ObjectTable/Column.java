package kernbeisser.CustomComponents.ObjectTable;

import java.awt.*;
import java.util.function.Consumer;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import kernbeisser.CustomComponents.AccessChecking.Getter;
import kernbeisser.Exeptions.AccessDeniedException;
import org.intellij.lang.annotations.MagicConstant;

public interface Column<T> {
  Color STRIPED_BACKGROUND_COLOR = new Color(240, 240, 240);
  TableCellRenderer DEFAULT_RENDERER = new DefaultTableCellRenderer();
  TableCellRenderer DEFAULT_STRIPED_RENDERER =
      new StripedRenderer(
          DEFAULT_RENDERER,
          new DefaultTableCellRenderer() {
            {
              setBackground(STRIPED_BACKGROUND_COLOR);
            }
          });
  int DEFAULT_ALIGNMENT = SwingConstants.CENTER;

  static <T> Column<T> create(String s, Getter<T, Object> v) {
    return create(s, v, DEFAULT_ALIGNMENT);
  }

  static <T> Column<T> create(String s, Getter<T, Object> v, @MagicConstant int alignment) {
    return create(s, v, alignment, true);
  }

  static <T> Column<T> create(
      String s, Getter<T, Object> v, @MagicConstant int alignment, boolean striped) {
    return create(s, v, alignment, striped, e -> {});
  }

  static <T> Column<T> create(String s, Getter<T, Object> v, Consumer<T> onAction) {
    return create(s, v, DEFAULT_ALIGNMENT, true, onAction);
  }

  static <T> Column<T> create(
      String s, Getter<T, Object> v, int alignment, boolean striped, Consumer<T> onAction) {
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
      public TableCellRenderer getRenderer() {
        return striped
            ? new StripedRenderer(
                new DefaultTableCellRenderer() {
                  {
                    setHorizontalAlignment(alignment);
                  }
                },
                new DefaultTableCellRenderer() {
                  {
                    setBackground(STRIPED_BACKGROUND_COLOR);
                    setHorizontalAlignment(alignment);
                  }
                })
            : new DefaultTableCellRenderer() {
              {
                setHorizontalAlignment(alignment);
              }
            };
      }

      @Override
      public void onAction(T t) {
        onAction.accept(t);
      }
    };
  }

  static <T> Column<T> createIcon(Icon icon, Consumer<T> onAction) {
    return new Column<T>() {
      final DefaultTableCellRenderer normal = new DefaultTableCellRenderer();
      final DefaultTableCellRenderer dark = new DefaultTableCellRenderer();

      {
        normal.setIcon(icon);
        dark.setIcon(icon);
        normal.setHorizontalAlignment(DEFAULT_ALIGNMENT);
        dark.setHorizontalAlignment(DEFAULT_ALIGNMENT);
        dark.setBackground(STRIPED_BACKGROUND_COLOR);
      }

      @Override
      public String getName() {
        return "";
      }

      @Override
      public Object getValue(T t) throws AccessDeniedException {
        return "";
      }

      @Override
      public TableCellRenderer getRenderer() {
        return new StripedRenderer(normal, dark);
      }

      @Override
      public void adjust(TableColumn column) {
        column.setMaxWidth(20);
      }

      @Override
      public void onAction(T t) {
        onAction.accept(t);
      }
    };
  }

  String getName();

  Object getValue(T t) throws AccessDeniedException;

  default void onAction(T t) {}

  default TableCellRenderer getRenderer() {
    return DEFAULT_RENDERER;
  }

  default void adjust(TableColumn column) {}
}
