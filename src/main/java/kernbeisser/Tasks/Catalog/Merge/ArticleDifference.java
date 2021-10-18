package kernbeisser.Tasks.Catalog.Merge;

import kernbeisser.DBEntities.Article;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
public class ArticleDifference<T> {
  @Getter private final Difference<Article, T> articleDifference;
  private final T previousVersion;
  private final T newVersion;
  @Setter @Getter @NonNull private Solution solution = Solution.NO_SOLUTION;

  public double distance() {
    return articleDifference.distance(previousVersion, newVersion);
  }

  public T getPreviousVersion() {
    return previousVersion;
  }

  public T getNewVersion() {
    return newVersion;
  }
}
