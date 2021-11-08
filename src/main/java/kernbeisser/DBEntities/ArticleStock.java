package kernbeisser.DBEntities;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import javax.persistence.*;
import kernbeisser.Enums.Setting;
import kernbeisser.Useful.Tools;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Table
@Data
public class ArticleStock {

  public static final Instant expiringDate =
      Instant.now().minus(Setting.INVENTORY_COUNTING_EXPIRE_HOURS.getIntValue(), ChronoUnit.HOURS);

  @Id
  @GeneratedValue(generator = "increment")
  @GenericGenerator(name = "increment", strategy = "increment")
  private long id;

  @ManyToOne @JoinColumn private Article article;

  @Column private double counted;

  @CreationTimestamp private Instant createDate;

  public static Optional<ArticleStock> ofArticle(EntityManager em, Article article) {
    return Tools.optional(
        em.createQuery(
                "select a from ArticleStock a where a.article = :a order by createDate desc",
                ArticleStock.class)
            .setMaxResults(1)
            .setParameter("a", article));
  }

  public static ArticleStock newFromArticle(Article e) {
    ArticleStock stock = new ArticleStock();
    stock.setArticle(e);
    stock.setCounted(0.0);
    return stock;
  }

  public double calculateNetPrice() {
    return article.getNetPrice() * counted;
  }

  public boolean isExpired() {
    return !createDate.isAfter(expiringDate);
  }

  public boolean isNotExpired() {
    return !isExpired();
  }
}
