package kernbeisser.Tasks.Catalog.Merge;

import java.util.Collection;
import java.util.Optional;
import kernbeisser.DBEntities.Article;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.Value;
import lombok.experimental.NonFinal;
import org.jetbrains.annotations.NotNull;

@RequiredArgsConstructor
@Value
public class ArticleMerge {
  Article revision;
  Article newState;
  MergeStatus mergeStatus;
  Collection<ArticleDifference<?>> articleDifferences;
  @Setter @NonFinal boolean resolved;

  public void mergeProperty(MappedDifference difference, Solution solution) {
    articleDifferences.stream()
        .filter(e -> e.getArticleDifference().equals(difference))
        .filter(e -> e.getSolution() == Solution.NO_SOLUTION)
        .forEach(e -> e.setSolution(solution));
    if (articleDifferences.size() == 0
        || articleDifferences.stream()
            .map(ArticleDifference::getSolution)
            .noneMatch(e -> e.equals(Solution.NO_SOLUTION))) resolved = true;
  }

  ArticleMerge resolved() {
    resolved = true;
    return this;
  }

  public static @NotNull ArticleMerge updateMerge(
      @NotNull Article revision,
      @NotNull Article newState,
      @NotNull Collection<ArticleDifference<?>> articleDifferences) {
    ArticleMerge merge =
        new ArticleMerge(
            revision,
            newState,
            articleDifferences.size() == 0 ? MergeStatus.NO_CONFLICTS : MergeStatus.CONFLICT,
            articleDifferences);
    if (merge.mergeStatus == MergeStatus.NO_CONFLICTS) return merge.resolved();
    else return merge;
  }

  public Optional<ArticleDifference<?>> getDifference(MappedDifference mappedDifference) {
    return articleDifferences.stream()
        .filter(e -> e.getArticleDifference().equals(mappedDifference))
        .findFirst();
  }
}
