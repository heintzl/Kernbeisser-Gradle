package kernbeisser.DBEntities;

import java.io.Serializable;
import java.time.Instant;
import javax.persistence.*;
import kernbeisser.Enums.PermissionKey;
import kernbeisser.Security.Key;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(
    indexes = {
      @Index(name = "IX_offer_fromDate", columnList = "fromDate"),
      @Index(name = "IX_offer_toDate", columnList = "toDate")
    })
@EqualsAndHashCode(doNotUseGetters = true)
public class Offer implements Serializable {
  @Id
  @GeneratedValue
  @Getter(onMethod_ = {@Key(PermissionKey.OFFER_ID_READ)})
  @Setter(onMethod_ = {@Key(PermissionKey.OFFER_ID_WRITE)})
  private int id;

  @Column()
  // dangerous SQL keyword 'from' do not rename to 'from'
  @Getter(onMethod_ = {@Key(PermissionKey.OFFER_FROM_DATE_READ)})
  @Setter(onMethod_ = {@Key(PermissionKey.OFFER_FROM_DATE_WRITE)})
  private Instant fromDate;

  @Column()
  // dangerous SQL keyword 'to' do not rename to 'to'
  @Getter(onMethod_ = {@Key(PermissionKey.OFFER_TO_DATE_READ)})
  @Setter(onMethod_ = {@Key(PermissionKey.OFFER_TO_DATE_WRITE)})
  private Instant toDate;

  @ManyToOne
  @JoinColumn(nullable = false)
  @Getter(onMethod_ = {@Key(PermissionKey.OFFER_OFFER_ARTICLE_READ)})
  @Setter(onMethod_ = {@Key(PermissionKey.OFFER_OFFER_ARTICLE_WRITE)})
  private Article offerArticle;

  @ManyToOne
  @JoinColumn(nullable = false)
  @Getter(onMethod_ = {@Key(PermissionKey.OFFER_PARENT_ARTICLE_READ)})
  @Setter(onMethod_ = {@Key(PermissionKey.OFFER_PARENT_ARTICLE_WRITE)})
  private Article parentArticle;

  @CreationTimestamp @Column @Getter private Instant createDate;
}
