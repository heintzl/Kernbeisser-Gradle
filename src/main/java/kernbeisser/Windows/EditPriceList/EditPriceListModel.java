package kernbeisser.Windows.EditPriceList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.BiFunction;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.swing.*;
import kernbeisser.CustomComponents.ObjectTable.Columns.Columns;
import kernbeisser.DBConnection.DBConnection;
import kernbeisser.DBEntities.Article;
import kernbeisser.DBEntities.PriceList;
import kernbeisser.Enums.ShopRange;
import kernbeisser.Forms.ObjectForm.Components.Source;
import kernbeisser.Windows.CollectionView.CollectionController;
import kernbeisser.Windows.MVC.IModel;
import lombok.Cleanup;
import lombok.Getter;
import lombok.var;

public class EditPriceListModel implements IModel<EditPriceListController> {

  @Getter private final PriceList priceList;
  private final Collection<Article> priceListBefore;

  EditPriceListModel(PriceList priceList) {
    this.priceList = priceList;
    priceListBefore = priceList.getAllArticles();
  }

  public static CollectionController<Article> getArticleSource() {
    return new CollectionController<>(
        new ArrayList<>(),
        Source.empty(),
        Columns.create("Lieferant", EditPriceListModel::getArticleSupplierName).withDefaultFilter(),
        Columns.create("Name", Article::getName).withDefaultFilter(),
        Columns.create("Ladennummer", Article::getKbNumber),
        Columns.create("Lieferantennummer", Article::getSuppliersItemNumber).withDefaultFilter(),
        Columns.create("Barcode", Article::getBarcode),
        Columns.create("Preisliste", Article::getPriceList).withDefaultFilter());
  }

  public Collection<Article> getAllArticles() {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    @Cleanup(value = "commit")
    EntityTransaction et = em.getTransaction();
    et.begin();
    return em.createQuery(
            "select a from Article a where shopRange <> "
                + ShopRange.NOT_IN_RANGE.ordinal()
                + " order by name",
            Article.class)
        .getResultList();
  }

  private static String getArticleSupplierName(Article article) {
    return article.getSupplier().getName();
  }

  public boolean persistChanges(
      Collection<Article> articles, BiFunction<Integer, Integer, Integer> confirmValues) {
    if (articles.equals(priceListBefore)) {
      return true;
    }
    var addedToPriceList = new ArrayList<>(articles);
    addedToPriceList.removeAll(priceListBefore);
    var removedFromPriceList = new ArrayList<>(priceListBefore);
    removedFromPriceList.removeAll(articles);
    int confirmation =
        confirmValues.apply(
            (int) addedToPriceList.stream().filter(a -> a.getPriceList() != null).count(),
            removedFromPriceList.size());
    if (confirmation != JOptionPane.YES_OPTION) {
      return confirmation == JOptionPane.NO_OPTION;
    }
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    @Cleanup(value = "commit")
    EntityTransaction et = em.getTransaction();
    et.begin();
    for (Article a : addedToPriceList) {
      a.setPriceList(priceList);
      em.merge(a);
    }
    for (Article a : removedFromPriceList) {
      a.setPriceList(null);
      em.merge(a);
    }
    return true;
  }
}
