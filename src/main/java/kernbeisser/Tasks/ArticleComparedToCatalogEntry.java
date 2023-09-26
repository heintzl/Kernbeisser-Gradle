package kernbeisser.Tasks;

import java.util.*;
import kernbeisser.DBEntities.Article;
import kernbeisser.DBEntities.Articles;
import kernbeisser.DBEntities.CatalogEntry;
import kernbeisser.Useful.Tools;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

public class ArticleComparedToCatalogEntry {

  @Getter @NotNull private final CatalogEntry catalogEntry;
  @Getter @NotNull private final Article article;
  @Getter private final Set<String> fieldDifferences = new HashSet<>();
  @Getter private int resultType;
  @Getter private Article conflictingArticle;
  @Getter @Setter private String description;
  public static final int EQUAL = 0;
  public static final int DIFFERENT = 1;
  public static final int BARCODE_CHANGED = 2;
  public static final int BARCODE_CONFLICT_SAME_SUPPLIER = 3;
  public static final int BARCODE_CONFLICT_OTHER_SUPPLIER = 4;

  public ArticleComparedToCatalogEntry(
      @NotNull Article article, @NotNull CatalogEntry catalogEntry) {
    this.catalogEntry = catalogEntry;
    this.article = article;
    analyzeDifferences();
  }

  private void analyzeOtherDifferences() {
    if (article.getSingleDeposit() != catalogEntry.getEinzelPfand())
      fieldDifferences.add("Einzel-Pfand");
    if (article.getContainerDeposit() != catalogEntry.getGebindePfand())
      fieldDifferences.add("Gebinde-Pfand");
    resultType = fieldDifferences.isEmpty() ? EQUAL : DIFFERENT;
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
        resultType = BARCODE_CONFLICT_SAME_SUPPLIER;
      } else {
        resultType = BARCODE_CONFLICT_OTHER_SUPPLIER;
      }
      return;
    }
    if (!Articles.validateBarcode(bc_article)) {
      analyzeOtherDifferences();
      return;
    }
    resultType = BARCODE_CHANGED;
  }
}
