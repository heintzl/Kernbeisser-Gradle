package kernbeisser.CustomComponents.SearchBox.Filters;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.swing.*;
import kernbeisser.DBEntities.Article;
import lombok.Setter;

public class ArticleFilter {

  @Setter private boolean filterNoBarcode = false;
  @Setter private boolean filterShowInShop = false;
  @Setter private boolean filterShopRange = true;

  Runnable callback;

  public ArticleFilter(Runnable refreshMethod) {
    callback = refreshMethod;
  }

  public Collection<Article> searchable(String query, int max) {
    return Article.getDefaultAll(
        query,
        e ->
            !(filterNoBarcode && e.getBarcode() != null)
                && !(filterShowInShop && !e.isShowInShop())
                && !(filterShopRange && !e.isShopRange()),
        max);
  }

  public List<JCheckBox> createFilterCheckboxes() {
    List<JCheckBox> checkBoxes = new ArrayList<>();
    final JCheckBox noBarcode = new JCheckBox("nur ohne Barcode");
    noBarcode.addActionListener(
        e -> {
          filterNoBarcode = noBarcode.isSelected();
          callback.run();
        });
    noBarcode.setSelected(false);
    checkBoxes.add(noBarcode);
    final JCheckBox showInShop = new JCheckBox("Favoriten Artikel");
    showInShop.addActionListener(
        e -> {
          filterShowInShop = showInShop.isSelected();
          callback.run();
        });
    showInShop.setSelected(false);
    checkBoxes.add(showInShop);
    JCheckBox onlyShopRange = new JCheckBox("nur KB Sortiment");
    onlyShopRange.addActionListener(
        e -> {
          filterShopRange = onlyShopRange.isSelected();
          callback.run();
        });
    onlyShopRange.setSelected(true);
    checkBoxes.add(onlyShopRange);
    return checkBoxes;
  }
}
