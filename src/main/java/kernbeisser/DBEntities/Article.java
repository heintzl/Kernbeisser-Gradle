package kernbeisser.DBEntities;

import java.time.Instant;
import javax.persistence.*;
import kernbeisser.Enums.MetricUnits;
import kernbeisser.Enums.PermissionKey;
import kernbeisser.Enums.ShopRange;
import kernbeisser.Enums.VAT;
import kernbeisser.Security.Key;
import kernbeisser.Useful.Tools;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.envers.Audited;

/*
 extends from the main article structure ArticleBase which extends Article and ArticleKornkraft
 the Article class contains additional statistic fields which aren't required for the all Articles
 and only used for Articles which are constantly in use of Kernbeisser
*/
@Entity
@Table(
    uniqueConstraints = {
      @UniqueConstraint(columnNames = {"supplier_id", "suppliersItemNumber", "offer"}),
      @UniqueConstraint(columnNames = {"kbNumber", "offer"}),
      @UniqueConstraint(columnNames = {"barcode", "offer"}),
    },
    indexes = {
      @Index(name = "IX_article_name", columnList = "name"),
      @Index(name = "IX_article_kbNumber", columnList = "kbNumber"),
      @Index(name = "IX_article_barcode", columnList = "barcode")
    })
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(doNotUseGetters = true)
@Audited
public final class Article {

  @Id
  @GeneratedValue(generator = "increment")
  @GenericGenerator(name = "increment", strategy = "increment")
  @Getter(onMethod_ = {@Key(PermissionKey.ARTICLE_ID_READ)})
  @Setter(onMethod_ = {@Key(PermissionKey.ARTICLE_ID_WRITE)})
  private int id;

  /*
  the Kernbeisser number is a unique index for use in the shop.
  It is a way to identify Articles and is sorted in priceLists
  and categories.
  */
  @Column(unique = true)
  @Getter(onMethod_ = {@Key(PermissionKey.ARTICLE_KB_NUMBER_READ)})
  @Setter(onMethod_ = {@Key(PermissionKey.ARTICLE_KB_NUMBER_WRITE)})
  private int kbNumber;

  /*
  describes the kind of the article and group them
   */
  @ManyToOne
  @JoinColumn
  @Getter(onMethod_ = {@Key(PermissionKey.ARTICLE_PRICE_LIST_READ)})
  @Setter(onMethod_ = {@Key(PermissionKey.ARTICLE_PRICE_LIST_WRITE)})
  private PriceList priceList;

  @Column
  @Getter(onMethod_ = {@Key(PermissionKey.ARTICLE_WEIGH_ABLE_READ)})
  @Setter(onMethod_ = {@Key(PermissionKey.ARTICLE_WEIGH_ABLE_WRITE)})
  // maybe rename to splittable, because it describes if a article should not always become sold as
  // one entire piece
  private boolean weighable;

  @Column
  @Getter(onMethod_ = {@Key(PermissionKey.ARTICLE_SHOW_IN_SHOP_READ)})
  @Setter(onMethod_ = {@Key(PermissionKey.ARTICLE_SHOW_IN_SHOP_WRITE)})
  private boolean showInShop;

  @Column
  @Getter(onMethod_ = {@Key(PermissionKey.ARTICLE_ACTIVE_READ)})
  @Setter(onMethod_ = {@Key(PermissionKey.ARTICLE_ACTIVE_WRITE)})
  private boolean active;

  @Column
  @Getter(onMethod_ = {@Key(PermissionKey.ARTICLE_DELETED_DATE_READ)})
  @Setter(onMethod_ = {@Key(PermissionKey.ARTICLE_DELETED_DATE_WRITE)})
  private Instant activeStateChange;

  @Column
  @Getter(onMethod_ = {@Key(PermissionKey.ARTICLE_VERIFIED_READ)})
  @Setter(onMethod_ = {@Key(PermissionKey.ARTICLE_VERIFIED_WRITE)})
  private boolean verified;

  @Column
  @Getter(onMethod_ = {@Key(PermissionKey.ARTICLE_NAME_READ)})
  @Setter(onMethod_ = {@Key(PermissionKey.ARTICLE_NAME_WRITE)})
  private String name;

  @Column
  @Getter(onMethod_ = {@Key(PermissionKey.ARTICLE_PRODUCER_READ)})
  @Setter(onMethod_ = {@Key(PermissionKey.ARTICLE_PRODUCER_WRITE)})
  private String producer;

