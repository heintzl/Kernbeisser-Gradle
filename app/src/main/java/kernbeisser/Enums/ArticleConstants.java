package kernbeisser.Enums;

import java.util.Arrays;
import java.util.Collection;
import kernbeisser.DBEntities.Article;
import lombok.Getter;

public enum ArticleConstants {
  PRODUCE(-1),
  BAKERY(-2),
  DEPOSIT(-3),
  SOLIDARITY(-4),
  CUSTOM_PRODUCT(-5);

  private static final Collection<Integer> constantArticleIdentifiers =
      Arrays.stream(ArticleConstants.values()).map(ArticleConstants::getUniqueIdentifier).toList();
  @Getter private final int uniqueIdentifier;

  ArticleConstants(int uniqueIdentifier) {
    this.uniqueIdentifier = uniqueIdentifier;
  }

  public static boolean isConstantArticle(Article article) {
    return constantArticleIdentifiers.contains(article.getKbNumber());
  }
}
