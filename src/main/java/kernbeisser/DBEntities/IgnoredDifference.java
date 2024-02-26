package kernbeisser.DBEntities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import kernbeisser.Tasks.Catalog.Merge.ArticleDifference;
import kernbeisser.Tasks.Catalog.Merge.MappedDifference;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

@Table
@Entity
@NoArgsConstructor
@EqualsAndHashCode(exclude = {"id"})
public class IgnoredDifference {

  @Id
  @GeneratedValue(generator = "increment")
  @GenericGenerator(name = "increment", strategy = "increment")
  private long id;

  @ManyToOne
  @JoinColumn(nullable = false)
  @Getter
  private Article article;

  @Column
  @Enumerated(EnumType.STRING)
  @Getter
  private MappedDifference difference;

  @Column @Getter private String original;

  @Column @Getter private String ignoredChange;

  public static IgnoredDifference from(Article article, ArticleDifference<?> difference) {
    IgnoredDifference out = new IgnoredDifference();
    out.article = article;
    out.original = String.valueOf(difference.getPreviousVersion());
    out.difference = (MappedDifference) difference.getArticleDifference();
    out.ignoredChange = String.valueOf(difference.getNewVersion());
    return out;
  }
}
