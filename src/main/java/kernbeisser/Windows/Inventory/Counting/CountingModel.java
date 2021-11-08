package kernbeisser.Windows.Inventory.Counting;

import java.util.Collection;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import kernbeisser.DBConnection.DBConnection;
import kernbeisser.DBEntities.Article;
import kernbeisser.DBEntities.ArticleStock;
import kernbeisser.DBEntities.Shelf;
import kernbeisser.Useful.Tools;
import kernbeisser.Windows.MVC.IModel;
import lombok.Cleanup;

public class CountingModel implements IModel<CountingController> {

  Collection<ArticleStock> getArticleStocks(Shelf shelf) {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    @Cleanup("commit")
    EntityTransaction et = em.getTransaction();
    et.begin();
    return shelf.getAllArticles(em).stream()
        .map(
            e ->
                ArticleStock.ofArticle(em, e)
                    .filter(ArticleStock::isNotExpired)
                    .orElse(ArticleStock.newFromArticle(e)))
        .collect(Collectors.toList());
  }

  public Collection<Shelf> getAllShelves() {
    return Tools.getAll(Shelf.class, null);
  }

  public void addArticleToShelf(Shelf shelf, Article article) {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    @Cleanup("commit")
    EntityTransaction et = em.getTransaction();
    et.begin();
    Shelf dbShelf = em.find(Shelf.class, shelf.getId());
    shelf.getArticles().add(article);
    dbShelf.getArticles().add(em.find(Article.class, article.getId()));
    em.persist(dbShelf);
  }

  public void setStock(ArticleStock stock, double value) {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    @Cleanup("commit")
    EntityTransaction et = em.getTransaction();
    et.begin();
    if (stock.getId() != 0) stock = em.find(ArticleStock.class, stock.getId());
    stock.setCounted(value);
    em.persist(stock);
  }
}
