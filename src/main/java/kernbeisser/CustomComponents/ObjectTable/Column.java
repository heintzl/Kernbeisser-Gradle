package kernbeisser.CustomComponents.ObjectTable;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.Comparator;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import kernbeisser.CustomComponents.AccessChecking.Getter;
import kernbeisser.Exeptions.PermissionKeyRequiredException;
import org.intellij.lang.annotations.MagicConstant;

public interface Column<T> {
  Color STRIPED_BACKGROUND_COLOR_A = new Color(240, 240, 240);
  Color STRIPED_BACKGROUND_COLOR_B = new DefaultTableCellRenderer().getBackground();
  TableCellRenderer DEFAULT_RENDERER = new DefaultTableCellRenderer();
  TableCellRenderer DEFAULT_STRIPED_RENDERER =
      new StripedRenderer(STRIPED_BACKGROUND_COLOR_A, STRIPED_BACKGROUND_COLOR_B);
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

  static <T> Column<T> create(String s, Getter<T, Object> v) {
    return create(s, v, DEFAULT_ALIGNMENT);
  }

  static <T> Column<T> create(String s, Getter<T, Object> v, @MagicConstant int alignment) {
    return create(s, v, alignment, true);
  }

  static <T> Column<T> create(
      String s, Getter<T, Object> v, @MagicConstant int alignment, Comparator<Object> comparator) {
    return create(s, v, alignment, true, e -> {}, comparator);
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
    return create(s, v, alignment, striped, onAction, DEFAULT_SORTER);
  }

  static <T> Column<T> create(
      String s,
      Getter<T, Object> v,
      int alignment,
      boolean striped,
      Consumer<T> onAction,
      Comparator<Object> sorter) {
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
        } catch (PermissionKeyRequiredException e) {
          read = false;
          return getValue(t);
        }
      }

      @Override
      public TableCellRenderer getRenderer() {
        DefaultTableCellRenderer renderer =
            striped ? new StripedRenderer() : new DefaultTableCellRenderer();
        renderer.setHorizontalAlignment(alignment);
        return renderer;
      }

      @Override
      public void onAction(MouseEvent e, T t) {
        onAction.accept(t);
      }

      @Override
      public Comparator<Object> sorter() {
        return sorter;
      }
    };
  }

  static <T> Column<T> createIcon(Icon icon, Consumer<T> onAction) {
    return createIcon(icon, onAction, e -> true);
  }

  static <T> Column<T> createIcon(
          String name, Function<T, Icon> iconFunction, Consumer<T> consumer) {
    return createIcon(name, iconFunction, consumer, null, DEFAULT_ICON_WIDTH);
  }

  static <T> Column<T> createIcon(
      String name, Function<T, Icon> iconFunction, Consumer<T> consumer, Consumer<T> rmConsumer, int width) {
    return new Column<T>() {
      @Override
      public String getName() {
        return name;
      }

      @Override
      public Object getValue(T t) throws PermissionKeyRequiredException {
        return t;
      }

      @Override
      public void onAction(MouseEvent e, T t) {
        if (rmConsumer != null && SwingUtilities.isRightMouseButton(e)) {
          rmConsumer.accept(t);
        } else {
          consumer.accept(t);
        }
      }

      @Override
      public TableCellRenderer getRenderer() {
        StripedRenderer renderer =
            new StripedRenderer() {
              @Override
              public Component getTableCellRendererComponent(
                  JTable table,
                  Object value,
                  boolean isSelected,
                  boolean hasFocus,
                  int row,
                  int column) {
                setIcon(iconFunction.apply((T) value));
                return super.getTableCellRendererComponent(
                    table, "", isSelected, hasFocus, row, column);
              }
            };
        renderer.setHorizontalAlignment(DEFAULT_ALIGNMENT);
        return renderer;
      }

      @Override
      public void adjust(TableColumn column) {
        column.setMaxWidth(width);
      }
    };
  }

  static <T> Column<T> createIcon(Icon icon, Consumer<T> onAction, Predicate<T> onlyIf) {
    return new Column<T>() {

      @Override
      public String getName() {
        return "";
      }

      @Override
      public Object getValue(T t) throws PermissionKeyRequiredException {
        return onlyIf.test(t);
      }

      @Override
      public TableCellRenderer getRenderer() {
        StripedRenderer renderer =
            new StripedRenderer() {
              @Override
              public Component getTableCellRendererComponent(
                  JTable table,
                  Object value,
                  boolean isSelected,
                  boolean hasFocus,
                  int row,
                  int column) {
                setIcon((boolean) value ? icon : null);
                return super.getTableCellRendererComponent(
                    table, "", isSelected, hasFocus, row, column);
              }
            };
        renderer.setHorizontalAlignment(DEFAULT_ALIGNMENT);
        renderer.setIcon(icon);
        return renderer;
      }

      @Override
      public void adjust(TableColumn column) {
        column.setMaxWidth(DEFAULT_ICON_WIDTH);
      }

      @Override
      public void onAction(MouseEvent e, T t) {
        if (onlyIf.test(t)) onAction.accept(t);
      }
    };
  }

  String getName();

  Object getValue(T t) throws PermissionKeyRequiredException;

  default void onAction(MouseEvent e, T t) {}

  default TableCellRenderer getRenderer() {
    return DEFAULT_RENDERER;
  }

  default void adjust(TableColumn column) {}

  default Comparator<Object> sorter() {
    return DEFAULT_SORTER;
  }
}
