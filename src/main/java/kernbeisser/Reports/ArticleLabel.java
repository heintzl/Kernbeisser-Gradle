package kernbeisser.Reports;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import kernbeisser.DBEntities.Article;
import kernbeisser.DBEntities.ShoppingItem;

public class ArticleLabel extends Report {

  private final List<Article> articles;

  public ArticleLabel(List<Article> articles) {
    super("articleLabel", "Etiketten");
    this.articles = articles;
  }

  @Override
  Map<String, Object> getReportParams() {
    return null;
  }

  @Override
  Collection<?> getDetailCollection() {
    return articles.stream().map(ShoppingItem::createReportItem).collect(Collectors.toList());
  }
}
