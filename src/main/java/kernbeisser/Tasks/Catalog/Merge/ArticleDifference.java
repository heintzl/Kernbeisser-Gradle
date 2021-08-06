package kernbeisser.Tasks.Catalog.Merge;

import kernbeisser.DBEntities.Article;
import lombok.Getter;

public class ArticleDifference<T> {
  public ArticleDifference(Difference<Article, T> articleDifference, T old, Article article) {
    this.articleDifference = articleDifference;
    this.previousVersion = old;
    this.article = article;
  }

  @Getter private final Difference<Article, T> articleDifference;
  private final T previousVersion;
  @Getter private final Article article;

  public void pushCurrentIntoNew() {
    articleDifference.set(article, previousVersion);
  }

  public double distance() {
    return articleDifference.distance(articleDifference.get(article), previousVersion);
  }

  public T getPreviousVersion() {
    return previousVersion;
  }

  public T getNewVersion() {
    return articleDifference.get(article);
  }
}
