package kernbeisser.DBEntities;

import java.io.Serializable;
import java.time.Instant;
import javax.persistence.*;
import kernbeisser.Enums.PermissionKey;
import kernbeisser.Enums.Repeat;
import kernbeisser.Security.Key;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table
@EqualsAndHashCode(doNotUseGetters = true)
public class Offer implements Serializable {
  @Id
  @GeneratedValue
  @Getter(onMethod_ = {@Key(PermissionKey.OFFER_ID_READ)})
  @Setter(onMethod_ = {@Key(PermissionKey.OFFER_ID_WRITE)})
  private int id;

  @Column
  @Getter(onMethod_ = {@Key(PermissionKey.OFFER_SPECIAL_NET_PRICE_READ)})
  @Setter(onMethod_ = {@Key(PermissionKey.OFFER_SPECIAL_NET_PRICE_WRITE)})
  private double specialNetPrice;

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

  @Column
  // dangerous SQL keyword 'repeat' do not rename to 'repeat'
  @Getter(onMethod_ = {@Key(PermissionKey.OFFER_REPEAT_MODE_READ)})
  @Setter(onMethod_ = {@Key(PermissionKey.OFFER_REPEAT_MODE_WRITE)})
  private Repeat repeatMode;

  @ManyToOne
  @JoinColumn(nullable = false)
  @Getter(onMethod_ = {@Key(PermissionKey.OFFER_ID_READ)})
  @Setter(onMethod_ = {@Key(PermissionKey.OFFER_ID_WRITE)})
  private ArticleBase article;
}
