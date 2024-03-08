package kernbeisser.CustomComponents.ObjectTable.Columns;

import static javax.swing.SwingConstants.RIGHT;

import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.function.Consumer;
import javax.swing.SwingUtilities;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import kernbeisser.CustomComponents.ObjectTable.Adjustors.SimpleCellAdjustor;
import kernbeisser.CustomComponents.ObjectTable.Adjustors.StripedCellAdjustor;
import kernbeisser.CustomComponents.ObjectTable.Adjustors.TableCellAdjustor;
import kernbeisser.CustomComponents.ObjectTable.Adjustors.TableColumnAdjustor;
import kernbeisser.CustomComponents.ObjectTable.Column;
import kernbeisser.CustomComponents.ObjectTable.Renderer.AdjustableTableCellRenderer;
import kernbeisser.Security.Utils.Getter;
import kernbeisser.Useful.Tools;
import org.intellij.lang.annotations.MagicConstant;
import org.jetbrains.annotations.NotNull;
import rs.groump.AccessDeniedException;

public class CustomizableColumn<T> extends DefaultColumn<T> {

  private final Getter<T, Object> propertyFactory;
  private final ArrayList<ColumnObjectSelectionListener<T>> listeners = new ArrayList<>();
  private final AdjustableTableCellRenderer<T> renderer = new AdjustableTableCellRenderer<>();
  private final Collection<TableColumnAdjustor> tableColumnAdjustors = new ArrayList<>();
  private Comparator<Object> sorter = Column.DEFAULT_SORTER;

  public CustomizableColumn(@NotNull String name, @NotNull Getter<T, Object> propertyFactory) {
    super(name);
    this.propertyFactory = propertyFactory;
    renderer.addTableCellAdjustor(new StripedCellAdjustor<T>());
  }

  public CustomizableColumn<T> withCellAdjustor(@NotNull TableCellAdjustor<T> adjustor) {
    renderer.addTableCellAdjustor(adjustor);
    return this;
  }

  public CustomizableColumn<T> withColumnAdjustor(@NotNull TableColumnAdjustor adjustor) {
    tableColumnAdjustors.add(adjustor);
    return this;
  }

  public CustomizableColumn<T> withListener(@NotNull ColumnObjectSelectionListener<T> listener) {
    listeners.add(listener);
    return this;
  }

  public CustomizableColumn<T> withSorter(@NotNull Comparator<Object> sorter) {
    this.sorter = sorter;
    if (sorter.equals(Column.NUMBER_SORTER)) {
      return this.withHorizontalAlignment(RIGHT);
    }
    return this;
  }

  public CustomizableColumn<T> withDefaultFilter() {
    setUsesStandardFiler(true);
    return this;
  }

  public CustomizableColumn<T> withRightClickConsumer(@NotNull Consumer<T> clickConsumer) {
    return withListener(
        (e, t) -> {
          if (SwingUtilities.isRightMouseButton(e)) {
            clickConsumer.accept(t);
          }
        });
  }

  public CustomizableColumn<T> withLeftClickConsumer(@NotNull Consumer<T> clickConsumer) {
    return withListener(
        (e, t) -> {
          if (SwingUtilities.isLeftMouseButton(e)) {
            clickConsumer.accept(t);
          }
        });
  }

  public CustomizableColumn<T> withHorizontalAlignment(@MagicConstant int horizontalAlignment) {
    withCellAdjustor(
        (SimpleCellAdjustor<T>) (comp, t) -> comp.setHorizontalAlignment(horizontalAlignment));
    return this;
  }

  public CustomizableColumn<T> withPreferredWidth(int preferredWidth) {
    withColumnAdjustor(
        column -> column.setPreferredWidth(Tools.scaleWithLabelScalingFactor(preferredWidth)));
    return this;
  }

  @Override
  public Object getValue(T t) throws AccessDeniedException {
    return propertyFactory.get(t);
  }

  @Override
  public void onAction(MouseEvent e, T t) {
    listeners.forEach(listener -> listener.onAction(e, t));
  }

  @Override
  public TableCellRenderer getRenderer() {
    return renderer;
  }

  @Override
  public void adjust(TableColumn column) {
    tableColumnAdjustors.forEach(e -> e.adjust(column));
  }

  @Override
  public Comparator<Object> sorter() {
    return sorter;
  }
}
