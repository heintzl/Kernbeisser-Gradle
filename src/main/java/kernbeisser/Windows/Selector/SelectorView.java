package kernbeisser.Windows.Selector;

import java.awt.*;
import java.util.Arrays;
import java.util.Collection;
import javax.swing.*;
import jiconfont.icons.font_awesome.FontAwesome;
import jiconfont.swing.IconFontSwing;
import kernbeisser.CustomComponents.ObjectTable.Column;
import kernbeisser.CustomComponents.ObjectTable.ObjectTable;
import kernbeisser.Windows.MVC.Linked;
import kernbeisser.Windows.MVC.View;
import org.jetbrains.annotations.NotNull;

public class SelectorView<T> implements View<SelectorController<T>> {
  private JPanel mainPanel;
  private ObjectTable<T> selected;
  private JButton add;
  private JButton remove;
  private JLabel title;

  @Linked private SelectorController<T> controller;

  private void createUIComponents() {
    selected = new ObjectTable<T>();
  }

  void setColumns(Column<T>[] columns) {
    selected.setColumns(Arrays.asList(columns));
  }

  void setObjects(Collection<T> collection) {
    selected.setObjects(collection);
  }

  T getSelectedValue() {
    return selected.getSelectedObject();
  }

  public void addValue(T e) {
    if (!selected.contains(e)) {
      selected.add(e);
    }
  }

  public void removeValue(T e) {
    selected.remove(e);
  }

  public void setTitle(String title) {
    this.title.setText(title);
  }

  @Override
  public void initialize(SelectorController<T> controller) {
    add.addActionListener(e -> controller.add());
    add.setIcon(IconFontSwing.buildIcon(FontAwesome.PLUS, 20, Color.GREEN));
    remove.addActionListener(e -> controller.remove());
    remove.setIcon(IconFontSwing.buildIcon(FontAwesome.TRASH, 20, Color.RED));
  }

  @Override
  public @NotNull JComponent getContent() {
    return mainPanel;
  }
}
