package kernbeisser.CustomComponents.SearchBox.Filters;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.swing.*;
import kernbeisser.DBEntities.Article;
import kernbeisser.DBEntities.Articles;
import kernbeisser.DBEntities.Supplier;
import lombok.Setter;

public class ArticleFilter implements SearchBoxFilter<Article> {

  @Setter private boolean filterNoBarcode = false;
  @Setter private boolean filterShowInShop = false;
  @Setter private boolean filterShopRange = true;
  @Setter private boolean filterKK = false;

  Runnable callback;

  public ArticleFilter(Runnable refreshMethod) {
    callback = refreshMethod;
  }

  public Collection<Article> searchable(String query, int max) {
    Supplier kkSupplier = Supplier.getKKSupplier();
    return Articles.getDefaultAll(
        query,
        e ->
            (!filterNoBarcode || e.getBarcode() == null)
                && (!filterShowInShop || e.isShowInShop())
                && (!filterShopRange || e.getShopRange().isVisible())
                && (!filterKK || e.getSupplier().equals(kkSupplier)),
        10000);
  }

  public List<JComponent> createFilterUIComponents() {
    List<JComponent> checkBoxes = new ArrayList<>();
    final JCheckBox noBarcode = new JCheckBox("nur ohne Barcode");
    noBarcode.addActionListener(
        e -> {
          filterNoBarcode = noBarcode.isSelected();
          callback.run();
        });
    noBarcode.setSelected(filterNoBarcode);
    checkBoxes.add(noBarcode);
    final JCheckBox showInShop = new JCheckBox("Favoriten Artikel");
    showInShop.addActionListener(
        e -> {
          filterShowInShop = showInShop.isSelected();
          callback.run();
        });
    showInShop.setSelected(filterShowInShop);
    checkBoxes.add(showInShop);
    final JCheckBox onlyShopRange = new JCheckBox("nur KB Sortiment");
    onlyShopRange.addActionListener(
        e -> {
          filterShopRange = onlyShopRange.isSelected();
          callback.run();
        });
    onlyShopRange.setSelected(filterShopRange);
    checkBoxes.add(onlyShopRange);
    final JCheckBox onlyKK = new JCheckBox("nur Kornkraft Sortiment");
    onlyKK.setName("KKFilter");
    onlyKK.addActionListener(
        e -> {
          filterKK = onlyKK.isSelected();
          callback.run();
        });
    onlyKK.setSelected(filterKK);
    checkBoxes.add(onlyKK);
    return checkBoxes;
  }
}
