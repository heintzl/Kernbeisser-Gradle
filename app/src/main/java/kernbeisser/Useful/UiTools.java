package kernbeisser.Useful;

import kernbeisser.CustomComponents.ObjectTable.Column;
import kernbeisser.CustomComponents.ObjectTable.Columns.Columns;
import kernbeisser.CustomComponents.ObjectTable.ObjectTable;
import kernbeisser.DBEntities.Article;

import java.awt.*;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import javax.swing.*;

public class UiTools {

  private static final HashMap<Object, Object> DEFAULT_UI = new HashMap<>(UIManager.getDefaults());

  public static final int DEFAULT_LABEL_SIZE = new JLabel().getFont().getSize();

  public static void reset() {
    Enumeration<Object> keys = UIManager.getDefaults().keys();
    while (keys.hasMoreElements()) {
      Object key = keys.nextElement();
      if (key.toString().endsWith(".font")) {
        UIManager.put(key, DEFAULT_UI.get(key));
      }
    }
    UIManager.put("Table.rowHeight", DEFAULT_UI.get("Table.rowHeight"));
  }

  public static void scaleFonts(float scaleFactor) {
    reset();
    Enumeration<Object> keys = UIManager.getDefaults().keys();
    while (keys.hasMoreElements()) {
      Object key = keys.nextElement();
      Font before = UIManager.getFont(key);
      if (key.toString().endsWith(".font")) {
        UIManager.put(
            key,
            new Font(
                before.getName(), before.getStyle(), Math.round(before.getSize() * scaleFactor)));
      }
    }
    // maybe work not for all LAFs
    UIManager.put("Table.rowHeight", (int) ((int) UIManager.get("Table.rowHeight") * scaleFactor));
  }

  public static void showArticleList(Component parentComponent, List<Article> articles) {
    ObjectTable<Article> table =
            new ObjectTable<>(
                    articles,
                    Columns.create("kbNumber", Article::getKbNumber)
                            .withSorter(Column.NUMBER_SORTER)
                            .withPreferredWidth(100),
                    Columns.create("Artikel", Article::getName).withPreferredWidth(500));
    JOptionPane.showMessageDialog(
            parentComponent, new JScrollPane(table), "Artikel", JOptionPane.INFORMATION_MESSAGE);
  }
}
