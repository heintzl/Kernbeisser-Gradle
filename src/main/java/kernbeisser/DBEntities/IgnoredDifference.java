package kernbeisser.DBEntities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import kernbeisser.Windows.SynchronizeArticles.ArticleDifference;
import kernbeisser.Windows.SynchronizeArticles.MappedDifferences;
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
  private MappedDifferences difference;

  @Column @Getter private String original;

  @Column @Getter private String ignoredChange;

  public static IgnoredDifference from(ArticleDifference<?> difference) {
    IgnoredDifference out = new IgnoredDifference();
    out.article = difference.getCurrent();
    out.original = String.valueOf(difference.getCurrentVersion());
    out.difference = (MappedDifferences) difference.getArticleDifference();
    out.ignoredChange = String.valueOf(difference.getNewVersion());
    return out;
  }
}
