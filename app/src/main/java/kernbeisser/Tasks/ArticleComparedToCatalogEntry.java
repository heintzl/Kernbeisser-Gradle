package kernbeisser.Tasks;

import java.util.*;
import kernbeisser.DBEntities.Article;
import kernbeisser.DBEntities.Articles;
import kernbeisser.DBEntities.CatalogEntry;
import kernbeisser.Enums.ArticleCatalogState;
import kernbeisser.Useful.Tools;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

public class ArticleComparedToCatalogEntry {

  @Getter private final CatalogEntry catalogEntry;
  @Getter private final Article article;
  @Getter private final Set<String> fieldDifferences = new HashSet<>();
  @Getter private ArticleCatalogState resultType;
  @Getter private Article conflictingArticle;
  @Getter @Setter private String description;

  public static final ArticleComparedToCatalogEntry NO_CATALOG_ENTRY =
      new ArticleComparedToCatalogEntry(ArticleCatalogState.NO_CATALOG_ENTRY, "");

  public ArticleComparedToCatalogEntry(
      @NotNull Article article, @NotNull CatalogEntry catalogEntry) {
    this.catalogEntry = catalogEntry;
    this.article = article;
    analyzeDifferences();
  }

  private ArticleComparedToCatalogEntry(ArticleCatalogState resultType, String description) {
    catalogEntry = null;
    article = null;
    this.resultType = resultType;
    this.description = description;
  }

  private void analyzeOtherDifferences() {
    if (article.getSingleDeposit() != catalogEntry.getEinzelPfand())
      fieldDifferences.add("Einzel-Pfand");
    if (article.getContainerDeposit() != catalogEntry.getGebindePfand())
      fieldDifferences.add("Gebinde-Pfand");
    resultType =
        fieldDifferences.isEmpty() ? ArticleCatalogState.EQUAL : ArticleCatalogState.DIFFERENT;
  }

  private void analyzeDifferences() {

    Long bc_article = Tools.ifNull(article.getBarcode(), -999L);
    Long bc_catalog = Tools.ifNull(catalogEntry.getEanLadenEinheit(), -999L);
    if (!Articles.validateBarcode(bc_catalog) || bc_catalog.equals(bc_article)) {
      analyzeOtherDifferences();
      return;
    }
    fieldDifferences.add("Barcode");
    Optional<Article> conflictingArticle = Articles.getByBarcode(bc_catalog);
    if (conflictingArticle.isPresent()) {
      this.conflictingArticle = conflictingArticle.get();
      if (article.getSupplier().equals(this.conflictingArticle.getSupplier())) {
        resultType = ArticleCatalogState.BARCODE_CONFLICT_SAME_SUPPLIER;
      } else {
        resultType = ArticleCatalogState.BARCODE_CONFLICT_OTHER_SUPPLIER;
      }
      return;
    }
    if (!Articles.validateBarcode(bc_article)) {
      analyzeOtherDifferences();
      return;
    }
    resultType = ArticleCatalogState.BARCODE_CHANGED;
  }
}
