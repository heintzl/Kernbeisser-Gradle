package kernbeisser.DBEntities;

import jakarta.persistence.*;
import java.time.Instant;
import kernbeisser.Enums.MetricUnits;
import kernbeisser.Enums.ShopRange;
import kernbeisser.Enums.VAT;
import kernbeisser.Security.Key;
import kernbeisser.Useful.ActuallyCloneable;
import kernbeisser.Useful.Tools;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.envers.Audited;
import rs.groump.PermissionKey;

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
@EqualsAndHashCode(doNotUseGetters = true)
@Audited
public final class Article implements ActuallyCloneable {

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

  // Hersteller
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

  // Lieferant
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

  // Packungsgröße
  @Column
  @Getter(onMethod_ = {@Key(PermissionKey.ARTICLE_AMOUNT_READ)})
  @Setter(onMethod_ = {@Key(PermissionKey.ARTICLE_AMOUNT_WRITE)})
  private int amount;

  @Column
  @Getter(onMethod_ = {@Key(PermissionKey.ARTICLE_BARCODE_READ)})
  @Setter(onMethod_ = {@Key(PermissionKey.ARTICLE_BARCODE_WRITE)})
  private Long barcode;

  // Gebindegröße
  @Column
  @Getter(onMethod_ = {@Key(PermissionKey.ARTICLE_CONTAINER_SIZE_READ)})
  @Setter(onMethod_ = {@Key(PermissionKey.ARTICLE_CONTAINER_SIZE_WRITE)})
  private double containerSize;

  // Einzelpfand
  @Column
  @Getter(onMethod_ = {@Key(PermissionKey.ARTICLE_SINGLE_DEPOSIT_READ)})
  @Setter(onMethod_ = {@Key(PermissionKey.ARTICLE_SINGLE_DEPOSIT_WRITE)})
  private double singleDeposit;

  // Gebindepfand
  @Column
  @Getter(onMethod_ = {@Key(PermissionKey.ARTICLE_CONTAINER_DEPOSIT_READ)})
  @Setter(onMethod_ = {@Key(PermissionKey.ARTICLE_CONTAINER_DEPOSIT_WRITE)})
  private double containerDeposit;

  // zusätzliche Bezeichnungen aus Katalog
  @Column
  @Getter(onMethod_ = {@Key(PermissionKey.ARTICLE_INFO_READ)})
  @Setter(onMethod_ = {@Key(PermissionKey.ARTICLE_INFO_WRITE)})
  private String info;

  @Column @UpdateTimestamp @Getter private Instant updateDate;

  @JoinColumn(nullable = false)
  @ManyToOne
  @Getter(onMethod_ = {@Key(PermissionKey.ARTICLE_SURCHARGE_READ)})
  @Setter(onMethod_ = {@Key(PermissionKey.ARTICLE_SURCHARGE_WRITE)})
  private SurchargeGroup surchargeGroup;

  @Column(nullable = false)
  @Getter(onMethod_ = {@Key(PermissionKey.ARTICLE_SHOP_RANGE_READ)})
  @Setter(onMethod_ = {@Key(PermissionKey.ARTICLE_SHOP_RANGE_WRITE)})
  private ShopRange shopRange = ShopRange.NOT_IN_RANGE;

  @Column
  @Getter(onMethod_ = {@Key(PermissionKey.ARTICLE_OFFER_READ)})
  @Setter(onMethod_ = {@Key(PermissionKey.ARTICLE_OFFER_WRITE)})
  private boolean offer;

  // Faktor für evtl. Umrechnungen, um Katalog-Einkaufspreise auf veränderte Ladeneinheiten
  // abzubilden
  @Column
  @Getter(onMethod_ = {@Key(PermissionKey.ARTICLE_CATALOGPRICEFACTOR_READ)})
  @Setter(onMethod_ = {@Key(PermissionKey.ARTICLE_CATALOGPRICEFACTOR_WRITE)})
  private double catalogPriceFactor;

  // Standard-Etikettenanzahl pro Lieferung
  @Column
  @Getter(onMethod_ = {@Key(PermissionKey.ARTICLE_LABELCOUNT_READ)})
  @Setter(onMethod_ = {@Key(PermissionKey.ARTICLE_LABELCOUNT_WRITE)})
  private int labelCount;

  // Etikettenanzahl gilt pro Liefereinheit
  @Column
  @Getter(onMethod_ = {@Key(PermissionKey.ARTICLE_LABELPERUNIT_READ)})
  @Setter(onMethod_ = {@Key(PermissionKey.ARTICLE_LABELPERUNIT_WRITE)})
  private boolean labelPerUnit;

  // Ladenaufschlag aus migrierten Daten - ersetzt durch surchargeGroup
  @Getter @Setter private Double obsoleteSurcharge;

  // provide default values
  public Article() {
    catalogPriceFactor = 1;
    labelCount = 1;
    labelPerUnit = false;
  }

  @Override
  public String toString() {
    return Tools.optional(this::getName).orElse("ArtikelBase[" + super.toString() + "]");
  }

  @Override
  public Article clone() {
    try {
      return (Article) super.clone();
    } catch (CloneNotSupportedException e) {
      throw Tools.showUnexpectedErrorWarning(e);
    }
  }
}
