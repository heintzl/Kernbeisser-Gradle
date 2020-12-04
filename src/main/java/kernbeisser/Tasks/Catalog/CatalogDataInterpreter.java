package kernbeisser.Tasks.Catalog;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import javax.persistence.EntityManager;
import kernbeisser.DBEntities.Article;
import kernbeisser.DBEntities.Supplier;
import kernbeisser.DBEntities.SurchargeGroup;
import kernbeisser.Tasks.DTO.Catalog;
import kernbeisser.Tasks.DTO.KornkraftArticle;
import kernbeisser.Tasks.DTO.KornkraftGroup;
import kernbeisser.Useful.Tree;

public class CatalogDataInterpreter {
  public static HashMap<Long, KornkraftGroup> createNumberRefMap(Catalog catalog) {
    KornkraftArticle[] data = catalog.getData();
    HashMap<Long, KornkraftGroup> out = new HashMap<>(data.length);
    for (KornkraftArticle article : data) {
      KornkraftGroup[] groups = catalog.getBreadcrums().get(article.getInetwg());
      out.put(article.getArtnr(), groups[groups.length - 1]);
    }
    return out;
  }

  public static HashMap<Long, SurchargeGroup> createNumberRefMap(
      Catalog catalog, HashMap<KornkraftGroup, SurchargeGroup> groupSurchargeGroupHashMap) {
    KornkraftArticle[] data = catalog.getData();
    HashMap<Long, SurchargeGroup> out = new HashMap<>(data.length);
    for (KornkraftArticle article : data) {
      KornkraftGroup[] groups = catalog.getBreadcrums().get(article.getInetwg());
      out.put(article.getArtnr(), groupSurchargeGroupHashMap.get(groups[groups.length - 1]));
    }
    return out;
  }

  public static Tree<KornkraftGroup> extractGroupsTree(Catalog catalog) {
    Tree<KornkraftGroup> tree = new Tree<>();
    for (KornkraftGroup[] value : catalog.getBreadcrums().values()) {
      tree.put(value);
    }
    return tree;
  }

  public static HashMap<KornkraftGroup, SurchargeGroup> extractSurchargeGroups(
      Tree<KornkraftGroup> kornkraftGroupTree) {
    Supplier kk = Supplier.getKKSupplier();
    HashMap<KornkraftGroup, SurchargeGroup> out = new HashMap<>();
    kornkraftGroupTree.overAll(
        new KornkraftGroup(),
        (parent, value) -> {
          SurchargeGroup sg = new SurchargeGroup();
          sg.setName(value.getItext());
          sg.setSupplier(kk);
          sg.setParent(out.get(parent));
          out.put(value, sg);
        });
    return out;
  }

  public static HashMap<KornkraftGroup, SurchargeGroup> extractSurchargeGroups(
      Tree<KornkraftGroup> kornkraftGroupTree, EntityManager entityManager) {
    Supplier kk = Supplier.getKKSupplier();
    HashMap<KornkraftGroup, SurchargeGroup> out = new HashMap<>();
    kornkraftGroupTree.overAll(
        new KornkraftGroup(),
        (parent, value) -> {
          SurchargeGroup sg = new SurchargeGroup();
          sg.setName(value.getItext());
          sg.setSupplier(kk);
          sg.setParent(out.get(parent));
          entityManager.persist(sg);
          out.put(value, sg);
        });
    return out;
  }

  public static void linkArticles(
      List<Article> articleBases, HashMap<Long, SurchargeGroup> surchargeGroupHashMap) {
    for (Article current : articleBases) {
      SurchargeGroup ref = surchargeGroupHashMap.get((long) current.getSuppliersItemNumber());
      if (ref != null) current.setSurchargeGroup(ref);
    }
  }

  public static void autoLinkArticle(List<Article> articleBases) {
    SurchargeGroup undef = SurchargeGroup.undefined();
    Article last = findNext(0, articleBases);
    Article next = last;
    articleBases.sort(Comparator.comparingInt(Article::getSuppliersItemNumber));
    for (Article articleBase : articleBases) {
      if (!articleBase.getSurchargeGroup().equals(undef)) last = articleBase;
    }
    for (int i = 0; i < articleBases.size(); i++) {
      Article current = articleBases.get(i);
      if (next.getSuppliersItemNumber() <= current.getSuppliersItemNumber())
        next = findNext(i + 1, articleBases);
      if (diff(current, next) > diff(current, last))
        current.setSurchargeGroup(last.getSurchargeGroup());
      else current.setSurchargeGroup(next.getSurchargeGroup());
    }
  }

  public static int diff(Article a, Article b) {
    return Math.abs(a.getSuppliersItemNumber() - b.getSuppliersItemNumber());
  }

  public static Article findNext(int offset, List<Article> articleBases) {
    SurchargeGroup undef = SurchargeGroup.undefined();
    for (int i = 0; i < articleBases.size(); i++) {
      Article current = articleBases.get((offset + i) % articleBases.size());
      if (!current.getSurchargeGroup().equals(undef)) {
        return current;
      }
    }
    throw new UnsupportedOperationException(
        "cannot auto link articles without any set surcharge group");
  }
}
