package kernbeisser.CustomComponents.SearchBox;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Collection;
import javax.swing.*;
import jiconfont.icons.font_awesome.FontAwesome;
import jiconfont.swing.IconFontSwing;
import kernbeisser.CustomComponents.ObjectTable.Column;
import kernbeisser.CustomComponents.ObjectTable.ObjectTable;
import kernbeisser.Useful.Tools;
import kernbeisser.Windows.View;
import org.jetbrains.annotations.NotNull;

public class SearchBoxView<T> extends JPanel implements View<SearchBoxController<T>> {
  private JButton search;
  private JTextField searchInput;
  private ObjectTable<T> objects;
  private JPanel main;

  private final SearchBoxController<T> controller;

  SearchBoxView(SearchBoxController<T> controller) {
    this.controller = controller;
  }

  void setObjects(Collection<T> objects) {
    this.objects.setObjects(objects);
    repaint();
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
    add(main);
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
    return super.getSize();
  }

  public void setSearch(String s) {
    searchInput.setText(s);
  }

  public void setSelectedObject(T value) {
    objects.setSelectedObject(value);
  }

  public boolean setSelectedObjectId(Object o) {
    T t = Tools.findById(objects.getItems(),o);
    objects.setSelectedObject(t);
    return t != null;
  }
}
