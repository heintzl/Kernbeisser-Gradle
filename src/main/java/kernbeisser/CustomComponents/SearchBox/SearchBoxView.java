package kernbeisser.CustomComponents.SearchBox;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Collection;
import java.util.function.Predicate;
import javax.swing.*;
import jiconfont.icons.font_awesome.FontAwesome;
import jiconfont.swing.IconFontSwing;
import kernbeisser.CustomComponents.ObjectTable.Column;
import kernbeisser.CustomComponents.ObjectTable.ObjectTable;
import kernbeisser.CustomComponents.ObjectTable.RowFilter;
import kernbeisser.Useful.Tools;
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

  public T getSelectedObject() {
    return objects.getSelectedObject();
  }

  private void createUIComponents() {
    objects = new ObjectTable<>();
  }

  @Override
  public void initialize(SearchBoxController<T> controller) {
    search.setIcon(IconFontSwing.buildIcon(FontAwesome.SEARCH, 14, new Color(0x757EFF)));
    objects.addSelectionListener(e -> controller.select());
    search.addActionListener(e -> controller.search());
    searchInput.addKeyListener(
        new KeyAdapter() {
          @Override
          public void keyReleased(KeyEvent e) {
            controller.search();
          }
        });
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

  public void setSelectedObject(T value) {
    objects.setSelectedObject(value);
  }

  public boolean setSelectedObjectId(Object o) {
    T t = Tools.findById(objects.getItems(), o);
    objects.setSelectedObject(t);
    return t != null;
  }

  @Override
  public Component getFocusOnInitialize() {
    return searchInput;
  }

  public void setRowFilter(RowFilter<T> rowFilter) {
    objects.setRowFilter(rowFilter);
  }

  public void selectObject(Predicate<T> predicate) {
    objects.setSelectedObject(objects.get(predicate));
  }
}