  @Column
  @Getter(onMethod_ = {@Key(PermissionKey.ARTICLE_NET_PRICE_READ)})
  @Setter(onMethod_ = {@Key(PermissionKey.ARTICLE_NET_PRICE_WRITE)})
  private double netPrice;

  @Column
  @Getter(onMethod_ = {@Key(PermissionKey.ARTICLE_METRIC_UNITS_READ)})
  @Setter(onMethod_ = {@Key(PermissionKey.ARTICLE_METRIC_UNITS_WRITE)})
  private MetricUnits metricUnits;

  @JoinColumn
  @ManyToOne
  @Getter(onMethod_ = {@Key(PermissionKey.ARTICLE_SUPPLIER_READ)})
  @Setter(onMethod_ = {@Key(PermissionKey.ARTICLE_SUPPLIER_WRITE)})
  private Supplier supplier;

  @Column
  @Getter(onMethod_ = {@Key(PermissionKey.ARTICLE_SUPPLIERS_ITEM_NUMBER_READ)})
  @Setter(onMethod_ = {@Key(PermissionKey.ARTICLE_SUPPLIERS_ITEM_NUMBER_WRITE)})
  private int suppliersItemNumber;

  @Column(nullable = false)
  @Getter(onMethod_ = {@Key(PermissionKey.ARTICLE_VAT_READ)})
  @Setter(onMethod_ = {@Key(PermissionKey.ARTICLE_VAT_WRITE)})
  private VAT vat;

  @Column
  @Getter(onMethod_ = {@Key(PermissionKey.ARTICLE_AMOUNT_READ)})
  @Setter(onMethod_ = {@Key(PermissionKey.ARTICLE_AMOUNT_WRITE)})
  private int amount;

  @Column
  @Getter(onMethod_ = {@Key(PermissionKey.ARTICLE_BARCODE_READ)})
  @Setter(onMethod_ = {@Key(PermissionKey.ARTICLE_BARCODE_WRITE)})
  private Long barcode;

  @Column
  @Getter(onMethod_ = {@Key(PermissionKey.ARTICLE_CONTAINER_SIZE_READ)})
  @Setter(onMethod_ = {@Key(PermissionKey.ARTICLE_CONTAINER_SIZE_WRITE)})
  private double containerSize;

  @Column
  @Getter(onMethod_ = {@Key(PermissionKey.ARTICLE_SINGLE_DEPOSIT_READ)})
  @Setter(onMethod_ = {@Key(PermissionKey.ARTICLE_SINGLE_DEPOSIT_WRITE)})
  private double singleDeposit;

  @Column
  @Getter(onMethod_ = {@Key(PermissionKey.ARTICLE_CONTAINER_DEPOSIT_READ)})
  @Setter(onMethod_ = {@Key(PermissionKey.ARTICLE_CONTAINER_DEPOSIT_WRITE)})
  private double containerDeposit;

  @Column
  @Getter(onMethod_ = {@Key(PermissionKey.ARTICLE_INFO_READ)})
  @Setter(onMethod_ = {@Key(PermissionKey.ARTICLE_INFO_WRITE)})
  private String info;

  @Column @UpdateTimestamp @Getter private Instant updateDate;

  @JoinColumn(nullable = false)
  @ManyToOne
  @Getter(onMethod_ = {@Key(PermissionKey.SURCHARGE_TABLE_SUPPLIER_READ)})
  @Setter(onMethod_ = {@Key(PermissionKey.SURCHARGE_TABLE_SUPPLIER_WRITE)})
  private SurchargeGroup surchargeGroup;

  @Column(nullable = false)
  @Getter(onMethod_ = {@Key(PermissionKey.ARTICLE_SHOP_RANGE_READ)})
  @Setter(onMethod_ = {@Key(PermissionKey.ARTICLE_SHOP_RANGE_WRITE)})
  private ShopRange shopRange = ShopRange.NOT_IN_RANGE;

  @Column
  @Getter(onMethod_ = {@Key(PermissionKey.ARTICLE_OFFER_READ)})
  @Setter(onMethod_ = {@Key(PermissionKey.ARTICLE_OFFER_WRITE)})
  private boolean offer;

  @Getter @Setter private Double obsoleteSurcharge;

  @Override
  public String toString() {
    return Tools.optional(this::getName).orElse("ArtikelBase[" + super.toString() + "]");
  }
}
