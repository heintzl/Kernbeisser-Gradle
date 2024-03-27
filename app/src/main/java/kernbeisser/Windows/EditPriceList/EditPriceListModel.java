package kernbeisser.Windows.EditPriceList;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import java.util.ArrayList;
import java.util.Collection;
import java.util.function.BiFunction;
import javax.swing.*;
import kernbeisser.CustomComponents.ObjectTable.Columns.Columns;
import kernbeisser.DBConnection.DBConnection;
import kernbeisser.DBConnection.QueryBuilder;
import kernbeisser.DBEntities.Article;
import kernbeisser.DBEntities.PriceList;
import kernbeisser.DBEntities.TypeFields.ArticleField;
import kernbeisser.Enums.ShopRange;
import kernbeisser.Forms.ObjectForm.Components.Source;
import kernbeisser.Windows.CollectionView.CollectionController;
import kernbeisser.Windows.MVC.IModel;
import lombok.Cleanup;
import lombok.Getter;

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
    return QueryBuilder.selectAll(Article.class)
        .where(ArticleField.shopRange.eq(ShopRange.NOT_IN_RANGE).not())
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
    ArrayList<Article> addedToPriceList = new ArrayList<>(articles);
    addedToPriceList.removeAll(priceListBefore);
    ArrayList<Article> removedFromPriceList = new ArrayList<>(priceListBefore);
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
