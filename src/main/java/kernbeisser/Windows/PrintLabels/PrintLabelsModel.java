package kernbeisser.Windows.PrintLabels;

import java.util.*;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import kernbeisser.CustomComponents.ObjectTable.Columns.Columns;
import kernbeisser.DBConnection.DBConnection;
import kernbeisser.DBEntities.Article;
import kernbeisser.Enums.ShopRange;
import kernbeisser.Forms.ObjectForm.Components.Source;
import kernbeisser.Reports.ArticleLabel;
import kernbeisser.Useful.Tools;
import kernbeisser.Windows.CollectionView.CollectionController;
import kernbeisser.Windows.MVC.IModel;
import lombok.Cleanup;
import lombok.Getter;
import lombok.Setter;

public class PrintLabelsModel implements IModel<PrintLabelsController> {

  @Getter @Setter private Collection<Article> printPoolBefore;

  public static CollectionController<Article> getArticleSource() {
    return new CollectionController<>(
        new ArrayList<>(),
        Source.empty(),
        Columns.create("Lieferant", PrintLabelsModel::getArticleSupplierName),
        Columns.create("Name", Article::getName),
        Columns.create("Ladennummer", Article::getKbNumber),
        Columns.create("Lieferantennummer", Article::getSuppliersItemNumber),
        Columns.create("Barcode", Article::getBarcode),
        Columns.create("Preisliste", Article::getPriceList));
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

  void print(CollectionController<Article> articles) {
    new ArticleLabel(
            articles.getModel().getLoaded().stream().distinct().collect(Collectors.toList()))
        .sendToPrinter("Drucke Ladenschilder", Tools::showUnexpectedErrorWarning);
    articles.selectAllChosen();
  }
}
