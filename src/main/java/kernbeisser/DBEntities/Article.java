package kernbeisser.DBEntities;


import kernbeisser.DBConnection.DBConnection;
import kernbeisser.Enums.ContainerDefinition;
import kernbeisser.Enums.Cooling;
import kernbeisser.Enums.PermissionKey;
import kernbeisser.Security.Key;
import kernbeisser.Security.Proxy;
import kernbeisser.Useful.Tools;
import lombok.*;

import javax.persistence.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity
@Table
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Article extends ArticleBase{
    @Column(unique = true)
    @Getter(onMethod_= {@Key(PermissionKey.ARTICLE_KB_NUMBER_READ)})
    @Setter(onMethod_= {@Key(PermissionKey.ARTICLE_KB_NUMBER_WRITE)})
    private int kbNumber;

    @Column
    @Getter(onMethod_= {@Key(PermissionKey.ARTICLE_SURCHARGE_READ)})
    @Setter(onMethod_= {@Key(PermissionKey.ARTICLE_SURCHARGE_WRITE)})
    private double surcharge;

    @ManyToOne
    @JoinColumn(name = "priceListId")
    @Getter(onMethod_= {@Key(PermissionKey.ARTICLE_PRICE_LIST_READ)})
    @Setter(onMethod_= {@Key(PermissionKey.ARTICLE_PRICE_LIST_WRITE)})
    private PriceList priceList;

    @Column
    @Getter(onMethod_= {@Key(PermissionKey.ARTICLE_CONTAINER_DEF_READ)})
    @Setter(onMethod_= {@Key(PermissionKey.ARTICLE_CONTAINER_DEF_WRITE)})
    private ContainerDefinition containerDef;

    @Column
    @Getter(onMethod_= {@Key(PermissionKey.ARTICLE_SUPPLIERS_ITEM_NUMBER_READ)})
    @Setter(onMethod_= {@Key(PermissionKey.ARTICLE_SUPPLIERS_ITEM_NUMBER_WRITE)})
    private int suppliersItemNumber;

    @Column
    @Getter(onMethod_= {@Key(PermissionKey.ARTICLE_WEIGH_ABLE_READ)})
    @Setter(onMethod_= {@Key(PermissionKey.ARTICLE_WEIGH_ABLE_WRITE)})
    private boolean weighable;

    @Column
    @Getter(onMethod_= {@Key(PermissionKey.ARTICLE_LISTED_READ)})
    @Setter(onMethod_= {@Key(PermissionKey.ARTICLE_LISTED_WRITE)})
    private boolean listed;

    @Column
    @Getter(onMethod_= {@Key(PermissionKey.ARTICLE_SHOW_IN_SHOP_READ)})
    @Setter(onMethod_= {@Key(PermissionKey.ARTICLE_SHOW_IN_SHOP_WRITE)})
    private boolean showInShop;

    @Column
    @Getter(onMethod_= {@Key(PermissionKey.ARTICLE_DELETED_READ)})
    @Setter(onMethod_= {@Key(PermissionKey.ARTICLE_DELETED_WRITE)})
    private boolean deleted;

    @Column
    @Getter(onMethod_= {@Key(PermissionKey.ARTICLE_PRINT_AGAIN_READ)})
    @Setter(onMethod_= {@Key(PermissionKey.ARTICLE_PRINT_AGAIN_WRITE)})
    private boolean printAgain;

    @Column
    @Getter(onMethod_= {@Key(PermissionKey.ARTICLE_DELETE_ALLOWED_READ)})
    @Setter(onMethod_= {@Key(PermissionKey.ARTICLE_DELETE_ALLOWED_WRITE)})
    private boolean deleteAllowed;

    @Column
    @Getter(onMethod_= {@Key(PermissionKey.ARTICLE_LOSS_READ)})
    @Setter(onMethod_= {@Key(PermissionKey.ARTICLE_LOSS_WRITE)})
    private int loss;

    @Column
    @Getter(onMethod_= {@Key(PermissionKey.ARTICLE_INFO_READ)})
    @Setter(onMethod_= {@Key(PermissionKey.ARTICLE_INFO_WRITE)})
    private String info;

    @Column
    @Getter(onMethod_= {@Key(PermissionKey.ARTICLE_SOLD_READ)})
    @Setter(onMethod_= {@Key(PermissionKey.ARTICLE_SOLD_WRITE)})
    private int sold;

    @JoinColumn
    @OneToMany(fetch = FetchType.EAGER)
    @Getter(onMethod_= {@Key(PermissionKey.ARTICLE_OFFERS_READ)})
    @Setter(onMethod_= {@Key(PermissionKey.ARTICLE_OFFERS_WRITE)})
    private List<Offer> offers = new ArrayList<>();

    @Column
    @Getter(onMethod_= {@Key(PermissionKey.ARTICLE_DELIVERED_READ)})
    @Setter(onMethod_= {@Key(PermissionKey.ARTICLE_DELIVERED_WRITE)})
    private int delivered;

    @Column
    @Getter(onMethod_= {@Key(PermissionKey.ARTICLE_INTAKE_READ)})
    @Setter(onMethod_= {@Key(PermissionKey.ARTICLE_INTAKE_WRITE)})
    private Instant intake;

    @Column
    @Getter(onMethod_= {@Key(PermissionKey.ARTICLE_LAST_DELIVERY_READ)})
    @Setter(onMethod_= {@Key(PermissionKey.ARTICLE_LAST_DELIVERY_WRITE)})
    private Instant lastDelivery;

    @Column
    @Getter(onMethod_= {@Key(PermissionKey.ARTICLE_DELETED_DATE_READ)})
    @Setter(onMethod_= {@Key(PermissionKey.ARTICLE_DELETED_DATE_WRITE)})
    private Instant deletedDate;

    @Column
    @Getter(onMethod_= {@Key(PermissionKey.ARTICLE_COOLING_READ)})
    @Setter(onMethod_= {@Key(PermissionKey.ARTICLE_COOLING_WRITE)})
    private Cooling cooling;

    @Column
    @Getter(onMethod_= {@Key(PermissionKey.ARTICLE_COVERED_INTAKE_READ)})
    @Setter(onMethod_= {@Key(PermissionKey.ARTICLE_COVERED_INTAKE_WRITE)})
    private boolean coveredIntake;

    public static List<Article> getAll(String condition) {
        return Tools.getAll(Article.class, condition);
    }

    public static Collection<Article> defaultSearch(String search, int maxResults) {
        EntityManager em = DBConnection.getEntityManager();
        Collection<Article> out = em.createQuery(
                "select i from Article i where kbNumber = :n or suppliersItemNumber = :n or i.supplier.shortName like :s or i.supplier.name like :s or UPPER(i.name) like :ds or mod(barcode, 10000) = :n or UPPER( i.priceList.name) like :u order by i.name asc",
                Article.class
        )
                                    .setParameter("n", Tools.tryParseInteger(search))
                                    .setParameter("s", search + "%")
                                    .setParameter("ds",(search.length()>3 ? "%"+search+"%" : search+"%").toUpperCase())
                                    .setParameter("u",search.toUpperCase()+"%")
                                    .setMaxResults(maxResults)
                                    .getResultList();
        em.close();
        return Proxy.getSecureInstances(out);
    }

    public static Article getByKbNumber(int kbNumber) {
        EntityManager em = DBConnection.getEntityManager();
        try {
            return em.createQuery("select i from Article i where kbNumber = :n", Article.class)
                     .setParameter("n", kbNumber)
                     .getSingleResult();
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
    }

    public static Article getBySuppliersItemNumber(int suppliersNumber) {
        EntityManager em = DBConnection.getEntityManager();
        try {
            return em.createQuery("select i from Article i where suppliersItemNumber = :n", Article.class)
                     .setParameter("n", suppliersNumber)
                     .getSingleResult();
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
    }

    public static Article getByBarcode(long barcode) {
        EntityManager em = DBConnection.getEntityManager();
        try {
            return em.createQuery("select i from Article i where barcode = :n", Article.class)
                     .setParameter("n", barcode)
                     .getSingleResult();
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
    }


    public SurchargeTable getSurchargeTable() {
        //TODO really expensive!
        EntityManager em = DBConnection.getEntityManager();
        try {
            return em.createQuery(
                    "select st from SurchargeTable st where st.supplier.id = :supplier and st.from <= :number and st.to >= :number",
                    SurchargeTable.class)
                     .setParameter("supplier", getSupplier() != null ? getSupplier().getSid() : -1)
                     .setParameter("number", getSuppliersItemNumber())
                     .setMaxResults(1)
                     .getSingleResult();
        } catch (NoResultException e) {
            return SurchargeTable.DEFAULT;
        }
    }


    @Override
    public int hashCode() {
        return getId() + getName().hashCode();
    }

    @Override
    public String toString() {
        return Tools.decide(this::getName,"ArtikelBase["+super.toString()+"]");
    }

    public Article unwrapProxy(){
        if(!Proxy.isProxyInstance(this))return this;
        Article out = new Article();
        Tools.copyInto(this,out);
        return out;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        Article article = (Article) o;
        return kbNumber == article.kbNumber &&
               Double.compare(article.surcharge, surcharge) == 0 &&
               suppliersItemNumber == article.suppliersItemNumber &&
               weighable == article.weighable &&
               listed == article.listed &&
               showInShop == article.showInShop &&
               deleted == article.deleted &&
               printAgain == article.printAgain &&
               deleteAllowed == article.deleteAllowed &&
               loss == article.loss &&
               sold == article.sold &&
               delivered == article.delivered &&
               coveredIntake == article.coveredIntake &&
               priceList.equals(article.priceList) &&
               containerDef == article.containerDef &&
               info.equals(article.info) &&
               offers.equals(article.offers) &&
               intake.equals(article.intake) &&
               lastDelivery.equals(article.lastDelivery) &&
               deletedDate.equals(article.deletedDate) &&
               cooling == article.cooling;
    }
}
