package kernbeisser.Windows.PrintLabels;

import java.util.*;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.NoResultException;
import kernbeisser.CustomComponents.ObjectTable.Column;
import kernbeisser.CustomComponents.ObjectTable.Columns.Columns;
import kernbeisser.DBConnection.DBConnection;
import kernbeisser.DBEntities.Article;
import kernbeisser.DBEntities.ArticlePrintPool;
import kernbeisser.DBEntities.Articles;
import kernbeisser.Enums.ShopRange;
import kernbeisser.Forms.ObjectForm.Components.Source;
import kernbeisser.Reports.ArticleLabel;
import kernbeisser.Useful.Tools;
import kernbeisser.Windows.CollectionView.CollectionController;
import kernbeisser.Windows.MVC.IModel;
import lombok.Cleanup;
import lombok.Getter;

public class PrintLabelsModel implements IModel<PrintLabelsController> {

  @Getter private Map<Article, Integer> printPoolMapBefore;
  @Getter private Map<Article, Integer> printPoolMap;

  public static CollectionController<Article> getArticleSource() {
    return new CollectionController<>(
        new ArrayList<>(),
        Source.empty(),
        Columns.create("Name", Article::getName)
            .withDefaultFilter()
            .withColumnAdjustor(e -> e.setPreferredWidth(200)),
        Columns.create("Ladennummer", Article::getKbNumber)
            .withSorter(Column.NUMBER_SORTER)
            .withDefaultFilter(),
        Columns.create("Lieferantennummer", Article::getSuppliersItemNumber)
            .withSorter(Column.NUMBER_SORTER)
            .withDefaultFilter());
  }

  public int getPrintPool(Article article) {
    return Optional.ofNullable(printPoolMap.get(article)).orElse(0);
  }

  public void setPrintPoolBefore(Map<Article, Integer> printPool) {
    printPoolMapBefore = new HashMap<>(printPool);
    printPoolMap = new HashMap<>(printPool);
  }

  public static Collection<Article> getAllArticles() {
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

  static String getArticleSupplierName(Article article) {
    return article.getSupplier().getName();
  }

  void print(CollectionController<Article> articles) {
    List<Article> printPool =
        articles.getModel().getLoaded().stream()
            .flatMap(a -> Collections.nCopies(ArticlePrintPool.get(a), a).stream())
            .collect(Collectors.toList());
    new ArticleLabel(printPool)
        .sendToPrinter("Drucke Ladenschilder", Tools::showUnexpectedErrorWarning);
  }

  void setPrintPool(Article article, int numberOfLabels) {
    printPoolMap.put(article, numberOfLabels);
  }

  public static Article getByBarcode(String s) throws NoResultException {
    return Articles.getByBarcode(Long.parseLong(s)).orElseThrow(NoResultException::new);
  }
}
