package kernbeisser.Windows.SynchronizeArticles;

import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Function;
import javax.persistence.EntityManager;
import kernbeisser.DBEntities.Article;
import kernbeisser.DBEntities.ArticleBase;
import kernbeisser.DBEntities.ArticleKornkraft;
import lombok.Data;

@Data
public class ArticleDifference<T> {
  private final ArticleBase kernbeisser, catalog;
  private final Function<ArticleBase, T> getValue;
  private final BiConsumer<ArticleBase, T> setValue;
  private final DifferenceType differenceType;

  public ArticleDifference(
      ArticleBase kernbeisser,
      ArticleBase catalog,
      Function<ArticleBase, T> getValue,
      BiConsumer<ArticleBase, T> setValue,
      DifferenceType differenceType) {
    this.getValue = getValue;
    this.kernbeisser = kernbeisser;
    this.catalog = catalog;
    this.setValue = setValue;
    this.differenceType = differenceType;
  }

  void applyKernbeisser(EntityManager em) {
    ArticleKornkraft articleKornkraft = em.find(ArticleKornkraft.class, catalog.getId());
    articleKornkraft.setSynchronised(true);
    em.persist(articleKornkraft);
  }

  void applyCatalog(EntityManager em) {
    Article article = em.find(Article.class, kernbeisser.getId());
    setValue.accept(article, getValue.apply(catalog));
    em.persist(article);
  }

  public ArticleBase getKernbeisserArticle() {
    return kernbeisser;
  }

  public ArticleBase getCatalogArticle() {
    return catalog;
  }

  public T getKernbeisserVersion() {
    return getValue.apply(kernbeisser);
  }

  public T getCatalogVersion() {
    return getValue.apply(catalog);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ArticleDifference<?> that = (ArticleDifference<?>) o;
    return Objects.equals(kernbeisser, that.kernbeisser)
        && Objects.equals(catalog, that.catalog)
        && Objects.equals(getValue, that.getValue)
        && Objects.equals(setValue, that.setValue)
        && Objects.equals(differenceType, that.differenceType);
  }

  @Override
  public int hashCode() {
    return Objects.hash(kernbeisser, catalog, getValue, setValue, differenceType);
  }
}
