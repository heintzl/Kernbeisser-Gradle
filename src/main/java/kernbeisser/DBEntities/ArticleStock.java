package kernbeisser.DBEntities;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Optional;
import javax.persistence.*;
import kernbeisser.DBConnection.DBConnection;
import kernbeisser.Enums.Setting;
import kernbeisser.Useful.Tools;
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
    return Tools.optional(
        em.createQuery(
                "select a from ArticleStock a where a.article = :a and a.shelf = :s and a.inventoryDate = :d",
                ArticleStock.class)
            .setMaxResults(1)
            .setParameter("a", article)
            .setParameter("s", shelf)
            .setParameter("d", Setting.INVENTORY_SCHEDULED_DATE.getDateValue()));
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

  public double calculateNetPrice() {
    return article.getNetPrice() * counted;
  }
}
