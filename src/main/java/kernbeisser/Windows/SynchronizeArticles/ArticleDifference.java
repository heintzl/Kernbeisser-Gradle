package kernbeisser.Windows.SynchronizeArticles;

import kernbeisser.DBEntities.Article;
import lombok.Getter;

public class ArticleDifference<T> {
  public ArticleDifference(
      Difference<Article, T> articleDifference, Article current, Article newVersion) {
    this.articleDifference = articleDifference;
    this.current = current;
    this.newVersion = newVersion;
  }

  @Getter private final Difference<Article, T> articleDifference;
  @Getter private final Article current;
  private final Article newVersion;

  public void pushCurrentIntoNew() {
    articleDifference.transfer(current, newVersion);
  }

  public double distance() {
    return articleDifference.distance(current, newVersion);
  }

  public T getCurrentVersion() {
    return articleDifference.get(current);
  }

  public T getNewVersion() {
    return articleDifference.get(newVersion);
  }
}
