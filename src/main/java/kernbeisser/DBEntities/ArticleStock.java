package kernbeisser.DBEntities;

import java.time.Instant;
import javax.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Table
public class ArticleStock {
  @Id
  @GeneratedValue(generator = "increment")
  @GenericGenerator(name = "increment", strategy = "increment")
  private long id;

  @ManyToOne @JoinColumn private Article article;

  @Column private double counted;

  @CreationTimestamp private Instant createDate;
}
