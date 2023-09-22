package kernbeisser.Windows.EditArticles;

import java.util.*;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import kernbeisser.DBConnection.DBConnection;
import kernbeisser.DBEntities.Article;
import kernbeisser.DBEntities.Articles;
import kernbeisser.DBEntities.CatalogEntry;
import kernbeisser.DBEntities.Supplier;
import kernbeisser.Tasks.ArticleComparedToCatalogEntry;
import kernbeisser.Useful.Tools;
import kernbeisser.Windows.MVC.IModel;
import lombok.Cleanup;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EditArticlesModel implements IModel<EditArticlesController> {
  private final Logger logger = LogManager.getLogger(EditArticlesModel.class);

  private Map<String, CatalogEntry> getCatalogMap(Collection<Article> articles) {
    Supplier kkSupplier = Supplier.getKKSupplier();
    Collection<Integer> articleNos = new ArrayList<>();
    articles.stream()
        .filter(e -> e.getSupplier().equals(kkSupplier))
        .mapToInt(Article::getSuppliersItemNumber)
        .forEach(articleNos::add);

    return DBConnection.getConditioned(CatalogEntry.class, "artikelNr", articleNos).stream()
        .filter(
            e ->
                Boolean.FALSE == e.getAktionspreis()
                    && (e.getEanLadenEinheit() != null
                        || e.getGebindePfand() != 0.0
                        || e.getEinzelPfand() != 0.0))
        .collect(Collectors.toMap(CatalogEntry::getArtikelNr, c -> c));
  }

  public List<ArticleComparedToCatalogEntry> previewCatalog(Collection<Article> articles) {
    List<ArticleComparedToCatalogEntry> differences = new ArrayList<>();
    Map<String, CatalogEntry> catalogMap = getCatalogMap(articles);
    for (Article article : articles) {
      CatalogEntry correspondingCatalogEntry =
          catalogMap.get(Integer.toString(article.getSuppliersItemNumber()));
      if (correspondingCatalogEntry == null) {
        continue;
      }
      ArticleComparedToCatalogEntry compared =
          new ArticleComparedToCatalogEntry(article, correspondingCatalogEntry);
      List<String> fieldDifferences = compared.getFieldDifferences();
      if (fieldDifferences.size() > 0) {
        compared.setDescription(String.join(", ", fieldDifferences));
        differences.add(compared);
      }
    }
    return differences;
  }

  public void mergeCatalog(Collection<Article> articles) {
    Map<String, CatalogEntry> catalogMap = getCatalogMap(articles);
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    @Cleanup(value = "commit")
    EntityTransaction et = em.getTransaction();
    et.begin();
    try {
      for (Article article : articles) {
        String log = "";
        CatalogEntry catalogEntry =
            catalogMap.get(Integer.toString(article.getSuppliersItemNumber()));
        if (catalogEntry == null) {
          continue;
        }
        log += "Artikel " + article.getSuppliersItemNumber() + ":";
        long barcode = Tools.ifNull(catalogEntry.getEanLadenEinheit(), 0L);
        double singleDeposit = catalogEntry.getEinzelPfand();
        double containerDeposit = catalogEntry.getGebindePfand();
        Article persistedArticle = em.find(Article.class, article.getId());
        if (barcode > 1E10) {
          Optional<Article> sameBarcode = Articles.getByBarcode(barcode);
          if (sameBarcode.isPresent()) {
            if (sameBarcode.get().equals(article)) {
              continue;
            }
            logger.warn(
                log
                    + String.format(
                        " hat den selben Barcode, wie der Artikel mit der KB-Artikelnummer %d und wird daher übersprungen",
                        sameBarcode.get().getKbNumber()));
            continue;
          }
          log += String.format(" bc %s -> %s |", article.getBarcode(), barcode);
          persistedArticle.setBarcode(barcode);
        }
        if (singleDeposit > 0.0) {
          log += String.format(" E.-Pf. %.2f -> %.2f |", article.getSingleDeposit(), singleDeposit);
          persistedArticle.setSingleDeposit(singleDeposit);
        }
        if (containerDeposit > 0.0) {
          log +=
              String.format(
                  " G.-Pf. %.2f -> %.2f |", article.getContainerDeposit(), containerDeposit);
          persistedArticle.setContainerDeposit(containerDeposit);
        }
        em.merge(persistedArticle);
        logger.info(log);
      }
    } catch (Exception e) {
      Tools.showUnexpectedErrorWarning(e);
      et.rollback();
      logger.error("Rolled back changes due to an exception");
    }
  }
}
