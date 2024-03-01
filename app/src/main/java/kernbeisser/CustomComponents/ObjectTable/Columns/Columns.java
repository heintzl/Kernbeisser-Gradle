package kernbeisser.CustomComponents.ObjectTable.Columns;

import java.util.Comparator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import javax.swing.Icon;
import kernbeisser.CustomComponents.ObjectTable.Adjustors.IconCustomizer;
import kernbeisser.Security.Utils.Getter;
import org.jetbrains.annotations.NotNull;

public class Columns {
  public static <T> @NotNull CustomizableColumn<T> create(
      @NotNull String name, @NotNull Getter<T, Object> propertyFactory) {
    return new CustomizableColumn<>(name, propertyFactory);
  }

  public static <T> @NotNull CustomizableColumn<T> create(
      @NotNull String name,
      @NotNull Getter<T, Object> propertyFactory,
      @NotNull Consumer<T> consumer) {
    return new CustomizableColumn<>(name, propertyFactory)
        .withListener((e, t) -> consumer.accept(t));
  }

  public static <T> @NotNull CustomizableColumn<T> create(
      @NotNull String name, @NotNull Getter<T, Object> propertyFactory, int alignment) {
    return new CustomizableColumn<>(name, propertyFactory).withHorizontalAlignment(alignment);
  }

  public static <T> @NotNull CustomizableColumn<T> create(
      @NotNull String name,
      @NotNull Getter<T, Object> propertyFactory,
      int alignment,
      Comparator<Object> sorter) {
    return new CustomizableColumn<>(name, propertyFactory)
        .withHorizontalAlignment(alignment)
        .withSorter(sorter);
  }

  public static <T> @NotNull CustomizableColumn<T> createIconColumn(
      @NotNull String name,
      @NotNull Function<T, Icon> iconFactory,
      @NotNull Consumer<T> leftClick,
      @NotNull Consumer<T> rightClick,
      int columnWith) {
    return new CustomizableColumn<T>(name, e -> "")
        .withCellAdjustor(new IconCustomizer<T>(iconFactory))
        .withLeftClickConsumer(leftClick)
        .withRightClickConsumer(rightClick)
        .withColumnAdjustor(e -> e.setPreferredWidth(columnWith));
  }

  public static <T> @NotNull CustomizableColumn<T> createIconColumn(
      @NotNull String name, @NotNull Function<T, Icon> iconFactory) {
    return new CustomizableColumn<T>(name, e -> "")
        .withCellAdjustor(new IconCustomizer<T>(iconFactory));
  }

  public static <T> @NotNull CustomizableColumn<T> createIconColumn(
      @NotNull String name, @NotNull Function<T, Icon> iconFactory, Predicate<T> onlyIf) {
    return new CustomizableColumn<T>(name, e -> "")
        .withCellAdjustor(new IconCustomizer<T>(iconFactory));
  }

  public static <T> @NotNull CustomizableColumn<T> createIconColumn(
      @NotNull Icon icon, @NotNull Consumer<T> consumer, Predicate<T> onlyIf) {
    return new CustomizableColumn<T>("", e -> "")
        .withColumnAdjustor((e) -> e.setMaxWidth(icon.getIconWidth() + 5))
        .withCellAdjustor(
            (component, t, isSelected, hasFocus, row, column) ->
                component.setIcon(onlyIf.test(t) ? icon : null))
        .withLeftClickConsumer(
            e -> {
              if (onlyIf.test(e)) consumer.accept(e);
            });
  }
}
