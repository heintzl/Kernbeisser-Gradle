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
import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EditArticlesModel implements IModel<EditArticlesController> {

  private static final Supplier kkSupplier = Supplier.getKKSupplier();
  private final Logger logger = LogManager.getLogger(EditArticlesModel.class);
  @Getter private List<ArticleComparedToCatalogEntry> differences;

  private Map<String, CatalogEntry> getCatalogMap(Collection<Article> articles) {

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

  public void previewCatalog(Collection<Article> articles) {
    differences = new ArrayList<>();
    Map<String, CatalogEntry> catalogMap = getCatalogMap(articles);
    for (Article article : articles) {
      if (!article.getSupplier().equals(kkSupplier)) {
        continue;
      }
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
  }

  public void mergeCatalog(Collection<Article> articles) {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    EntityTransaction et = em.getTransaction();
    et.begin();
    try {
      for (Article article : articles) {
        String log = "";
        Optional<ArticleComparedToCatalogEntry> optCatalogEntry =
            differences.stream().filter(a -> a.getArticle().equals(article)).findFirst();
        if (!optCatalogEntry.isPresent()) {
          continue;
        }
        CatalogEntry catalogEntry = optCatalogEntry.get().getCatalogEntry();
        log += "Artikel " + article.getSuppliersItemNumber() + ":";
        long barcode = Tools.ifNull(catalogEntry.getEanLadenEinheit(), 0L);
        double singleDeposit = catalogEntry.getEinzelPfand();
        double containerDeposit = catalogEntry.getGebindePfand();
        boolean changed = false;
        Article persistedArticle = em.find(Article.class, article.getId());
        if (barcode > 1E10) {
          Optional<Article> sameBarcode = Articles.getByBarcode(barcode);
          if (sameBarcode.isPresent()) {
            if (!sameBarcode.get().equals(article)) {
              logger.warn(
                  log
                      + String.format(
                          " hat den selben Barcode, wie der Artikel mit der KB-Artikelnummer %d und wird daher übersprungen",
                          sameBarcode.get().getKbNumber()));
            }
          } else {
            log += String.format(" bc %s -> %s |", article.getBarcode(), barcode);
            persistedArticle.setBarcode(barcode);
            changed = true;
          }
        }
        if (singleDeposit > 0.0) {
          log += String.format(" E.-Pf. %.2f -> %.2f |", article.getSingleDeposit(), singleDeposit);
          persistedArticle.setSingleDeposit(singleDeposit);
          changed = true;
        }
        if (containerDeposit > 0.0) {
          log +=
              String.format(
                  " G.-Pf. %.2f -> %.2f |", article.getContainerDeposit(), containerDeposit);
          persistedArticle.setContainerDeposit(containerDeposit);
          changed = true;
        }
        if (changed) {
          em.merge(persistedArticle);
        }
        logger.info(log);
      }
      em.flush();
      et.commit();
    } catch (Exception e) {
      Tools.showUnexpectedErrorWarning(e);
      et.rollback();
      logger.error("Rolled back changes due to an exception");
    }
  }
}
