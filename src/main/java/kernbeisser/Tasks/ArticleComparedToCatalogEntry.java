package kernbeisser.Tasks;

import com.google.common.collect.ImmutableMap;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiPredicate;
import kernbeisser.DBEntities.Article;
import kernbeisser.DBEntities.CatalogEntry;
import kernbeisser.Useful.Tools;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

public class ArticleComparedToCatalogEntry {

  @Getter @NotNull private final CatalogEntry catalogEntry;
  @Getter @NotNull private final Article article;
  @Getter private final List<String> fieldDifferences;
  @Getter @Setter private String description;
  private static final ImmutableMap<String, BiPredicate<Article, CatalogEntry>> comparators =
      buildComparators();

  public ArticleComparedToCatalogEntry(Article article, CatalogEntry catalogEntry) {
    this.catalogEntry = catalogEntry;
    this.article = article;
    this.fieldDifferences = analyzeDifferences();
  }

  private static ImmutableMap<String, BiPredicate<Article, CatalogEntry>> buildComparators() {
    return ImmutableMap.<String, BiPredicate<Article, CatalogEntry>>builder()
        .put(
            "Barcode",
            (a, c) ->
                Tools.ifNull(a.getBarcode(), -999L)
                    .equals(Tools.ifNull(c.getEanLadenEinheit(), -999L)))
        .put("Einzel-Pfand", (a, c) -> a.getSingleDeposit() == (c.getEinzelPfand()))
        .put("Gebinde-Pfand", (a, c) -> a.getContainerDeposit() == (c.getGebindePfand()))
        .build();
  }

  private List<String> analyzeDifferences() {
    List<String> result = new ArrayList<>();
    comparators.forEach(
        (name, comparator) -> {
          if (!comparator.test(article, catalogEntry)) {
            result.add(name);
          }
        });
    return result;
  }
}
