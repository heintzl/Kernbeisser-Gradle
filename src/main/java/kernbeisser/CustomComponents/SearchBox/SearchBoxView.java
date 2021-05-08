package kernbeisser.CustomComponents.SearchBox;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Collection;
import java.util.Optional;
import javax.swing.*;
import jiconfont.icons.font_awesome.FontAwesome;
import jiconfont.swing.IconFontSwing;
import kernbeisser.CustomComponents.ObjectTable.Column;
import kernbeisser.CustomComponents.ObjectTable.ObjectSelectionListener;
import kernbeisser.CustomComponents.ObjectTable.ObjectTable;
import kernbeisser.CustomComponents.ObjectTable.RowFilter;
import kernbeisser.Windows.MVC.IView;
import kernbeisser.Windows.MVC.Linked;
import org.jetbrains.annotations.NotNull;

public class SearchBoxView<T> implements IView<SearchBoxController<T>> {
  private JButton search;
  private JTextField searchInput;
  private ObjectTable<T> objects;
  private JPanel main;

  @Linked private SearchBoxController<T> controller;

  void setObjects(Collection<T> objects) {
    this.objects.setObjects(objects);
  }

  void setColumns(Collection<Column<T>> columns) {
    objects.setColumns(columns);
  }

  String getSearch() {
    return searchInput.getText();
  }

  public Optional<T> getSelectedObject() {
    return objects.getSelectedObject();
  }

  private void createUIComponents() {
    objects = new ObjectTable<>();
  }

  @Override
  public void initialize(SearchBoxController<T> controller) {
    search.setIcon(IconFontSwing.buildIcon(FontAwesome.SEARCH, 14, new Color(0x757EFF)));
    search.addActionListener(e -> controller.invokeSearch());
    searchInput.addKeyListener(
        new KeyAdapter() {
          @Override
          public void keyReleased(KeyEvent e) {
            controller.invokeSearch();
          }
        });
  }

  void addSelectionListener(ObjectSelectionListener<T> selectionListener) {
    objects.addSelectionListener(selectionListener);
  }

  void addDoubleClickListener(ObjectSelectionListener<T> doubleSelectionListener) {
    objects.addDoubleClickListener(doubleSelectionListener);
  }

  @Override
  public @NotNull JComponent getContent() {
    return main;
  }

  @Override
  public @NotNull Dimension getSize() {
    return main.getSize();
  }

  public void setSearch(String s) {
    searchInput.setText(s);
  }

  @Override
  public Component getFocusOnInitialize() {
    return searchInput;
  }

  public void setRowFilter(RowFilter<T> rowFilter) {
    objects.setRowFilter(rowFilter);
  }
}
