package kernbeisser.DBEntities;

import jakarta.persistence.*;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Optional;
import kernbeisser.DBConnection.DBConnection;
import kernbeisser.DBConnection.QueryBuilder;
import kernbeisser.DBEntities.Repositories.ArticleRepository;
import kernbeisser.DBEntities.ArticleStock_;
import kernbeisser.Enums.Setting;
import lombok.Cleanup;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Table
@Data
public class ArticleStock {

  @Id
  @GeneratedValue(generator = "increment")
  @GenericGenerator(name = "increment", strategy = "increment")
  private long id;

  @ManyToOne @JoinColumn private Article article;
  @ManyToOne @JoinColumn private Shelf shelf;

  @Column private double counted;

  @Column private LocalDate inventoryDate;

  @CreationTimestamp private Instant createDate;

  public static Optional<ArticleStock> ofArticle(EntityManager em, Article article, Shelf shelf) {
    return QueryBuilder.selectAll(ArticleStock.class)
        .where(
            ArticleStock_.article.eq(article),
            ArticleStock_.shelf.eq(shelf),
            ArticleStock_.inventoryDate.eq(Setting.INVENTORY_SCHEDULED_DATE.getDateValue()))
        .getSingleResultOptional(em);
  }

  public static ArticleStock newFromArticle(Article e, Shelf shelf) {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    @Cleanup("commit")
    EntityTransaction et = em.getTransaction();
    et.begin();
    ArticleStock stock = new ArticleStock();
    stock.setArticle(e);
    stock.setCounted(0.0);
    stock.setShelf(shelf);
    stock.setInventoryDate(Setting.INVENTORY_SCHEDULED_DATE.getDateValue());
    return stock;
  }

  public Article getArticleAtInventoryDate() {
    Instant date =
        Setting.INVENTORY_SCHEDULED_DATE
            .getDateValue()
            .atStartOfDay()
            .atZone(ZoneId.systemDefault())
            .toInstant();
    Article articleAtDate = ArticleRepository.getArticleStateAtDate(article, date);
    if (articleAtDate == null) {
      return article;
    }
    return articleAtDate;
  }
}
