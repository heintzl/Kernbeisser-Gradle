package kernbeisser.DBEntities;

import java.io.Serializable;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NoResultException;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import kernbeisser.CustomComponents.ObjectTree.Node;
import kernbeisser.DBConnection.DBConnection;
import kernbeisser.Enums.PermissionKey;
import kernbeisser.Forms.ObjectForm.Components.Source;
import kernbeisser.Security.Key;
import kernbeisser.Useful.Tools;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.envers.Audited;

@Entity
@Table(name = "PriceLists")
@EqualsAndHashCode(doNotUseGetters = true)
@Audited
@NoArgsConstructor
public class PriceList implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(updatable = false, insertable = false, nullable = false)
  @Getter(onMethod_ = {@Key(PermissionKey.PRICE_LIST_ID_READ)})
  @Setter(onMethod_ = {@Key(PermissionKey.PRICE_LIST_ID_WRITE)})
  private int id;

  @PrimaryKeyJoinColumn
  @Column(nullable = false, unique = true)
  @Getter(onMethod_ = {@Key(PermissionKey.PRICE_LIST_NAME_READ)})
  @Setter(onMethod_ = {@Key(PermissionKey.PRICE_LIST_NAME_WRITE)})
  private String name;

  @ManyToOne
  @JoinColumn
  @Getter(onMethod_ = {@Key(PermissionKey.PRICE_LIST_SUPER_PRICE_LIST_READ)})
  @Setter(onMethod_ = {@Key(PermissionKey.PRICE_LIST_SUPER_PRICE_LIST_WRITE)})
  private PriceList superPriceList;

  @Column @Getter @Setter private Instant lastPrint;

  @UpdateTimestamp
  @Getter(onMethod_ = {@Key(PermissionKey.PRICE_LIST_UPDATE_DATE_READ)})
  @Setter(onMethod_ = {@Key(PermissionKey.PRICE_LIST_UPDATE_DATE_WRITE)})
  private Instant updateDate;

  @CreationTimestamp
  @Getter(onMethod_ = {@Key(PermissionKey.PRICE_LIST_ID_READ)})
  @Setter(onMethod_ = {@Key(PermissionKey.PRICE_LIST_ID_WRITE)})
  private Instant createDate;

  public PriceList(String name) {
    this.name = name;
  }

  public static void savePriceList(String name) {
    savePriceList(name, null);
  }

  public static void savePriceList(String priceListName, PriceList superPriceList) {
    PriceList p = new PriceList(priceListName);
    p.setSuperPriceList(superPriceList);
    Tools.runInSession(em -> em.persist(p));
  }

  public static void deletePriceList(PriceList toDelete) {
    Tools.runInSession(em -> em.remove(em.contains(toDelete) ? toDelete : em.merge(toDelete)));
  }

  private static PriceList getPriceList(String name) throws NoResultException {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    @Cleanup(value = "commit")
    EntityTransaction et = em.getTransaction();
    et.begin();
    return em.createQuery(
            "select p from PriceList p where name like '" + name + "'", PriceList.class)
        .getSingleResult();
  }

  private static PriceList getOrCreate(String name) {
    try {
      return getPriceList(name);
    } catch (NoResultException e) {
      savePriceList(name);
      return getPriceList(name);
    }
  }

  public static PriceList getSingleItemPriceList() {
    return getOrCreate("Einzelartikel");
  }

  public static PriceList getCoveredIntakePriceList() {
    return getOrCreate("Verdeckte Aufnahme");
  }

  public static List<PriceList> getAll(String condition) {
    return Tools.getAll(PriceList.class, condition);
  }

  public static Collection<PriceList> getAllHeadPriceLists() {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    @Cleanup(value = "commit")
    EntityTransaction et = em.getTransaction();
    et.begin();
    return em.createQuery(
            "select p from PriceList p where p.superPriceList = null", PriceList.class)
        .getResultList();
  }

  public List<PriceList> getAllPriceLists() {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    @Cleanup(value = "commit")
    EntityTransaction et = em.getTransaction();
    et.begin();
    return em.createQuery(
            "select p from PriceList p where p.superPriceList = "
                + getId()
                + " order by p.name asc",
            PriceList.class)
        .getResultList();
  }

  @Override
  public String toString() {
    return Tools.optional(this::getName).orElse("Preisliste[" + id + "]");
  }

  public List<Article> getAllArticles() {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    @Cleanup(value = "commit")
    EntityTransaction et = em.getTransaction();
    et.begin();
    return getAllArticles(em);
  }

  public List<Article> getAllArticles(EntityManager em) {
    return em.createQuery("select a from Article a where a.priceList = :p", Article.class)
        .setParameter("p", this)
        .getResultList();
  }

  public static Node<PriceList> asNode(Node<PriceList> parent, PriceList priceList) {
    return new Node<PriceList>() {
      @Override
      public String toString() {
        return getValue().name;
      }

      @Override
      public PriceList getValue() {
        return priceList;
      }

      @Override
      public List<Node<PriceList>> getNodes() {
        return Tools.transform(priceList.getAllPriceLists(), p -> asNode(this, p));
      }

      @Override
      public Node<? extends PriceList> getParent() {
        return parent;
      }
    };
  }

  public static Node<PriceList> getPriceListsAsNode() {
    return new Node<PriceList>() {
      @Override
      public PriceList getValue() {
        return new PriceList("Preislisten");
      }

      @Override
      public String toString() {
        return "Preislisten";
      }

      @Override
      public List<Node<PriceList>> getNodes() {
        return Tools.transform(PriceList.getAllHeadPriceLists(), e -> PriceList.asNode(this, e));
      }

      @Override
      public Node<? extends PriceList> getParent() {
        return null;
      }
    };
  }

  public static Source<PriceList> onlyWithContent() {
    return () -> {
      @Cleanup EntityManager em = DBConnection.getEntityManager();
      @Cleanup("commit")
      EntityTransaction et = em.getTransaction();
      et.begin();
      return em.createQuery(
              "select p from PriceList p where p in (select a.priceList from Article a)",
              PriceList.class)
          .getResultList();
    };
  }
}
