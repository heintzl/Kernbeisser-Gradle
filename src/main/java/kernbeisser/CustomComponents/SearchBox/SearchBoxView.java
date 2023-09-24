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
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

public class SearchBoxView<T> implements IView<SearchBoxController<T>> {

  private JButton search;
  private JTextField searchInput;
  private ObjectTable<T> objectTable;
  private JPanel main;
  @Getter private JPanel extraOptionsPanel;

  @Linked private SearchBoxController<T> controller;

  void setObjectTable(Collection<T> objectTable) {
    this.objectTable.setObjects(objectTable);
  }

  void setColumns(Collection<Column<T>> columns) {
    objectTable.setColumns(columns);
  }

  String getSearch() {
    return searchInput.getText();
  }

  public Optional<T> getSelectedObject() {
    return objectTable.getSelectedObject();
  }

  private void createUIComponents() {
    objectTable = new ObjectTable<>();
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
    objectTable.addSelectionListener(selectionListener);
  }

  void addDoubleClickListener(ObjectSelectionListener<T> doubleSelectionListener) {
    objectTable.addDoubleClickListener(doubleSelectionListener);
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
    objectTable.setRowFilter(rowFilter);
  }

  public ObjectTable<T> getObjectTable() {
    return objectTable;
  }
}
