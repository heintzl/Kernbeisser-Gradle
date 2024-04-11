package kernbeisser.Windows.Inventory.Counting;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import java.util.List;
import kernbeisser.DBConnection.DBConnection;
import kernbeisser.DBConnection.QueryBuilder;
import kernbeisser.DBEntities.Article;
import kernbeisser.DBEntities.ArticleStock;
import kernbeisser.DBEntities.Shelf;
import kernbeisser.DBEntities.Shelf_;
import kernbeisser.Windows.MVC.IModel;
import lombok.Cleanup;

public class CountingModel implements IModel<CountingController> {

  public List<Shelf> getAllShelves() {
    return QueryBuilder.selectAll(Shelf.class).orderBy(Shelf_.shelfNo.desc()).getResultList();
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
